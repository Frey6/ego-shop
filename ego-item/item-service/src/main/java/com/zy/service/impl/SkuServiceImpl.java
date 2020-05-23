package com.zy.service.impl;

import com.zy.entity.Sku;
import com.zy.mapper.SkuMapper;
import com.zy.service.SkuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 单品SKU表 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService{

  @Autowired
  private SkuMapper skuMapper ;

  @Override
  public void decrStocks(Map<Long, Integer> skuStocks) {
    stockOperation(skuStocks ,false);
  }

  @Override
  public void addStocks(Map<Long, Integer> skuStocks) {
    stockOperation(skuStocks ,true);
  }

  private void stockOperation(Map<Long, Integer> skuStocks,boolean isAdd) {
    if(skuStocks!=null && !skuStocks.isEmpty()){
      skuStocks.forEach((skuId,stocks)->{
        Sku sku = skuMapper.selectById(skuId);
        Integer dbStock = sku.getStocks();
        Integer currentStock = 0 ;
        if(isAdd){
          currentStock = dbStock + stocks ;
        }else{
          currentStock = dbStock - stocks ;
        }
        if(currentStock<0){
          throw  new IllegalArgumentException("库存不足") ;
        }else{
          sku.setStocks(currentStock);
          sku.setUpdateTime(new Date());
          skuMapper.updateById(sku) ;
        }
      });
    }
  }


}

