package com.zy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.ProdTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品分组表 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface  ProdTagService extends IService<ProdTag> {

  IPage<ProdTag> findByPage(Page<ProdTag> page, ProdTag prodTag);

  /**
   * 加载首页数据的标签
   * @return
   */
  List<ProdTag> loadIndexProdTag();
}
