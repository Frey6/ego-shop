package com.zy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.domain.ProdCommSurvey;
import com.zy.entity.Prod;
import com.zy.entity.ProdComm;
import com.zy.entity.User;
import com.zy.service.ProdCommService;
import com.zy.service.ProdService;
import com.zy.service.UserService;
import com.zy.vo.ProdCommVo;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProdController
{
  @Reference(check=false)
  private ProdService prodService;
  @Reference(check=false)
  private ProdCommService prodCommService;
  @Reference(check=false)
  private UserService userService;
  @Autowired
  private MapperFacade mapperFacade;

  @GetMapping({"/prod/prodInfo"})
  @ApiOperation("加载商品的详情")
  public ResponseEntity<Prod> loadProd(@RequestParam(required=true) Long prodId)
  {
    Prod prod = (Prod)this.prodService.getById(prodId);
    return ResponseEntity.ok(prod);
  }

  @GetMapping({"/prodComm/prodCommData"})
  @ApiOperation("评论的总览")
  public ResponseEntity<ProdCommSurvey> loadProdCommSurvey(Long prodId)
  {
    ProdCommSurvey prodCommSurvey = this.prodService.getProdCommSurvey(prodId);
    return ResponseEntity.ok(prodCommSurvey);
  }
//
  @GetMapping({"/prodComm/prodCommPageByProd"})
  @ApiOperation("加载评论的数据")
  public ResponseEntity<IPage<ProdCommVo>> findByPage(Page<ProdComm> page, @RequestParam(required=true) Long prodId, @RequestParam(defaultValue="-1") Integer evaluate)
  {
    Page<ProdCommVo> prodCommVoPage = new Page(page.getCurrent(), page.getSize());
    IPage<ProdComm> prodCommIPage = this.prodCommService.findPortalPage(page, prodId, evaluate);
    if ((prodCommIPage.getTotal() > 0L) && (!prodCommIPage.getRecords().isEmpty()))
    {
      List<ProdComm> records = prodCommIPage.getRecords();
      List<ProdCommVo> prodCommVos = this.mapperFacade.mapAsList(records, ProdCommVo.class);
      for (ProdCommVo prodCommVo : prodCommVos)
      {
        User user = this.userService.findUserByUserName(prodCommVo.getUserId());
        prodCommVo.setNickName(user.getNickName());
        prodCommVo.setPic(user.getPic());
      }
      prodCommVoPage.setTotal(prodCommIPage.getTotal());
      prodCommVoPage.setRecords(prodCommVos);
    }
    return ResponseEntity.ok(prodCommVoPage);
  }
}