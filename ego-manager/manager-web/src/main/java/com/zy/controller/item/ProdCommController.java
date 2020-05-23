package com.zy.controller.item;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.ProdComm;
import com.zy.service.ProdCommService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/prod/prodComm"})
public class ProdCommController
{
  @Reference(check=false)
  private ProdCommService prodCommService;

  @GetMapping({"/page"})
  @RequiresPermissions({"prod:prodComm:page"})
  @ApiOperation("分页查询商品的评论数据")
  public ResponseEntity<IPage<ProdComm>> findByPage(Page<ProdComm> page, ProdComm prodComm)
  {
    IPage<ProdComm> prodCommIPage = this.prodCommService.findByPage(page, prodComm);
    return ResponseEntity.ok(prodCommIPage);
  }

  @GetMapping({"/info/{id}"})
  @RequiresPermissions({"prod:prodComm:info"})
  @ApiOperation("评论的回显")
  public ResponseEntity<ProdComm> findById(@PathVariable("id") Long id)
  {
    ProdComm prodComm = (ProdComm)this.prodCommService.getById(id);
    return ResponseEntity.ok(prodComm);
  }

  @PutMapping
  @ApiOperation("评论的修改")
  @RequiresPermissions({"prod:prodComm:update"})
  public ResponseEntity<Void> update(@RequestBody @Validated ProdComm prodComm)
  {
    this.prodCommService.updateById(prodComm);
    return ResponseEntity.ok().build();
  }
}
