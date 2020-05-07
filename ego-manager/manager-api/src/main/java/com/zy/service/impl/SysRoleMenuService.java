package com.zy.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.entity.SysRole;
import com.zy.entity.SysRoleMenu;

/**
 * <p>
 * 角色与菜单对应关系 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

  IPage<SysRole> findByPage(Page<SysRole> page, SysRole sysRole);


}
