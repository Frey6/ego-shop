package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.entity.SysRole;
import com.zy.entity.SysRoleMenu;
import com.zy.entity.SysUserRole;
import com.zy.mapper.SysRoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.mapper.SysRoleMenuMapper;
import com.zy.mapper.SysUserMapper;
import com.zy.mapper.SysUserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {


  @Autowired
  private SysRoleMenuMapper sysRoleMenuMapper;
  @Autowired
  private SysUserMapper sysUserMapper;
  @Autowired
   private SysUserRoleMapper sysUserRoleMapper;
  @Autowired
   private   RedisTemplate redisTemplate;
  @Override
  @Transactional
  public boolean save(SysRole sysRole) {
    log.info("新增一个角色%s",sysRole.getRoleName());
    sysRole.setCreateTime(new Date());
    boolean save = super.save(sysRole);
    if (save){
      @NotEmpty List<Long> menuIdList = sysRole.getMenuIdList();
      for (Long menuId : menuIdList) {
        SysRoleMenu sysRoleMenu = new SysRoleMenu();
        sysRoleMenu.setMenuId(menuId);
        sysRoleMenu.setRoleId(sysRole.getRoleId());
        sysRoleMenuMapper.insert(sysRoleMenu);
      }
    }
    return save;
  }

  @Override
  public SysRole getById(Serializable id) {
    SysRole sysRole = super.getById(id);
    List<Object> objects = sysRoleMenuMapper.selectObjs(
      new LambdaQueryWrapper<SysRoleMenu>().
        select(SysRoleMenu::getMenuId).eq(SysRoleMenu::getRoleId, id)
    );
    if (objects!=null&&!objects.isEmpty()){
      ArrayList<Long> longs = new ArrayList<>(objects.size());
      objects.forEach((idObj)->longs.add((Long) idObj));
      sysRole.setMenuIdList(longs);
    }
    return  sysRole;
  }

  @Transactional
  @Override
  public boolean removeByIds(Collection<? extends Serializable> idList) {
    log.info("删除角色id%s的值",idList);
    Integer count = sysUserRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, idList));
    if (count>0){
      throw new IllegalArgumentException("当前用户正在使用中，不能删除");
    }
    return super.removeByIds(idList);
  }

  @Transactional
  @Override
  public boolean updateById(SysRole entity) {
    log.info("修改id为%s的数据",entity.getRoleId());
    entity.setCreateTime(new Date());
    boolean flag = super.updateById(entity);
    if(flag){ // 处理中间表的关系？ // 删除旧的值，新增新的值
      // 1 删除旧值
      sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId,entity.getRoleId())) ;
      // 2 新增新的值
      @NotEmpty List<Long> menuIdList = entity.getMenuIdList();
      for (Long menuId : menuIdList) {
        SysRoleMenu sysRoleMenu = new SysRoleMenu();
        sysRoleMenu.setRoleId(entity.getRoleId());
        sysRoleMenu.setMenuId(menuId);
        sysRoleMenuMapper.insert(sysRoleMenu) ;
      }
    }
    // 我们修改了角色，或者是角色里面的菜单，刚好有人在使用这个角色->可能导致该该用户的菜单，权限发现变化，我们一般而已，需要重新查询一次
//         1 我们的菜单到底是怎么查询的？ 菜单数据，前端用户保存后，只要不刷新页面，它使用的就是旧的值
    // 2 我们的权限到底是怎么验证的？只要前端用户不刷新页面，它的按钮的显示和隐藏，使用的也是旧的值
    // 若要让用户重新刷新页面，就必须踢出它？-> 只要我们给正在访问我们的用户，抛出一个401 的异常，他会被里面的踢出

    // 现在我们的权限数据和登录数据都保存在redis 里面，若我们把当前所有受影响的用户从redis 里面删除，那就没有任何问题，我们可以查询出当前受影响的用户的id
    List<Object> userIds = sysUserRoleMapper.selectObjs(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, entity.getRoleId()));
    // 这些用户怎么去在登录一次？？ 该不该让改用户去重新登录？---> 当这个角色修改了，但是没有影响到当前用户的功能时，
    // 我们不需要让他登录，当时当该角色的修改影响到该用户时，我们让他重新登录
    // 影响这个用户-> 该用户可能没有权限了，我们之间把该用户的所有权限都移除，这样的话，他就不得不重新登录一下
    // 用户的权限数据保存在redis 里面，只要移除redis 里面的权限相关的值，就ok了
    if(userIds!=null && !userIds.isEmpty()){
      userIds.forEach((userId)->{
        redisTemplate.delete("AUTH_PERMIS:"+userId) ;// 清除受影响用户的权限后，用户若点击一个功能，必然触发401
      });

    }
    return flag ;
  }

}
