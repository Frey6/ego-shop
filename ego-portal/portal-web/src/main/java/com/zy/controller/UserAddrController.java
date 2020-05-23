package com.zy.controller;


import com.zy.entity.Area;
import com.zy.entity.User;
import com.zy.entity.UserAddr;
import com.zy.service.AreaService;
import com.zy.service.UserAddrService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserAddrController
{
  @Reference(check=false)
  private UserAddrService service;
  @Reference(check=false)
  private AreaService areaService;

  @GetMapping({"/p/address/list"})
  @ApiOperation("分列查询列表")
  public ResponseEntity<List<UserAddr>> list()
  {
    User user = (User)SecurityUtils.getSubject().getPrincipal();
    List<UserAddr> userAddrs = this.service.findUserAdderss(user.getUserId());
    return ResponseEntity.ok(userAddrs);
  }

  @GetMapping({"/p/area/listByPid"})
  @ApiOperation("通过父id来查询子菜单")
  public ResponseEntity<List<Area>> listByPid(Long pid)
  {
    List<Area> areas = this.areaService.listByPid(pid);
    return ResponseEntity.ok(areas);
  }
//
  @PostMapping({"/p/address/addAddr"})
  @ApiOperation("新增一个UserAddr")
  public ResponseEntity<Void> save(@RequestBody @Validated UserAddr entity)
  {
    User user = (User)SecurityUtils.getSubject().getPrincipal();
    entity.setUserId(user.getUserId());
    this.service.save(entity);
    return ResponseEntity.ok().build();
  }

  @GetMapping({"/p/address/addrInfo/{id}"})
  @ApiOperation("查询UserAddr的详情")
  public ResponseEntity<UserAddr> info(@PathVariable("id") Long id)
  {
    UserAddr entity = (UserAddr)this.service.getById(id);
    return ResponseEntity.ok(entity);
  }
//
  @DeleteMapping({"/p/address/deleteAddr/{id}"})
  @ApiOperation("删除UserAddr")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id)
  {
    this.service.removeById(id);
    return ResponseEntity.ok().build();
  }

  @PutMapping({"/p/address/updateAddr"})
  @ApiOperation("根据id修改UserAddr")
  public ResponseEntity<Void> update(@RequestBody @Validated UserAddr entity)
  {
    User user = (User) SecurityUtils.getSubject().getPrincipal();
    entity.setUserId(user.getUserId());
    this.service.updateById(entity);
    return ResponseEntity.ok().build();
  }

  @PutMapping({"/p/address/defaultAddr/{id}"})
  public ResponseEntity<Void> setDefaultAddr(@PathVariable("id") Long id)
  {
    User user = (User)SecurityUtils.getSubject().getPrincipal();
    this.service.setDefaultAddr(user.getUserId(), id);
    return ResponseEntity.ok().build();
  }
}

