package com.zy.service;

import com.zy.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface UserService extends IService<User> {

  User findUserByUserName(String openId);
}
