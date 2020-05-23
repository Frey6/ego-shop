package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.ProdProp;
import com.zy.entity.ProdPropValue;
import com.zy.mapper.ProdPropMapper;
import com.zy.mapper.ProdPropValueMapper;
import com.zy.service.ProdPropService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
@Slf4j
public class ProdPropServiceImpl extends ServiceImpl<ProdPropMapper, ProdProp> implements ProdPropService {

  @Autowired
  private  ProdPropMapper prodPropMapper;

  @Autowired
  private ProdPropValueMapper prodPropValueMapper;

  @Override
  public ProdProp getById(Serializable id) {
    ProdProp byId = super.getById(id);
    if(byId!=null){
      List<ProdPropValue> prodPropValues = prodPropValueMapper.selectList(new LambdaQueryWrapper<ProdPropValue>().eq(ProdPropValue::getPropId, byId.getPropId()));
      byId.setProdPropValues(prodPropValues);
    }

    return byId;
  }

  @Override
  @Transactional
  public boolean updateById(ProdProp entity) {
    log.info("修改id为{}的属性",entity.getPropId());
    boolean b = super.updateById(entity);
    if (b){
      prodPropValueMapper.delete(new LambdaQueryWrapper<ProdPropValue>().eq(ProdPropValue::getPropId,entity.getPropId()));
      @NotEmpty List<ProdPropValue> prodPropValues = entity.getProdPropValues();
      for (ProdPropValue prodPropValue : prodPropValues) {
        prodPropValue.setPropId(entity.getPropId());
        prodPropValueMapper.insert(prodPropValue);
      }
    }
    return b;
  }



  @Override
  @Transactional
  public boolean save(ProdProp entity) {
    log.info("新增一个属性，{}",entity.getPropName());
    boolean save = super.save(entity);
    if (save){
      @NotEmpty List<ProdPropValue> prodPropValues = entity.getProdPropValues();
      for (ProdPropValue prodPropValue : prodPropValues) {
        prodPropValue.setPropId(entity.getPropId());
        prodPropValueMapper.insert(prodPropValue);
      }
    }
    return save;
  }

  @Override
  @Transactional
  public boolean removeById(Serializable id) {
    log.info("删除id为{}的属性",id);

    boolean b = super.removeById(id);
    if(b){
      prodPropValueMapper.delete(new LambdaQueryWrapper<ProdPropValue>().eq(ProdPropValue::getPropId,id));
    }
    return b;
  }

  @Override
  public IPage<ProdProp> findByPage(Page<ProdProp> page, ProdProp prodProp) {
    IPage<ProdProp> prodPropIPage = prodPropMapper.selectPage(page, new LambdaQueryWrapper<ProdProp>().
      like(StringUtils.hasText(prodProp.getPropName()), ProdProp::getPropName, prodProp.getPropName()));
    if(prodPropIPage.getRecords().size()>0){
      List<ProdProp> records = prodPropIPage.getRecords();
      for (ProdProp record : records) {
        List<ProdPropValue> prodPropValues = prodPropValueMapper.selectList(new LambdaQueryWrapper<ProdPropValue>().
          eq(ProdPropValue::getPropId, record.getPropId()));
        record.setProdPropValues(prodPropValues);
      }

    }
    return prodPropIPage;
  }

  @Override
  public List<ProdPropValue> listSpecValue(Long id) {
    List<ProdPropValue> prodPropValues = prodPropValueMapper.selectList(new LambdaQueryWrapper<ProdPropValue>().eq(ProdPropValue::getPropId, id));

    return prodPropValues==null? Collections.emptyList():prodPropValues;
  }
}
