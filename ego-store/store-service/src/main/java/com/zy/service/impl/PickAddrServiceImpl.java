package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.PickAddr;
import com.zy.mapper.PickAddrMapper;
import com.zy.service.PickAddrService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * <p>
 * 用户配送地址 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */

@CacheConfig(cacheNames = "com.zy.service.impl.PickAddrServiceImpl")
@Slf4j
@Service
public class PickAddrServiceImpl extends ServiceImpl<PickAddrMapper, PickAddr> implements PickAddrService{

  @Autowired
  private PickAddrMapper pickAddrMapper ;

  @Override
  public IPage<PickAddr> findByPage(Page<PickAddr> page, PickAddr condition) {

    return pickAddrMapper.selectPage(page,new LambdaQueryWrapper<PickAddr>().
      like(StringUtils.hasText(condition.getAddrName()),PickAddr::getAddrName,condition.getAddrName()));
  }

  @Cacheable(key = "#id")
  @Override
  public PickAddr getById(Serializable id) {
    return super.getById(id);
  }

  @CacheEvict(key = "#id")
  @Override
  public boolean removeById(Serializable id) {
    return super.removeById(id);
  }

  @CacheEvict(key = "#entity.addrId")
  @Override
  public boolean updateById(PickAddr entity) {
    return super.updateById(entity);
  }
}

