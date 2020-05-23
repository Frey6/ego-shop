package com.zy.controller.item;

import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.zy.entity.Category;
import com.zy.service.CategoryService;
import com.zy.utils.FastdfsUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/prod/category")
@Slf4j
public class ProdCategoryController {

  @Reference(check = false)
  private CategoryService categoryService;

  @Value("${resources.url}")
  private  String serverPath;

  @Autowired
  private FastFileStorageClient fastFileStorageClient;

  @GetMapping({"/table"})
  @RequiresPermissions({"prod:category:page"})
  @ApiOperation("分类全查询")
  public ResponseEntity<List<Category>> list()
  {
    List<Category> list = this.categoryService.list();
    return ResponseEntity.ok(list);
  }

  @DeleteMapping({"/{id}"})
  @RequiresPermissions({"prod:category:delete"})
  @ApiOperation("删除分类")
  public ResponseEntity<Void> delete(@PathVariable Long id)
  {
    this.categoryService.removeById(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping({"listCategory"})
  @RequiresPermissions({"prod:category:page"})
  @ApiOperation("列出所有的父亲")
  public ResponseEntity<List<Category>> listAllParent()
  {
    List<Category> categories = this.categoryService.listAllParent();
    return ResponseEntity.ok(categories);
  }

  @PostMapping
  @RequiresPermissions({"prod:category:save"})
  @ApiOperation("新增一个分类")
  public ResponseEntity<Void> save(@RequestBody @Validated Category category)
  {
    this.categoryService.save(category);
    return ResponseEntity.ok().build();
  }

  @GetMapping({"/info/{id}"})
  @RequiresPermissions({"prod:category:info"})
  @ApiOperation("数据的回显")
  public ResponseEntity<Category> findById(@PathVariable("id") Long id)
  {
    Category category = (Category)this.categoryService.getById(id);
    return ResponseEntity.ok(category);
  }

  @PutMapping
  @RequiresPermissions({"prod:category:update"})
  @ApiOperation("修改分类")
  public ResponseEntity<Void> update(@RequestBody @Validated Category category) {
    Category dbCategory = categoryService.getById(category.getCategoryId());
    if (dbCategory == null) {
      throw new IllegalArgumentException("修改的值不存在");
    }
//    if (!dbCategory.getPic().equals(category.getPic()))
//    {
//      String pic = dbCategory.getPic();
//      String group = FastdfsUtil.parseGroup(pic);
//      try
//      {
//        this.fastFileStorageClient.deleteFile(group, pic.replaceFirst(group + "/", ""));
//      }
//      catch (Exception e)
//      {
//        log.error("��������������������{}", pic);
//      }
//    }
    if (!dbCategory.getPic().equals(category.getPic())){
      @NotBlank String pic = dbCategory.getPic();
      String group = FastdfsUtil.parseGroup(pic);
      try {
        fastFileStorageClient.deleteFile(group,pic.replaceFirst(group+"/",""));
      } catch (Exception e) {
        log.info("图片删除失败，地址为{}",pic);
      }
    }

    categoryService.updateById(category);
    return ResponseEntity.ok().build();
  }

}
