package com.zy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.domain.ProdCommSurvey;
import com.zy.entity.Prod;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 商品 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface ProdService extends IService<Prod> {

  IPage<Prod> findByPage(Page<Prod> page, Prod prod);

  Prod getSimpleProd(Long relation);

  /**
   * 获取t1到t2时刻内crud的数据
   * @param t1
   * @param t2
   * @return
   */
  Integer getTotal(Date t1, Date t2);

  /**
   * 获取t1到t2时刻内修改的分页数据
   * @param current
   * @param size
   * @param t1
   * @param t2
   * @return
   */
  IPage<Prod> selectPage(int current, Integer size, Date t1, Date t2);

  ProdCommSurvey getProdCommSurvey(Long prodId);

  void decrStocks(Map<Long, Integer> prodStocks);

  void addStocks(Map<Long, Integer> prodStocks);
}
