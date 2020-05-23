package com.zy.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.model.ProdSolr;
import com.zy.service.SearchService;
import org.apache.dubbo.config.annotation.Service;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SearchServiceImpl  implements SearchService {

  @Autowired
  private SolrClient solrClient ;

  /**
   * tagList:6
   * 通过商品的活动标签来搜索商品
   * @param tagId
   * @param sort
   * @param page
   * @return
   */
  @Override
  public Page<ProdSolr> findByTagId(Long tagId, Integer sort, Page<ProdSolr> page) {
    SolrQuery solrQuery = new SolrQuery("tagList:" + tagId);
    return query(page, sort,solrQuery);
  }

  private Page<ProdSolr> query(Page<ProdSolr> page,  Integer sort,SolrQuery solrQuery) {
    builderSolrQuery(sort, page, solrQuery); // 添加分页，排序，以及删除的过滤查询
    try {
      QueryResponse query = solrClient.query(solrQuery);
      SolrDocumentList results = query.getResults();
      if(results!=null && !results.isEmpty()){
        page.setTotal(results.getNumFound()) ; // 总条数
        List<ProdSolr> prodSolrList = docs2ProdSolr(results);
        page.setRecords(prodSolrList) ;
      }
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return page;
  }

  private List<ProdSolr> docs2ProdSolr(SolrDocumentList results) {
    List<ProdSolr> prodSolrList = new ArrayList<>(results.size());
    for (SolrDocument result : results) {
      prodSolrList.add(docs2ProdSolr(result)) ;
    }
    return prodSolrList ;
  }
  private ProdSolr docs2ProdSolr(SolrDocument doc){
    ProdSolr prodSolr = ProdSolr.builder() // 返回一个ProdSolr.builder() 构建者
      .brief(doc.getFieldValue("brief").toString())
      .categoryId(Long.valueOf(doc.getFieldValue("categoryId").toString()))
      .pic(doc.getFieldValue("pic").toString())
      .positiveRating(new BigDecimal(doc.getFieldValue("positiveRating").toString()))
      .praiseNumber(Long.valueOf(doc.getFieldValue("praiseNumber").toString()))
      .prodId(Long.valueOf(doc.getFieldValue("id").toString()))
          .prodName(doc.getFieldValue("prodName").toString())
          .price(new BigDecimal(doc.getFieldValue("prodPrice").toString()))
          .soldNum(Long.valueOf(doc.getFieldValue("soldNum").toString()))
          .tagList((List<Long>)doc.getFieldValue("tagList"))
          .status(Integer.valueOf(doc.getFieldValue("status").toString()))
          .totalStocks(Long.valueOf(doc.getFieldValue("totalStocks").toString()))
          .build();
    return prodSolr ;
  }

  private void builderSolrQuery(Integer sort, Page<ProdSolr> page, SolrQuery solrQuery) {
    // 分页
    solrQuery.setStart((int) ((page.getCurrent()-1)*page.getSize()));
    solrQuery.setRows((int) page.getSize()) ;

    if(sort!=null){ // 需要排序
      switch (sort){
        case 0 : // 综合的排序
          solrQuery.addSort("praiseNumber",SolrQuery.ORDER.desc) ;
          break ;
        case 1 : // 销量排序
          solrQuery.addSort("soldNum", SolrQuery.ORDER.desc) ;
          break ;
        case 2: // 价格排序
          solrQuery.addSort("prodPrice", SolrQuery.ORDER.asc) ;
          break ;
        default:
          throw new RuntimeException("排序方法不允许!") ;
      }
    }

    // 现在我们在prodSolr 里面添加了一个status ，该状态可能代表有的商品下降了或者被删除，我们应该不让它显示
    solrQuery.addFilterQuery("status:1");
  }

  /**
   * categoryId:x
   * 通过商品的分类id 来搜索商品
   * @param catId
   * @param sort
   * @param page
   * @return
   */
  @Override
  public Page<ProdSolr> findByCatId(Long catId, Integer sort, Page<ProdSolr> page) {
    SolrQuery solrQuery = new SolrQuery("categoryId:" + catId);
    return query(page, sort,solrQuery);
  }

  /**
   * keyword:x
   * 通过关键字来搜索商品
   * @param keyword
   * @param sort
   * @param page
   * @return
   */
  @Override
  public Page<ProdSolr> search(String keyword, Integer sort, Page<ProdSolr> page) {
    SolrQuery solrQuery = new SolrQuery("keyword:" + keyword);
    return query(page, sort,solrQuery);
  }

  @Override
  public List<ProdSolr> findByProdIds(List<Long> prodIdList) {
    if(prodIdList==null || prodIdList.isEmpty()){
      return Collections.emptyList() ;
    }
    List<String> ids = new ArrayList<>(prodIdList.size());
    for (Long aLong : prodIdList) {
      ids.add(String.valueOf(aLong)) ;
    }
    try {
      SolrDocumentList results = solrClient.getById(ids);
      if(results!=null && !results.isEmpty()){
        List<ProdSolr> prodSolrList = docs2ProdSolr(results);
        return prodSolrList ;
      }
    } catch (SolrServerException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Collections.emptyList() ;
  }

}
