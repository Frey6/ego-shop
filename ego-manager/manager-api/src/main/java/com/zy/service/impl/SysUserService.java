package com.zy.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.entity.SysUser;

/**
 * <p>
 * 系统用户 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface SysUserService extends IService<SysUser> {
  IPage<SysUser> findByPage(Page<SysUser> page, SysUser sysUser);

  SysUser findUserByusername(String username);
}
