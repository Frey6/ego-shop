package com.zy.test;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.SolrHttpClientBuilder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SolrTest {
  private static final String SOLR_URL ="http://111.229.65.208:8983/solr/ego" ;

  private static HttpSolrClient build = null;



  static{
     build = new HttpSolrClient.Builder(SOLR_URL).build();
  }

  public static void main(String[] args) {
    ExecutorService executorService = Executors.newFixedThreadPool(8);

//    for (int i=1;i<=2;i++){
////      int finalI = i;
////      executorService.submit(()->{
////        System.out.println("执行第"+ finalI +"个线程");
////        querySolr();
////        System.out.println("执行第"+ finalI +"个线程，成功");
////      });
////    }
   querySolr();
    System.out.println(build);

//    deleteSolr();
//    addSolr();
//    addEgo();
  }

  private static void querySolr() {
    SolrQuery solrQuery = new SolrQuery("*:*");
    try {
      QueryResponse query = build.query(solrQuery);
      SolrDocumentList results = query.getResults();
      System.out.println("总条数"+results.getNumFound());
      for (SolrDocument result : results) {
//        System.out.println(result);
      }
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private static void addEgo() {
    SolrInputDocument document = new SolrInputDocument();
    document.setField("id",91L);
    document.setField("prodName","fast");
    document.setField("prodPrice",new BigDecimal(0.0).doubleValue());
    Integer integer = new Integer(0);
    Integer i = integer + 1;
//    long j=i;
    long j = i.longValue();
   document.setField("soldNum",j);
    document.setField("brief","666");
    document.setField("pic","www.sdvfsger.com");
    document.setField("status",new Integer(1));
    document.setField("totalStocks",i);
    document.setField("categoryId",93L);
    document.setField("tagList", Collections.EMPTY_LIST);
    document.setField("praiseNumber",0L);
    document.setField("positiveRating",new BigDecimal(0.0).doubleValue());
    try {
      build.add(document);
      build.commit();
      System.out.println("ego-demo添加完成");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  private static void deleteSolr() {
    try {
      build.deleteById("1");
      build.deleteByQuery("*:*");
      build.commit();
      System.out.println("删除成功");
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void addSolr() {
    SolrInputDocument document = new SolrInputDocument();
//    String uuid = new UUID().toString();
    String uuid = UUID.randomUUID().toString();
    document.addField("id",uuid);

//    document.addField("_text_","导入测试");
    try {
      build.add(document);
      build.commit();
      System.out.println("导入成功");
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
