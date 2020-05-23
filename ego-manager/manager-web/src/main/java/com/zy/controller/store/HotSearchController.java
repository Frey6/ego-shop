package com.zy.controller.store;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.HotSearch;
import com.zy.service.HotSearchService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController()
@RequestMapping("/admin/hotSearch")
public class HotSearchController {

  @Reference(check = false)
  private HotSearchService hotSearchService ;

  @GetMapping("/page")
  @RequiresPermissions("admin:hotSearch:page")
  @ApiOperation("热搜的分页查询")
  public ResponseEntity<IPage<HotSearch>> findByPage(Page<HotSearch> page, HotSearch hotSearch){
    IPage<HotSearch> pageData = hotSearchService.findByPage(page,hotSearch);
    return  ResponseEntity.ok(pageData) ;
  }

  @GetMapping("/info/{id}")
  @RequiresPermissions("admin:hotSearch:info")
  @ApiOperation("热搜的回显")
  public ResponseEntity<HotSearch> findById(@PathVariable("id") Long id){
    HotSearch byId = hotSearchService.getById(id);
    return ResponseEntity.ok(byId) ;
  }

  @DeleteMapping("/{id}")
  @RequiresPermissions("admin:hotSearch:delete")
  @ApiOperation("删除热搜")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id){
    hotSearchService.removeById(id) ;
    return ResponseEntity.ok().build() ;
  }

  @PostMapping
  @RequiresPermissions("admin:hotSearch:save")
  @ApiOperation("新增一个热搜")
  public ResponseEntity<Void> add(@RequestBody @Validated  HotSearch hotSearch){
    hotSearch.setRecDate(new Date());
    hotSearchService.save(hotSearch) ;
    return ResponseEntity.ok().build() ;
  }

  @PutMapping
  @RequiresPermissions("admin:hotSearch:update")
  @ApiOperation("新增一个热搜")
  public ResponseEntity<Void> update(@RequestBody @Validated HotSearch hotSearch){
    hotSearchService.updateById(hotSearch) ;
    return ResponseEntity.ok().build() ;
  }

}

