package com.zy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.HotSearch;
import com.zy.model.ProdSolr;
import com.zy.service.HotSearchService;
import com.zy.service.SearchService;
import com.zy.vo.HotSearchVo;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class SearchController {

  @Reference(check = false)
  private SearchService searchService ;
  @Reference(check=false)
  private HotSearchService hotSearchService;
  @Autowired
  private MapperFacade mapperFacade;

  @GetMapping("/prod/prodListByTagId")
  @ApiOperation("通过活动的标签的id 查询商品")
  public ResponseEntity<Page<ProdSolr>> prodListByTagId(
    @RequestParam(required = true) Long tagId,
    Page<ProdSolr> page
  ){
    Page<ProdSolr> prod = searchService.findByTagId(tagId, 0, page);
    List<ProdSolr> records = prod.getRecords();
    for (ProdSolr record : records) {
      record.setSoldNum(record.getSoldNum()-1);
      record.setTotalStocks(record.getTotalStocks()-1);
    }
    prod.setRecords(records);
    return ResponseEntity.ok(prod) ;
  }

  @GetMapping("/search/searchProdPage")
  @ApiOperation("关键字查询商品")
  public ResponseEntity<Page<ProdSolr>> search(
    Page<ProdSolr> page,
    @RequestParam(defaultValue = "0") Integer sort,
    @RequestParam(defaultValue = "*") String prodName){
    Page<ProdSolr> search = searchService.search(prodName, sort, page);
    List<ProdSolr> records = search.getRecords();
    for (ProdSolr record : records) {
      record.setSoldNum(record.getSoldNum()-1);
      record.setTotalStocks(record.getTotalStocks()-1);
    }
    search.setRecords(records);
    return ResponseEntity.ok(search) ;
  }
  @GetMapping({"/prod/pageProd"})
  @ApiOperation("通过分类的id查询商品")
  public ResponseEntity<Page<ProdSolr>> findByCatId(@RequestParam(required=true) Long categoryId)
  {
    Page<ProdSolr> page = new Page();
    page.setSize(20L);
    return ResponseEntity.ok(this.searchService.findByCatId(categoryId, Integer.valueOf(0), page));
  }
  @GetMapping({"/search/hotSearchByShopId"})
  @ApiOperation("热搜")
  public ResponseEntity<List<HotSearchVo>> loadTopNHotSearch(Integer number, Integer sort)
  {
    List<HotSearch> hotSearches = this.hotSearchService.loadTopNHotSearch(number, sort);
    if ((hotSearches == null) || (hotSearches.isEmpty())) {
      return ResponseEntity.ok(Collections.emptyList());
    }
    List<HotSearchVo> hotSearchVos = this.mapperFacade.mapAsList(hotSearches, HotSearchVo.class);
    return ResponseEntity.ok(hotSearchVos);
  }

}

