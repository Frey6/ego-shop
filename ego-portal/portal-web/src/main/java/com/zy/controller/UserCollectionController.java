package com.zy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.User;
import com.zy.entity.UserCollection;
import com.zy.model.ProdSolr;
import com.zy.service.SearchService;
import com.zy.service.UserCollectionService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserCollectionController {

  @Reference(check = false)
  private UserCollectionService userCollectionService ;

  @Reference(check = false)
  private SearchService searchService;

  @GetMapping("/p/user/collection/count")
  @ApiOperation("收藏")
  public ResponseEntity<Integer> getCollectionCount(){
    User user = (User) SecurityUtils.getSubject().getPrincipal();
    Integer totalCount = userCollectionService.getCollectionCount(user.getUserId());
    return ResponseEntity.ok(totalCount) ;
  }
  @GetMapping("/p/user/collection/prods")
  public ResponseEntity<Page<ProdSolr>> loadCollections(Page<UserCollection> page){
    User user = (User) SecurityUtils.getSubject().getPrincipal();
    // 查询商品的ids
    Page<Long> prodIdList = userCollectionService.getProdList(user.getUserId(),page);
    // 利用商品的ids 查询搜索系统得到商品的数据
    if(prodIdList.getRecords().isEmpty()){
      page.setTotal(prodIdList.getTotal()) ;
      return ResponseEntity.ok(null) ;
    }
    List<ProdSolr> pageData = searchService.findByProdIds(prodIdList.getRecords()) ;
    Page<ProdSolr> prodSolrPage = new Page<>(page.getCurrent(), page.getSize());
    prodSolrPage.setRecords(pageData) ;
    return ResponseEntity.ok(prodSolrPage) ;
  }

}

