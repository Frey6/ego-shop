package com.zy.controller.store;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.Transport;
import com.zy.service.TransportService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop/transport")
public class TransportController {

  @Reference(check = false)
  private TransportService service;

  @GetMapping("/page")
  @RequiresPermissions("shop:transport:page")
  @ApiOperation("分页查询Transport")
  public ResponseEntity<IPage<Transport>> page(Page<Transport> page, Transport condition) {
    IPage<Transport> pageDate = service.findByPage(page, condition);
    return ResponseEntity.ok(pageDate);
  }


  @PostMapping
  @RequiresPermissions("shop:transport:save")
  @ApiOperation("新增一个Transport")
  public ResponseEntity<Void> save(@RequestBody @Validated Transport entity) {
    service.save(entity);
    return ResponseEntity.ok().build();
  }

  @PutMapping
  @RequiresPermissions("shop:transport:update")
  @ApiOperation("根据id修改Transport")
  public ResponseEntity<Void> update(@RequestBody @Validated Transport entity) {
    service.updateById(entity);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  @RequiresPermissions("shop:transport:delete")
  @ApiOperation("删除Transport")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    service.removeById(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/info/{id}")
  @RequiresPermissions("shop:transport:info")
  @ApiOperation("查询Transport详情")
  public ResponseEntity<Transport> info(@PathVariable("id") Long id) {
    Transport entity = service.getById(id);
    return ResponseEntity.ok(entity);
  }
}
