package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.entity.SysUser;
import com.zy.mapper.SysUserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 系统用户 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
@Autowired
private  SysUserMapper sysUserMapper;

  @Override
  public SysUser findUserByusername(String username) {
    if (!StringUtils.hasText(username)){
      throw new RuntimeException("用户名不能为空");
    }
    LambdaQueryWrapper<SysUser> eq = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username);
    return sysUserMapper.selectOne(eq);
  }
}
