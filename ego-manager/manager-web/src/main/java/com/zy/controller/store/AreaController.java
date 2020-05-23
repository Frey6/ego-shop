package com.zy.controller.store;

import com.zy.entity.Area;
import com.zy.service.AreaService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/area")
public class AreaController {

  @Reference(check = false)
  private AreaService areaService;


  @GetMapping("/list")
  @RequiresPermissions("admin:area:list")
  @ApiOperation("查询地址的列表")
  public ResponseEntity<List<Area>> list() {
    List<Area> list = areaService.list();
    return ResponseEntity.ok(list);
  }

  @GetMapping({"/listByPid"})
  @RequiresPermissions({"admin:area:list"})
  @ApiOperation("通过父id查询儿子")
  public ResponseEntity<List<Area>> listByPid(@RequestParam(name="pid", defaultValue="0") Long pId)
  {
    List<Area> areas = areaService.listByPid(pId);
    return ResponseEntity.ok(areas);
  }

  @PostMapping
  @RequiresPermissions("admin:area:save")
  @ApiOperation("新增一个Area")
  public ResponseEntity<Void> save(@RequestBody @Validated Area entity) {
    areaService.save(entity);
    return ResponseEntity.ok().build();
  }

  @PutMapping
  @RequiresPermissions("admin:area:update")
  @ApiOperation("根据id修改Area")
  public ResponseEntity<Void> update(@RequestBody @Validated Area entity) {
    areaService.updateById(entity);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  @RequiresPermissions("admin:area:delete")
  @ApiOperation("删除Area")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    areaService.removeById(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/info/{id}")
  @RequiresPermissions("admin:area:info")
  @ApiOperation("查询Area详情")
  public ResponseEntity<Area> info(@PathVariable("id") Long id) {
    Area entity = areaService.getById(id);
    return ResponseEntity.ok(entity);
  }
}
