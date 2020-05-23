package com.zy.service;

import com.zy.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 产品类目 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface CategoryService extends IService<Category> {

  List<Category> listAllParent();

  /**
   * 加载所有的二级菜单
   * @return
   */
  List<Category> loadAllSubCategorys();
}
