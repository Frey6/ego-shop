package com.zy.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.entity.SysRole;
import com.zy.entity.SysRoleMenu;
import com.zy.entity.SysUserRole;
import com.zy.mapper.SysRoleMapper;
import com.zy.mapper.SysRoleMenuMapper;
import com.zy.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 角色与菜单对应关系 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
@Slf4j
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

  @Autowired
  private SysRoleMapper sysRoleMapper;

  @Override
  public IPage<SysRole> findByPage(Page<SysRole> page, SysRole sysRole) {
    page.addOrder(OrderItem.desc("create_time"));
    IPage<SysRole> sysRoleIPage = sysRoleMapper.selectPage(page, new LambdaQueryWrapper<SysRole>().
      like(
      StringUtils.hasText(sysRole.getRoleName()),
        t -> {
          return sysRole.getRoleName();
        },
      sysRole.getRoleName()
    ));
    return sysRoleIPage;
  }


}
