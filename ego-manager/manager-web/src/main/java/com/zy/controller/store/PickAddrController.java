package com.zy.controller.store;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.PickAddr;
import com.zy.service.PickAddrService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop/pickAddr")
public class PickAddrController {

  @Reference(check = false)
  private PickAddrService service;

  @GetMapping("/page")
  @RequiresPermissions("shop:pickAddr:page")
  @ApiOperation("分页查询PickAddr")
  public ResponseEntity<IPage<PickAddr>> page(Page<PickAddr> page, PickAddr condition) {
    IPage<PickAddr> pageDate = service.findByPage(page,condition);
    return ResponseEntity.ok(pageDate);
  }


  @PostMapping
  @RequiresPermissions("shop:pickAddr:save")
  @ApiOperation("新增一个PickAddr")
  public ResponseEntity<Void> save(@RequestBody @Validated PickAddr entity) {
    service.save(entity);
    return ResponseEntity.ok().build();
  }

  @PutMapping
  @RequiresPermissions("shop:pickAddr:update")
  @ApiOperation("根据id修改PickAddr")
  public ResponseEntity<Void> update(@RequestBody @Validated PickAddr entity) {
    service.updateById(entity);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  @RequiresPermissions("shop:pickAddr:delete")
  @ApiOperation("删除PickAddr")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    service.removeById(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/info/{id}")
  @RequiresPermissions("shop:pickAddr:info")
  @ApiOperation("查询PickAddr详情")
  public ResponseEntity<PickAddr> info(@PathVariable("id") Long id) {
    PickAddr entity = service.getById(id);
    return ResponseEntity.ok(entity);
  }
}

