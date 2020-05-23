package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.HotSearch;
import com.zy.mapper.HotSearchMapper;
import com.zy.service.HotSearchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 热搜 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "com.zy.service.impl.HotSearchServiceImpl")
public class HotSearchServiceImpl extends ServiceImpl<HotSearchMapper, HotSearch> implements HotSearchService{

  @Autowired
  private HotSearchMapper hotSearchMapper ;

  @Override
  public IPage<HotSearch> findByPage(Page<HotSearch> page, HotSearch hotSearch) {
    page.addOrder(OrderItem.desc("rec_date")) ;
    return hotSearchMapper.selectPage(page,new LambdaQueryWrapper<HotSearch>().
      like(StringUtils.hasText(hotSearch.getTitle()),HotSearch::getTitle,hotSearch.getTitle()).
      like(StringUtils.hasText(hotSearch.getContent()),HotSearch::getContent,hotSearch.getContent()).
      eq(hotSearch.getStatus()!=null ,HotSearch::getStatus,hotSearch.getStatus())
    );
  }

  @Override
  public List<HotSearch> loadTopNHotSearch(Integer number, Integer sort) {
    return  hotSearchMapper.selectList(new LambdaQueryWrapper<HotSearch>().
      eq(HotSearch::getStatus,1).
      orderByAsc(HotSearch::getSeq).
      last("limit "+number));
  }

  @Cacheable(key="#id")
  @Override
  public HotSearch getById(Serializable id) {
    return super.getById(id);
  }

  @CacheEvict(key = "#entity.hotSearchId")
  @Override
  public boolean updateById(HotSearch entity) {
    return super.updateById(entity);
  }

  @CacheEvict(key = "#id")
  @Override
  public boolean removeById(Serializable id) {
    return super.removeById(id);
  }
}


