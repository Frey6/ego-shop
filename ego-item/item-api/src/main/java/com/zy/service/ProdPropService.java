package com.zy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.ProdProp;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.entity.ProdPropValue;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface ProdPropService extends IService<ProdProp> {
  /**
   * 分页查询的规格管理
   * @param page
   * @param prodProp
   * @return
   */
  IPage<ProdProp> findByPage(Page<ProdProp> page, ProdProp prodProp);

  List<ProdPropValue> listSpecValue(Long id);
}
