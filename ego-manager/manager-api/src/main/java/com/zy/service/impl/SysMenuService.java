package com.zy.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.entity.SysMenu;
import com.zy.vo.MenuAuthResult;

import java.util.List;

/**
 * <p>
 * 菜单管理 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface SysMenuService extends IService<SysMenu> {
  /**
   * 通过用户的id加载用户的菜单数据和权限数据
   * @param userId
   * @return
   */
  MenuAuthResult loadMenuAndAuth(Long userId);

  /**
   * 加载父菜单
   * @return
   */
  List<SysMenu> listParentList();
}
