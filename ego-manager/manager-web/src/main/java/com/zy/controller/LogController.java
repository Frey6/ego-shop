package com.zy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.SysLog;
import com.zy.mapper.SysLogMapper;
import com.zy.service.impl.SysLogService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/log")
public class LogController {
  @Autowired
  private SysLogService sysLogService;

  @GetMapping("/page")
  @ApiOperation("分页查询")
  @RequiresPermissions("sys:log:page")
  public ResponseEntity<IPage<SysLog>> page(Page<SysLog>  page, SysLog sysLog){
    page.addOrder(OrderItem.desc("create_date"));
    IPage<SysLog> byPage = sysLogService.findByPage(page, sysLog);
    return  ResponseEntity.ok(byPage);
  }
}
