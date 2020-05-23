package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.IndexImg;
import com.zy.mapper.IndexImgMapper;
import com.zy.service.IndexImgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 主页轮播图 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "com.zy.service.impl.IndexImgServiceImpl")
public class IndexImgServiceImpl extends ServiceImpl<IndexImgMapper, IndexImg> implements IndexImgService{

  @Autowired
  private IndexImgMapper indexImgMapper  ;

  private final static String INDEX_ALL_IMGS = "'index_all_imgs'" ;


  @Override
  public IPage<IndexImg> findByPage(Page<IndexImg> page, IndexImg condition) {
    page.addOrder(OrderItem.desc("upload_time"));
    return indexImgMapper.selectPage(page,new LambdaQueryWrapper<IndexImg>().
      eq(condition.getStatus()!=null,IndexImg::getStatus,condition.getStatus()));
  }

  @Cacheable(key = INDEX_ALL_IMGS)
  @Override
  public List<IndexImg> loadIndexImgs() {

    return indexImgMapper.selectList(new LambdaQueryWrapper<IndexImg>().
      eq(IndexImg::getStatus,1).
      orderByAsc(IndexImg::getSeq)  // 前端使用它来排序

    );
  }

  @CacheEvict(key = INDEX_ALL_IMGS)
  @Override
  public boolean save(IndexImg entity) {
    entity.setUploadTime(new Date());
    return super.save(entity);
  }

  @Cacheable(key = "#id")
  @Override
  public IndexImg getById(Serializable id) {
    return super.getById(id);
  }

  @Caching(evict = {@CacheEvict(key="#id"),@CacheEvict(key = INDEX_ALL_IMGS)})
  @Override
  public boolean removeById(Serializable id) {
    return super.removeById(id);
  }

  @Caching(evict = {@CacheEvict(key="#entity.imgId"),@CacheEvict(key = INDEX_ALL_IMGS)})
  @Override
  public boolean updateById(IndexImg entity) {
    return super.updateById(entity);
  }
}
