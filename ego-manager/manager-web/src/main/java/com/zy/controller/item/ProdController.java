package com.zy.controller.item;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.Prod;
import com.zy.service.ProdService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prod/prod")
public class ProdController {
  @Reference(check = false)
  private ProdService prodService;


  @GetMapping("/page")
  @ApiOperation("商品的分页查询")
  @RequiresPermissions("prod:prod:page")
  public ResponseEntity<IPage<Prod>> page(Page<Prod> page,Prod prod){
    IPage<Prod> page1=  prodService.findByPage(page,prod);
    return  ResponseEntity.ok(page1);
  }

  @DeleteMapping("/{id}")
  @ApiOperation("删除一个值")
  @RequiresPermissions("prod:prod:delete")
  public  ResponseEntity<Void> delete(@PathVariable("id") Long id){
    prodService.removeById(id);
    return  ResponseEntity.ok().build();
  }
  @DeleteMapping()
  @ApiOperation("删除一个值")
  @RequiresPermissions("prod:prod:delete")
  public  ResponseEntity<Void> delete(@RequestBody List<Long> list){
    prodService.removeByIds(list);
    return  ResponseEntity.ok().build();
  }

  @PostMapping
  @ApiOperation("新增一个商品")
  @RequiresPermissions({"prod:prod:save"})
  public ResponseEntity<Void> save(@RequestBody @Validated Prod prod)
  {
    this.prodService.save(prod);
    return ResponseEntity.ok().build();
  }

  @GetMapping({"/info/{id}"})
  @ApiOperation("数据的回显")
  @RequiresPermissions({"prod:prod:info"})
  public ResponseEntity<Prod> findById(@PathVariable("id") Long id)
  {
    Prod prod = prodService.getById(id);
    return ResponseEntity.ok(prod);
  }

  @PutMapping
  @ApiOperation("修改一个商品")
  @RequiresPermissions({"prod:prod:update"})
  public ResponseEntity<Void> update(@RequestBody @Validated Prod prod)
  {
    this.prodService.updateById(prod);
    return ResponseEntity.ok().build();
  }


//  @GetMapping("/ok")
//  public List<Prod>  test(){
//    List<Prod> list = prodService.list();
//    return  list;
//  }
}
