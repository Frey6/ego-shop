package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.SysUser;
import com.zy.entity.SysUserRole;
import com.zy.mapper.SysUserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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

@Autowired
private SysUserRoleMapper sysUserRoleMapper;

  @Override
  public boolean removeByIds(Collection<? extends Serializable> idList) {

    boolean b = super.removeByIds(idList);
    if (b){
     sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, idList));
      for (Serializable userId : idList) {
        redisTemplate.delete("AUTH_PERMIS"+userId);
      }
    }
    return b;
  }

  @Override
  public boolean removeById(Serializable id) {
    return super.removeById(id);
  }

  @Autowired
private RedisTemplate redisTemplate;

  @Override
  public SysUser findUserByusername(String username) {
    if (!StringUtils.hasText(username)){
      throw new RuntimeException("用户名不能为空");
    }
    LambdaQueryWrapper<SysUser> eq = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username);
    return sysUserMapper.selectOne(eq);
  }

  @Override
  @Transactional
  public boolean updateById(SysUser entity) {
    /**
     * 怎么修改一个用户
     */
    boolean flag = super.updateById(entity);
    if(flag){ // 修改成功了，我们需要处理该用户的登录状态和该用户的角色
      // 对于角色的修改，我们是先删除，后添加
      sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().
        eq(SysUserRole::getUserId,entity.getUserId()));
      List<Long> roleIdList = entity.getRoleIdList();
      for (Long roleId : roleIdList) {
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(entity.getUserId());
        sysUserRole.setRoleId(roleId);
        sysUserRoleMapper.insert(sysUserRole) ;
      }
      // 让改用户退出登录，只要情况改用户的权限， 改用户，只要一访问服务器，里面服务器会抛一个401
      redisTemplate.delete("AUTH_PERMIS:"+entity.getUserId()) ;
    }
    return flag ;
  }


  @Override
  public SysUser getById(Serializable id) {

    SysUser sysUser = super.getById(id);
    List<Object> objects = sysUserRoleMapper.selectObjs(
      new LambdaQueryWrapper<SysUserRole>().
        select(
          SysUserRole::getRoleId
        ).eq(SysUserRole::getUserId, id));
    if (objects!=null&&!objects.isEmpty()){
      ArrayList<Long> roleIds = new ArrayList<>(objects.size());
      objects.forEach((role)->roleIds.add((Long) role));
      sysUser.setRoleIdList(roleIds);
    }
  return  sysUser;
  }

  @Transactional
  @Override
  public boolean save(SysUser entity) {
    entity.setCreateTime(new Date());
    boolean save = super.save(entity);
    if (save){
      @NotEmpty List<Long> roleIdList = entity.getRoleIdList();
      for (Long roleId: roleIdList) {
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setRoleId(roleId);
        sysUserRole.setUserId(entity.getUserId());
        sysUserRoleMapper.insert(sysUserRole);
      }
    }
    return save;

  }

  @Override
  public IPage<SysUser> findByPage(Page<SysUser> page, SysUser sysUser) {
    page.addOrder(OrderItem.desc("create_time"));
    return sysUserMapper.selectPage(page,new LambdaQueryWrapper<SysUser>().
      like(StringUtils.hasText(sysUser.getUsername()),
        SysUser::getUsername,
        sysUser.getUsername()
    ));
  }
}
