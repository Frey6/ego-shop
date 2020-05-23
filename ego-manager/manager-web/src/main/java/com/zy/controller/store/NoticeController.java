package com.zy.controller.store;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.Notice;
import com.zy.service.NoticeService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/shop/notice"})
public class NoticeController
{
  @Reference(check=false)
  private NoticeService noticeService;

  @GetMapping({"/page"})
  @RequiresPermissions({"shop:notice:page"})
  @ApiOperation("分页查询公共")
  public ResponseEntity<IPage<Notice>> findByPage(Page<Notice> page, Notice notice)
  {
    IPage<Notice> pageData = this.noticeService.findByPage(page, notice);
    return ResponseEntity.ok(pageData);
  }

  @GetMapping({"/info/{id}"})
  @RequiresPermissions({"shop:notice:info"})
  @ApiOperation("公告的回显")
  public ResponseEntity<Notice> findById(@PathVariable("id") Long id)
  {
    Notice notice = noticeService.getById(id);
    return ResponseEntity.ok(notice);
  }

  @DeleteMapping({"/{id}"})
  @RequiresPermissions({"shop:notice:delete"})
  @ApiOperation("删除公告")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id)
  {
    this.noticeService.removeById(id);
    return ResponseEntity.ok().build();
  }

  @PutMapping
  @RequiresPermissions({"shop:notice:update"})
  @ApiOperation("修改公告")
  public ResponseEntity<Void> update(@RequestBody @Validated Notice notice)
  {
    this.noticeService.updateById(notice);
    return ResponseEntity.ok().build();
  }

  @PostMapping
  @RequiresPermissions({"shop:notice:save"})
  @ApiOperation("新增公告")
  public ResponseEntity<Void> save(@RequestBody @Validated Notice notice)
  {
    this.noticeService.save(notice);
    return ResponseEntity.ok().build();
  }
}
