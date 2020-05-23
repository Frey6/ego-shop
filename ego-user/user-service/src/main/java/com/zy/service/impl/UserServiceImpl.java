package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.entity.User;
import com.zy.mapper.UserMapper;
import com.zy.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.Date;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
@Slf4j
@CacheConfig( cacheNames = "com.zy.service.impl.UserServiceImpl")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

  @Autowired
  private  UserMapper userMapper;

  @Override
  @Cacheable(key = "#openId")
  public User findUserByUserName(String openId) {

    User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, openId));
    return  user;
  }

  @CacheEvict(key = "#entity.userId")
  @Override
  public boolean save(User entity) {
    return super.save(entity);
  }

  @CacheEvict(key = "#entity.userId")
  @Override
  public boolean updateById(User entity) {
//    entity.setModifyTime(new Date());
    return super.updateById(entity);
  }
}
