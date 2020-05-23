package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.ProdTag;
import com.zy.entity.ProdTagReference;
import com.zy.mapper.ProdTagMapper;
import com.zy.mapper.ProdTagReferenceMapper;
import com.zy.service.ProdTagService;
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
 * 商品分组表 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */

@Service
@Slf4j
@CacheConfig(cacheNames = "com.zy.service.impl.ProdTagServiceImpl")
public class ProdTagServiceImpl extends ServiceImpl<ProdTagMapper, ProdTag> implements ProdTagService{

  @Autowired
  private ProdTagMapper prodTagMapper ;

  @Autowired
  private ProdTagReferenceMapper prodTagReferenceMapper ;

  private static final String INDEX_PROD_TAG = "'prod-prod-tag'" ;

  @Override
  public IPage<ProdTag> findByPage(Page<ProdTag> page, ProdTag prodTag) {
    page.addOrder(OrderItem.desc("update_time"));
    return prodTagMapper.selectPage(page,new LambdaQueryWrapper<ProdTag>().
      like(StringUtils.hasText(prodTag.getTitle()),ProdTag::getTitle,prodTag.getTitle()).
      eq(prodTag.getStatus()!=null,ProdTag::getStatus,prodTag.getStatus())
    );
  }

  @Cacheable(key = INDEX_PROD_TAG)
  @Override
  public List<ProdTag> loadIndexProdTag() {
    return prodTagMapper.selectList(new LambdaQueryWrapper<ProdTag>().
      eq(ProdTag::getStatus,1).
      orderByAsc(ProdTag::getSeq)
    );
  }

  @CacheEvict(key = INDEX_PROD_TAG)
  @Override
  public boolean removeById(Serializable id) {
    log.info("删除一个商品的标签{}",id);
    // 我们的商品数据可能引用了标签
    Integer count = prodTagReferenceMapper.selectCount(new LambdaQueryWrapper<ProdTagReference>().eq(ProdTagReference::getTagId, id));
    if(count>0){
      throw new IllegalArgumentException("这个商品标签正在被别人使用");
    }
    return super.removeById(id);
  }

  @CacheEvict(key = INDEX_PROD_TAG)
  @Override
  public boolean save(ProdTag entity) {
    return super.save(entity);
  }

  @CacheEvict(key = INDEX_PROD_TAG)
  @Override
  public boolean updateById(ProdTag entity) {
    return super.updateById(entity);
  }
}

