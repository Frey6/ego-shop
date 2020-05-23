package com.zy.service;

import com.zy.entity.Sku;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 单品SKU表 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface SkuService extends IService<Sku> {

  /**
   * 扣减库存
   * @param skuStocks
   */
  void decrStocks(Map<Long, Integer> skuStocks);

  /**
   * 新增库存
   * @param skuStocks
   */
  void addStocks(Map<Long, Integer> skuStocks);
}
