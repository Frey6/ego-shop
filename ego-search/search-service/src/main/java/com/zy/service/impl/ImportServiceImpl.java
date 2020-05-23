package com.zy.service.impl;

import ch.qos.logback.classic.pattern.MessageConverter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zy.entity.Prod;
import com.zy.service.ImportService;
import com.zy.service.ProdService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Scheduled;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ImportServiceImpl implements ImportService, ApplicationListener<ContextRefreshedEvent> , MessageListener {

  @Reference(check = false)
  private ProdService prodService;

  @Value("${import.size}")
  private Integer size;

  @Autowired
  private SolrClient solrClient ;

  private Date t1;

   private ExecutorService executorService = Executors.newFixedThreadPool(8);

//   @Autowired
//   private MessageConverter messageConverter;
//  private ExecutorService threadPool = Executors.newFixedThreadPool(10);

//  private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

  /**
   * 项目一启动，我们就导入
   */
  @Override
  public void importAll() {

    long total = prodService.getTotal(null, null);
    long totalPage = total % size == 0 ? total / size : (total / size + 1);
    for (int i = 1; i <= totalPage; i++) {
      final int current = i;
      executorService.submit(() -> {
        System.out.println("执行第"+ current +"个线程");
        import2Solr(current, size, null, null);
        System.out.println("执行第"+ current +"个线程成功");
      });
    }
    t1 = new Date();
  }

  /**
   * 导入第current，每页size 条
   *
   * @param current
   * @param size
   */
  private void import2Solr(int current, Integer size, Date t1, Date t2) {
    IPage<Prod> prodPage = prodService.selectPage(current,size,t1,t2);
    if(prodPage.getTotal()>=0 && !prodPage.getRecords().isEmpty()){
      System.out.println("开始导入第"+current+"页，每页的数据为"+size);
      List<Prod> records = prodPage.getRecords();
      List<SolrInputDocument> docs =  prod2Doc(records);
      if(docs!=null && !docs.isEmpty()){
        try {
          solrClient.add(docs) ;
          solrClient.commit() ;
          System.out.println("导入第"+current+"页成功，每页的数据为"+size);
        } catch (SolrServerException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

    }
  }

  /**
   * 把prod 的集合转换为doc的集合
   * @param records
   * @return
   */
  private List<SolrInputDocument> prod2Doc(List<Prod> records) {
    if(records==null || records.isEmpty()){
      return null ;
    }
    List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>(records.size());
    for (Prod record : records) {
      SolrInputDocument doc = prod2Doc(record);
      docs.add(doc) ;
    }
    return docs ;
  }

  private SolrInputDocument prod2Doc(Prod prod){
    // doc 要以solr 为标准，它里面的字段，由solr 决定
    SolrInputDocument doc = new SolrInputDocument();
    doc.setField("id",prod.getProdId().toString());
    doc.setField("prodName",prod.getProdName());
    double prodPrice1= prod.getPrice().doubleValue();
    doc.setField("prodPrice",prodPrice1);
    long soldNum1 = prod.getSoldNum().longValue();
    Integer j = prod.getSoldNum() + 1;
    doc.setField("soldNum",prod.getSoldNum()==null? 0l:j.longValue());
    doc.setField("brief",prod.getBrief());
    @NotBlank String pic = prod.getPic();
    doc.setField("pic",pic);
    doc.setField("status",prod.getStatus()==null ? 1:prod.getStatus());
    Integer i = prod.getTotalStocks()+1;
    doc.setField("totalStocks",prod.getTotalStocks()==null ? 0L :i.longValue() );
    doc.setField("categoryId",prod.getCategoryId());
    doc.setField("tagList",(prod.getTagList()==null || prod.getTagList().isEmpty()) ? Collections.emptyList() : prod.getTagList());
    doc.setField("praiseNumber",prod.getPraiseNumber());
    doc.setField("positiveRating",prod.getPositiveRating().doubleValue());
    return  doc ;
  }

  /**
   * 项目运行一段时间，导入新增的，修改的数据，以及删除的数据
   */
  @Scheduled(initialDelay = 10 * 1000, fixedRate =10 * 1000)
  @Override
  public void importUpdate() {
    Date t2 = new Date();
    Integer total = prodService.getTotal(t1,t2);
    long totalPage = total % size == 0 ? total / size : (total / size + 1);
    // 让我们的子线程执行完毕后，主线程在往下走
    CountDownLatch countDownLatch = new CountDownLatch((int)totalPage) ; // 线程计数器
    for (int i = 1; i <= totalPage; i++) {
      final int current = i;
      executorService.submit(() -> {
        import2Solr(current, size, t1, t2);
        countDownLatch.countDown(); // 每执行完一次，就-1
      });
    }
    try {
      countDownLatch.await(30, TimeUnit.SECONDS) ; // 阻塞主线程，直到子线程执行完毕
      System.out.println("子线程执行完毕");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    t1 = t2; // 这里设置t1 = t2 可能会导致子线程还没有来得及运行，就把t2 的值赋值给t1 了导致 子线程里面t1=t2
  }


  /**
   * 商品的价格，库存。。 等等可能需要快速导入
   */
  @Override
  public void quickImport() {

  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    System.out.println("项目刚启动成功了");
    try {
      solrClient.deleteByQuery("*:*");
      solrClient.commit();
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    importAll(); // 全部导入
  }

  @JmsListener(destination = "prod.decr.queue")
  @Override
  public void onMessage(Message message) {
    ActiveMQTextMessage mqTextMessage = (ActiveMQTextMessage) message;
    String text = null;
    try {
      text = mqTextMessage.getText();
    } catch (JMSException e) {
      e.printStackTrace();
    }
    JSONObject jsonObject = JSONUtil.parseObj(text);
    jsonObject.forEach((prodId,stock)->{
      SolrDocument solrDocument = null;
      try {
        solrDocument = solrClient.getById(prodId.toString());
      } catch (SolrServerException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      if(solrDocument!=null){
        Long totalStocksSolr = Long.valueOf(solrDocument.getFirstValue("totalStocks").toString());
        Long currentStock = totalStocksSolr - Long.valueOf(stock.toString()) ;
        solrDocument.setField("totalStocks",currentStock);
        SolrInputDocument solrInputFields =  solrDocument2solrInputDocument(solrDocument) ;
        try {
          solrClient.add(solrInputFields) ;
          solrClient.commit() ;
        } catch (SolrServerException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private SolrInputDocument solrDocument2solrInputDocument(SolrDocument solrDocument) {
    SolrInputDocument solrInputFields = new SolrInputDocument();
    Set<String> keySet = solrDocument.keySet();
    for (String key : keySet) {
      solrInputFields.addField(key,solrDocument.getFieldValue(key));
    }
    return solrInputFields ;
  }
}


