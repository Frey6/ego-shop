package com.zy.controller.item;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.ProdTag;
import com.zy.service.ProdTagService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("prod/prodTag")
public class ProdTagController {


  @Reference(check = false)
  private ProdTagService prodTagService ;

  @GetMapping("/page")
  @ApiOperation("分页查询商品的标签")
  @RequiresPermissions("prod:prodTag:page")
  public ResponseEntity<IPage<ProdTag>> findByPage(Page<ProdTag> page,ProdTag prodTag){
    IPage<ProdTag> pageData = prodTagService.findByPage(page,prodTag);
    return ResponseEntity.ok(pageData) ;
  }


  @GetMapping("/info/{id}")
  @RequiresPermissions("prod:prodTag:info")
  @ApiOperation("标签数据的回显")
  public ResponseEntity<ProdTag> findById(@PathVariable("id") Long id){
    ProdTag prodTag = prodTagService.getById(id);
    return ResponseEntity.ok(prodTag);
  }


  @PostMapping
  @ApiOperation("新增一个商品的标签")
  @RequiresPermissions("prod:prodTag:save")
  public ResponseEntity<Void> save(@RequestBody @Validated ProdTag prodTag){
    prodTagService.save(prodTag);
    return ResponseEntity.ok().build() ;
  }

  @DeleteMapping("/{id}")
  @RequiresPermissions("prod:prodTag:delete")
  @ApiOperation("删除商品的标签")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id){
    prodTagService.removeById(id) ;
    return ResponseEntity.ok().build() ;
  }


  @DeleteMapping
  @RequiresPermissions("prod:prodTag:delete")
  @ApiOperation("删除商品的标签")
  public ResponseEntity<Void> delete(@RequestBody List<Long> ids){
    prodTagService.removeByIds(ids) ;
    return ResponseEntity.ok().build() ;
  }


  @PutMapping
  @ApiOperation("新增一个商品的标签")
  @RequiresPermissions("prod:prodTag:save")
  public ResponseEntity<Void> update(@RequestBody @Validated ProdTag prodTag){
    prodTagService.updateById(prodTag);
    return ResponseEntity.ok().build() ;
  }
  @GetMapping({"/listTagList"})
  @ApiOperation("列出所有的商品标签的列表")
  @RequiresPermissions({"prod:prodTag:info"})
  public ResponseEntity<List<ProdTag>> list()
  {
    List<ProdTag> list = this.prodTagService.list();
    return ResponseEntity.ok(list);
  }

}
