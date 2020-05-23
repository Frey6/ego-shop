package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.entity.Category;
import com.zy.entity.Prod;
import com.zy.mapper.CategoryMapper;
import com.zy.mapper.ProdMapper;
import com.zy.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 产品类目 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "com.zy.service.impl.CategoryServiceImpl")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

  private final static String ALL_SUB_CATEGORIES = "'all-sub-categories'" ;

  @Autowired
  private CategoryMapper categoryMapper;

  @Autowired
  private ProdMapper prodMapper;

  @CacheEvict(key = ALL_SUB_CATEGORIES)
  @Override
  public boolean removeById(Serializable id) {
    log.info("删除分类为%s的分类",id);
    Integer count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
    if (count>0){
      throw  new IllegalArgumentException("该分类有儿子，不能删除");
    }
    Integer count1 = prodMapper.selectCount(new LambdaQueryWrapper<Prod>().eq(Prod::getCategoryId, id));
    if (count1>0){
      throw  new IllegalArgumentException("该分类被商品正在使用，不能删除");
    }
    return super.removeById(id);
  }

  @Override
  public List<Category> listAllParent() {
    List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getGrade, 1));

    return categories;
  }

  @Cacheable(key = ALL_SUB_CATEGORIES)
  @Override
  public List<Category> loadAllSubCategorys() {
    List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getGrade, 2));
    return categories;
  }

  @CacheEvict(key = ALL_SUB_CATEGORIES)
  @Override
  public boolean save(Category entity) {
    log.info("新增一个分类，名称%s",entity.getCategoryName());
    validate(entity);
    entity.setRecTime(new Date());
    entity.setUpdateTime(new Date());
    return super.save(entity);
  }

  private void validate(Category entity) {
    if (entity.getParentId()==null||entity.getParentId().equals(0L)){
      entity.setParentId(0L);
      entity.setGrade(1);
    }
    else {
      Category parent = this.getById(entity.getParentId());
      if (parent==null){
        throw  new IllegalArgumentException("新增时选的父id不能为空");
      }
    int subNode=  parent.getGrade() + 1;
      entity.setGrade(subNode);
      entity.setStatus(1);
    }
  }

  @CacheEvict(key = ALL_SUB_CATEGORIES)
  @Override
  public boolean updateById(Category entity) {
    log.info("修改id为{}",entity.getCategoryId());
     validate(entity);
     entity.setUpdateTime(new Date());
    return super.updateById(entity);
  }
}
