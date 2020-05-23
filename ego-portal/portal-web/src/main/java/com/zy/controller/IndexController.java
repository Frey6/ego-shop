package com.zy.controller;

import cn.hutool.core.bean.BeanUtil;
import com.sun.prism.impl.BaseMesh;
import com.zy.entity.Category;
import com.zy.entity.IndexImg;
import com.zy.entity.Notice;
import com.zy.entity.ProdTag;
import com.zy.service.CategoryService;
import com.zy.service.IndexImgService;
import com.zy.service.NoticeService;
import com.zy.service.ProdTagService;
import com.zy.vo.CategoryVo;
import com.zy.vo.IndexImgVo;
import com.zy.vo.NoticeVo;
import com.zy.vo.ProdTagVo;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class IndexController {

  @Reference(check = false)
  private IndexImgService indexImgService;

  @Autowired
  private MapperFacade mapperFacade;

  @Reference(check = false)
  private NoticeService noticeService;

  @Reference(check = false)
  private ProdTagService prodTagService;

  @Reference(check = false)
  private CategoryService categoryService;

  /**
   * 轮播图的加载
   */
  @GetMapping("/indexImgs")
  public ResponseEntity<List<IndexImgVo>> loadIndexImgs(){
    List<IndexImg> indexImgList=  indexImgService.loadIndexImgs();
    if (indexImgList==null||indexImgList.isEmpty()){
      return  ResponseEntity.ok(Collections.EMPTY_LIST);
    }

    List<IndexImgVo> indexImgVos = mapperFacade.mapAsList(indexImgList, IndexImgVo.class);
//    ArrayList<IndexImgVo> indexImgVos = new ArrayList<>(indexImgList.size());
//    for (IndexImg indexImg : indexImgList) {
//      IndexImgVo indexImgVo = new IndexImgVo();
////      BeanUtil.copyProperties(indexImg,indexImgVo);
//      BeanUtils.copyProperties(indexImg,indexImgVo);
//      indexImgVos.add(indexImgVo);
//    }

    return ResponseEntity.ok(indexImgVos);
  }

  @GetMapping({"/shop/notice/topNoticeList"})
  @ApiOperation("加载置顶的通知")
  public ResponseEntity<List<NoticeVo>> loadTopNotice()
  {
    List<Notice> notices = noticeService.loadIndexTopNotice();
    if (notices == null) {
      return ResponseEntity.ok(Collections.emptyList());
    }
    List<NoticeVo> noticeVos = this.mapperFacade.mapAsList(notices, NoticeVo.class);
    return ResponseEntity.ok(noticeVos);
  }

  @GetMapping({"/prod/tag/prodTagList"})
  @ApiOperation("加载首页数据的标签")
  public ResponseEntity<List<ProdTagVo>> loadIndexTag()
  {
    List<ProdTag> prodTagList = prodTagService.loadIndexProdTag();
    if ((prodTagList == null) || (prodTagList.isEmpty())) {
      return ResponseEntity.ok(Collections.emptyList());
    }
    List<ProdTagVo> prodTagVoList = this.mapperFacade.mapAsList(prodTagList, ProdTagVo.class);
    return ResponseEntity.ok(prodTagVoList);
  }

  @GetMapping({"/category/categoryInfo"})
  @ApiOperation("加载所有的二级菜单")
  public ResponseEntity<List<CategoryVo>> loadAllSubCategorys()
  {
    List<Category> categories = categoryService.loadAllSubCategorys();
    if ((categories == null) || (categories.isEmpty())) {
      return ResponseEntity.ok(Collections.emptyList());
    }
    List<CategoryVo> categoryVos = this.mapperFacade.mapAsList(categories, CategoryVo.class);
    return ResponseEntity.ok(categoryVos);
  }
}
