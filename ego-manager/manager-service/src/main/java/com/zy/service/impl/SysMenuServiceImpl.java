package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zy.entity.SysMenu;
import com.zy.entity.SysRoleMenu;
import com.zy.entity.SysUserRole;
import com.zy.mapper.SysMenuMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.mapper.SysRoleMenuMapper;
import com.zy.mapper.SysUserRoleMapper;
import com.zy.vo.Element;
import com.zy.vo.MenuAuthResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单管理 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

  @Autowired
  private SysUserRoleMapper sysUserRoleMapper;

  @Autowired
  private SysRoleMenuMapper sysRoleMenuMapper;

  @Autowired
  private SysMenuMapper sysMenuMapper;

  @Autowired
  private RedisTemplate redisTemplate;

  private  void validateSysMenu(SysMenu entity,boolean isUpdate){
    switch (entity.getType()) {
      case 0: // 新增一个目录
        validateDicMenu(entity);
        break;
      case 1: // 新增一个菜单
        validateMenu(entity);
        break;
      case 2: // 新增一个按钮
        validateButton(entity);
        break;
      default: // 其他时
        throw new IllegalArgumentException("暂不支持其他类型的菜单");
    }
    checkName(entity,isUpdate);
  }

  @Override
  public boolean save(SysMenu entity) {
    log.info("新增一个菜单");
    validateSysMenu(entity,false);
    return super.save(entity);
  }

  /*
  校验一个按钮
   */
  private void validateButton(SysMenu entity) {
    //1 按钮的父亲不能为空
    // 2 按钮的权限不能为null
    // 3 按钮无需url
    // 4 在一个菜单里面，按钮的名称不能为重复
    if(entity.getParentId() == null || entity.getPerms()== null){
      throw  new IllegalArgumentException("按钮的父亲和权限不能为null") ;
    }
    entity.setUrl(null);
    // 1 在一个目录里面菜单的名称不能重复



  }

  /**
   * 校验一个菜单
   * @param entity
   */
  private void validateMenu(SysMenu entity) {
    // 0 菜单的父亲不能为null  // 2 菜单的路由不能为null
    if(entity.getParentId()==null || entity.getUrl()==null){
      throw new  IllegalArgumentException("菜单的父亲或路由不能为null") ;
    }



  }

  @Override
  public boolean updateById(SysMenu entity) {
    log.info("修改id为%s的数据",entity.getMenuId());
    SysMenu sysMenu = getById(entity.getMenuId());
    if (sysMenu==null||!sysMenu.getType().equals(entity.getType())){
      throw  new IllegalArgumentException("修改的数据不存在或者类型不匹配");
    }
    validateSysMenu(entity,true);
    return super.updateById(entity);
  }

  @Override
  public boolean removeById(Serializable id) {
    log.info("修改id为%s",id);
    Integer count = sysMenuMapper.selectCount(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
    if (count>0){
      throw  new IllegalArgumentException("当前菜单里面存在子菜单，不能删除");
    }
    //判断该菜单是否有角色在使用它，若有角色使用，则应该断开角色与菜单之间的关系
    Integer roleMenuCount = sysRoleMenuMapper.selectCount(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
    if (roleMenuCount>0){
      throw  new IllegalArgumentException("该菜单有角色在使用，请断开菜单与角色的关系，再删除改菜单");
    }

    return super.removeById(id);
  }

  private void checkName(SysMenu entity,boolean isUpdate) {
    // 1 在一个目录里面菜单的名称不能重
    Integer count = sysMenuMapper.selectCount(new LambdaQueryWrapper<SysMenu>().
      eq(SysMenu::getParentId, entity.getParentId()). // 父亲相同
      eq(SysMenu::getName, entity.getName()) // 名称相同
    );
    int HanderAddUpdate=isUpdate?1:0;
    if (count > HanderAddUpdate) {
      throw new IllegalArgumentException("该名称" + entity.getName() + "已经存在");
    }
  }

  /**
   * 校验一个目录
   * @param entity
   */
  private void validateDicMenu(SysMenu entity){
    // 1 目录没有父亲
    entity.setParentId(0L);
    entity.setUrl(null);
    entity.setPerms(null);
//    checkName(entity);
  }





  /**
   * 加载一个用户的菜单数据和权限数据
   * 认识表
   * user->role->menu
   * user_role  role_menu
   * 1 通过用户的id 查询用户所有的角色->List<Long> roleIds
   * 2 通过角色的id 查询菜单的id
   * 3 通过菜单的id 查询所有的菜单数据
   * 5 通过菜单数据拼接一个树结构
   *
   * @param userId
   * @return
   */
  @Override
  public MenuAuthResult loadMenuAndAuth(Long userId) {
    MenuAuthResult menuAuthResult = new MenuAuthResult();
    // 1 用户的id 查询角色
    List<Object> roleIds = sysUserRoleMapper.selectObjs(new LambdaQueryWrapper<SysUserRole>().select(
      SysUserRole::getRoleId
      ).
        eq(SysUserRole::getUserId, userId)
    );
    if (roleIds == null || roleIds.isEmpty()) {
      return menuAuthResult;
    }
    // 2 通过角色的id 查询菜单的id
    List<Object> menuIds = sysRoleMenuMapper.selectObjs(new LambdaQueryWrapper<SysRoleMenu>().select(
      SysRoleMenu::getMenuId
      ).in(SysRoleMenu::getRoleId, roleIds)
    );
    if (menuIds == null || menuIds.isEmpty()) {
      return menuAuthResult;
    }
    // 3 通过菜单的Id 查询菜单的数据
    List<SysMenu> sysMenus = sysMenuMapper.selectList(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getMenuId, menuIds));
    if (sysMenus == null || sysMenus.isEmpty()) {
      return menuAuthResult;
    }
    // sysMenus 既有菜单，有url 地址就是菜单，还要权限，有perms 的就是权限
    Set<Element> elements = new HashSet<>(); // 它里面存放的是权限
    List<SysMenu> allMenuData = new ArrayList<>();
    Set<String> permsCaches =new HashSet<>();//记录权限的,和Set《Elemet》有区别
    for (SysMenu sysMenu : sysMenus) {
      String perms = sysMenu.getPerms();
      if (StringUtils.hasText(perms)) { // 权限的数据
        String[] permsArray = perms.split(",");
        for (int i = 0; i < permsArray.length; i++) {
          String s = permsArray[i];
          permsCaches.add(s);
          elements.add(new Element(s)); // 现在set 集合里面是否能去重？可以，因为Element的equals的方法我重写；
        }
      }
      @NotNull Integer type = sysMenu.getType();
      if (type.equals(0)||type.equals(1)){
        allMenuData.add(sysMenu);
      }
    }
    redisTemplate.opsForValue().set("AUTH_PERMIS"+userId,permsCaches);
    menuAuthResult.setAuthorities(new ArrayList<>(elements));
    menuAuthResult.setMenuList(translateToTree(allMenuData));
    return menuAuthResult;
  }

  @Override
  public List<SysMenu> listParentList() {
    //
//    new QueryWrapper<SysMenu>().ne("type",2);
//    new LambdaQueryWrapper<>()不用记得属性，
    List<SysMenu> sysMenus = sysMenuMapper.selectList(
      new LambdaQueryWrapper<SysMenu>().
      ne(SysMenu::getType, 2));
    return sysMenus;
  }

  /**
   * 把list 集合转换为一个菜单数据
   * @param sysMenus
   * @return
   */
  public List<SysMenu> translateToTree(List<SysMenu> sysMenus){
    List<SysMenu> root = new ArrayList<>();

    for (SysMenu sysMenu : sysMenus) {
      if(sysMenu.getParentId().equals(0L)){ // 父节点id= 0
        root.add(sysMenu) ;// 一级的节点
      }
    }

    for (SysMenu sysMenu : root) { // 所有的一级节点
      List<SysMenu> subNodes = new ArrayList<>();
      for (SysMenu menu : sysMenus) {
        // 父亲== 我
        if(menu.getParentId().equals(sysMenu.getMenuId())){
          subNodes.add(menu);
        }
      }
      sysMenu.setList(subNodes); // 把所有的儿子放在里面
    }
    return  root  ;
  }
}

