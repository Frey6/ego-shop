package com.zy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.model.ProdSolr;

import java.util.List;

/**
 * 搜索服务
 */
/**
 * 搜索服务
 */
public interface SearchService {

  /**
   * 根据标签的id 查询 商品
   * @param tagId
   * @param page
   * @return
   */
  Page<ProdSolr> findByTagId(Long tagId , Integer sort , Page<ProdSolr> page);

  /**
   * 根据分类的id 查询商品
   * @param catId
   * @param sort
   * @param page
   * @return
   */
  Page<ProdSolr> findByCatId(Long catId,Integer sort,Page<ProdSolr> page);

  /**
   * 通过关键字查询商品
   * @param keyword
   * @param sort
   * @param page
   * @return
   */
  Page<ProdSolr> search(String keyword,Integer sort,Page<ProdSolr> page);

  /**
   * 利用商品的ids来查询商品
   * @param records
   * @return
   */
  List<ProdSolr> findByProdIds(List<Long> records);
}
