package com.zy.controller.item;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.ProdProp;
import com.zy.entity.ProdPropValue;
import com.zy.service.ProdPropService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prod/spec")
public class ProdSpecController {

  @Reference(check = false)
  private ProdPropService prodPropService;

  @GetMapping("/page")
  @ApiOperation("分页查询")
  @RequiresPermissions("prod:spec:page")
  public ResponseEntity<IPage<ProdProp>> page(Page<ProdProp> page,ProdProp prodProp){
    IPage<ProdProp> prodPropIPage= prodPropService.findByPage(page,prodProp);
    return  ResponseEntity.ok(prodPropIPage);
  }

  @DeleteMapping({"/{id}"})
  @RequiresPermissions({"prod:spec:delete"})
  @ApiOperation("删除属性")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id)
  {
    this.prodPropService.removeById(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping
  @RequiresPermissions({"prod:spec:save"})
  @ApiOperation("新增一个属性的值")
  public ResponseEntity<Void> add(@RequestBody @Validated ProdProp prodProp)
  {
    this.prodPropService.save(prodProp);
    return ResponseEntity.ok().build();
  }

  @PutMapping
  @RequiresPermissions({"prod:spec:update"})
  @ApiOperation("修改一个属性的值")
  public ResponseEntity<Void> update(@RequestBody @Validated ProdProp prodProp)
  {
    this.prodPropService.updateById(prodProp);
    return ResponseEntity.ok().build();
  }

  @GetMapping({"/info/{id}"})
  @RequiresPermissions({"prod:spec:info"})
  @ApiOperation("属性值的回显")
  public ResponseEntity<ProdProp> findById(@PathVariable("id") Long id)
  {
    ProdProp byId = (ProdProp)this.prodPropService.getById(id);
    return ResponseEntity.ok(byId);
  }

  @GetMapping({"/list"})
  @RequiresPermissions({"prod:spec:info"})
  @ApiOperation("属性的回显")
  public ResponseEntity<List<ProdProp>> list()
  {
    List<ProdProp> list = this.prodPropService.list();
    return ResponseEntity.ok(list);
  }

  @GetMapping({"/listSpecValue/{id}"})
  @RequiresPermissions({"prod:spec:info"})
  @ApiOperation("属性值的回显")
  public ResponseEntity<List<ProdPropValue>> listSpecValue(@PathVariable("id") Long id)
  {
    List<ProdPropValue> prodPropValue = this.prodPropService.listSpecValue(id);
    return ResponseEntity.ok(prodPropValue);
  }
}
