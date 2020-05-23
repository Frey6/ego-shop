package com.zy.controller.store;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.IndexImg;
import com.zy.entity.Prod;
import com.zy.service.IndexImgService;
import com.zy.service.ProdService;
import io.swagger.annotations.ApiOperation;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/indexImg")
public class IndexImgController {

  @Reference(check = false)
  private IndexImgService service;
  @Reference(check = false)
  private ProdService prodService;

  @GetMapping("/page")
  @RequiresPermissions("admin:indexImg:page")
  @ApiOperation("分页查询IndexImg")
  public ResponseEntity<IPage<IndexImg>> page(Page<IndexImg> page, IndexImg condition) {
    IPage<IndexImg> pageDate = service.findByPage(page, condition);
    return ResponseEntity.ok(pageDate);
  }


  @PostMapping
  @RequiresPermissions("admin:indexImg:save")
  @ApiOperation("新增一个IndexImg")
  public ResponseEntity<Void> save(@RequestBody @Validated IndexImg entity) {
    service.save(entity);
    return ResponseEntity.ok().build();
  }

  @PutMapping
  @RequiresPermissions("admin:indexImg:update")
  @ApiOperation("根据id修改IndexImg")
  public ResponseEntity<Void> update(@RequestBody @Validated IndexImg entity) {
    service.updateById(entity);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  @RequiresPermissions("admin:indexImg:delete")
  @ApiOperation("删除IndexImg")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    service.removeById(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/info/{id}")
  @RequiresPermissions("admin:indexImg:info")
  @ApiOperation("查询IndexImg详情")
  public ResponseEntity<IndexImg> info(@PathVariable("id") Long id) {
    IndexImg entity = (IndexImg)this.service.getById(id);
    if (entity.getRelation() != null)
    {
      Prod prod = this.prodService.getSimpleProd(entity.getRelation());
      entity.setProdName(prod.getProdName());
      entity.setPic(prod.getPic());
    }
    return ResponseEntity.ok(entity);
  }
}
