package com.zy.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.entity.UserAddr;
import com.zy.mapper.UserAddrMapper;
import com.zy.service.UserAddrService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户配送地址 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
@Slf4j
@CacheConfig(cacheNames ="com.zy.service.impl.UserAddrServiceImpl" )
public class UserAddrServiceImpl extends ServiceImpl<UserAddrMapper, UserAddr> implements UserAddrService {

  @Autowired
  private UserAddrMapper userAddrMapper;

  @Autowired
  private RedisTemplate redisTemplate;

  @Override
  @Cacheable(key = "#userId")
  public List<UserAddr> findUserAdderss(String userId) {
   log.info("查询用户id{}的地址",userId);
 return    userAddrMapper.selectList(new LambdaQueryWrapper<UserAddr>().
   eq(UserAddr::getUserId,userId).
   eq(UserAddr::getStatus,1).
   orderByAsc(UserAddr::getStatus,UserAddr::getUpdateTime));

  }

  @Override
  @Transactional
  @CacheEvict(key = "#userId")
  public void setDefaultAddr(String userId, Long id) {
    UserAddr userAddr = getById(id);
    if (userAddr==null||!userAddr.getUserId().equals(userId)){
      throw new RuntimeException("用户地址不存在，或者用户的id不匹配");
    }
    UserAddr updateUserAddr = new UserAddr();
    updateUserAddr.setCommonAddr(0);
     userAddrMapper.update(updateUserAddr,new LambdaQueryWrapper<UserAddr>().
       eq(UserAddr::getUserId,userId).
       eq(UserAddr::getCommonAddr,1));
     userAddr.setCommonAddr(1);
     userAddr.setUpdateTime(new Date());
     updateById(userAddr);
  }

  @Override
  public UserAddr getUserDefaultAddr(String userId) {
    UserAddr userAddr = userAddrMapper.selectOne(new LambdaQueryWrapper<UserAddr>().eq(UserAddr::getUserId, userId).eq(UserAddr::getCommonAddr, 1));

    return userAddr;
  }

  @Override
  @CacheEvict(key = "#entity.userId")
  public boolean save(UserAddr entity) {
    log.info("新增一个用户{}", JSONUtil.toJsonPrettyStr(entity));
    entity.setCreateTime(new Date());
    entity.setUpdateTime(new Date());
    entity.setStatus(1);
    Integer count = userAddrMapper.selectCount(new LambdaQueryWrapper<UserAddr>().
      eq(UserAddr::getUserId, entity.getUserId()).
      eq(UserAddr::getStatus, 1));
    if (count==null||count==0){
      entity.setCommonAddr(0);
    }
    return super.save(entity);
  }

  @Override
  public boolean removeById(Serializable id) {
    log.info("删除用户{}的收货地址",id);
    UserAddr userAddr = getById(id);
    if (userAddr==null){
      throw  new IllegalArgumentException("删除用户的地址为空");
    }
    String userId = userAddr.getUserId();
    redisTemplate.delete("com.zy.service.impl.UserAddrServiceImpl::"+userId);
    return super.removeById(id);
  }

  @Override
  @CacheEvict(key = "#entity.userId")
  public boolean updateById(UserAddr entity) {
    entity.setUpdateTime(new Date());
    return super.updateById(entity);
  }
}
