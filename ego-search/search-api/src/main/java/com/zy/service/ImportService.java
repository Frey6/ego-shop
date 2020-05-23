package com.zy.service;

/**
 * 商品的导入
 */
public interface ImportService {

  /**
   * 全量导入
   *  项目一启动，把数据库里面商品数据导入到solr 里面
   */
  void importAll() ;

  /**
   * 增量的导入
   * 当用户新增一个商品，我们的导入服务会隔半个小时后会把该商品导入进去
   */
  void importUpdate() ;

  /**
   * 商品的库存信息，价格信息，销量信息，会随着用户的修改被立即改变
   */
  void quickImport() ;
}

