package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.Prod;
import com.zy.entity.ProdComm;
import com.zy.mapper.ProdCommMapper;
import com.zy.mapper.ProdMapper;
import com.zy.service.ProdCommService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 商品评论 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
@Slf4j
public class ProdCommServiceImpl extends ServiceImpl<ProdCommMapper, ProdComm> implements ProdCommService {

  @Autowired
  private ProdMapper prodMapper;

  @Autowired
  private  ProdCommMapper prodCommMapper;
  @Override
  public IPage<ProdComm> findByPage(Page<ProdComm> page, ProdComm prodComm) {
    page.addOrder(OrderItem.desc("rec_time"));
    List<Object> prodIds=null;
    if (prodComm.getProdName()!=null){
      prodIds = prodMapper.selectObjs(new LambdaQueryWrapper<Prod>().select(Prod::getProdId).ne(Prod::getStatus, -1).like(Prod::getProdName, prodComm.getProdName()));
      if (prodIds==null||prodIds.isEmpty()){
        page.setTotal(0L);
        return page;
      }
    }
    IPage<ProdComm> prodCommIPage = prodCommMapper.selectPage(page, new LambdaQueryWrapper<ProdComm>().eq(prodComm.getStatus() != null, ProdComm::getStatus, prodComm.getStatus()).in((prodIds != null && !prodIds.isEmpty()), ProdComm::getProdId, prodIds));
    if (prodCommIPage.getRecords().size()>0){
      List<ProdComm> records = prodCommIPage.getRecords();
      for (ProdComm record : records) {
        Prod prod = prodMapper.selectOne(new LambdaQueryWrapper<Prod>().select(Prod::getProdName).eq(Prod::getProdId, record.getProdId()));
        record.setProdName(prod.getProdName());

      }
    }

    return prodCommIPage;
  }

  @Override
  public IPage<ProdComm> findPortalPage(Page<ProdComm> page, Long prodId, Integer evaluate) {
    page.addOrder(OrderItem.desc("rec_time")) ;
    return prodCommMapper.selectPage(page,new LambdaQueryWrapper<ProdComm>()
      .eq(ProdComm::getProdId,prodId).
        eq(
          (evaluate!=null && evaluate!=-1 && evaluate!=3),ProdComm::getEvaluate,evaluate)

      .isNotNull((evaluate!=null && evaluate==3),ProdComm::getPics)
    );
  }


  @Override
  public ProdComm getById(Serializable id) {
    ProdComm prodComm = super.getById(id);
    Prod prod = prodMapper.selectOne(new LambdaQueryWrapper<Prod>().select(Prod::getProdName).eq(Prod::getProdId, prodComm.getProdId()));
    prodComm.setProdName(prod.getProdName());
    return prodComm;
  }

  @Override
  public boolean updateById(ProdComm entity) {
    ProdComm prodComm = new ProdComm();
    prodComm.setProdCommId(entity.getProdCommId());
    prodComm.setReplyContent(entity.getReplyContent());
    prodComm.setStatus(entity.getStatus());
    prodComm.setReplyTime(new Date());
    return super.updateById(entity);
  }
}
