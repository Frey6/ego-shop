package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.entity.Area;
import com.zy.mapper.AreaMapper;
import com.zy.service.AreaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

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
@Service(timeout = 10000)
@CacheConfig(cacheNames = "com.zy.service.impl.AreaServiceImpl")
@Slf4j
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService{

  @Autowired
  private  AreaMapper areaMapper ;


  private final  static   String ALL_AREA = "'all-area'" ; // 在spring el 里面的常量的表示形式

  @Cacheable(key=ALL_AREA)
  public List<Area> list() {
    return super.list();
  }

  @Cacheable(key="#id")
  public Area getById(Serializable id) {
    return super.getById(id);
  }

  /**
   * // 你删除一个后，对list 有影响
   * 1 list 包含所有的数据
   *  你删除一个数据后，list 里面应该要少一个值
   *  我们的缓存此时有2 个：1 list 2 单个对象
   * @param id
   * @return
   */
  @Caching(evict = {
    @CacheEvict(key="#id"),
    @CacheEvict(key=ALL_AREA)
  })
  public boolean removeById(Serializable id) {
    log.info("删除区域{}",id);
    // 看看儿子
    Integer count = areaMapper.selectCount(new LambdaQueryWrapper<Area>().eq(Area::getParentId, id));
    if(count>0){
      throw new IllegalArgumentException("该节点里面有儿子") ;
    }
    return  super.removeById(id) ;
  }

  @CacheEvict(key = ALL_AREA)
  public boolean save(Area entity) {
    if(entity.getParentId()==null || entity.getParentId().equals(0L)){
      entity.setLevel(1);
    }
    else {
      Area area = super.getById(entity.getParentId());
      entity.setLevel(area.getLevel() + 1);
    }

    return super.save(entity);
  }

  @Caching(evict = {
    @CacheEvict(key="#entity.areaId"),
    @CacheEvict(key=ALL_AREA)
  })
  public boolean updateById(Area entity) {
    return super.updateById(entity);
  }

  @Override
  public List<Area> listByPid(Long pId) {
    List<Area> areas = areaMapper.selectList(new LambdaQueryWrapper<Area>().eq(Area::getParentId, pId));
    if(areas==null){
      return Collections.emptyList() ;
    }
    return areas;
  }

}

