package com.zy.service;

import com.zy.entity.UserAddr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户配送地址 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface UserAddrService extends IService<UserAddr> {

  /**
   * 通过用户的id查询用户的地址
   * @param userId
   * @return
   */
  List<UserAddr> findUserAdderss(String userId);

  /**
   * 将id设置为user的默认收货地址
   * @param userId
   * @param id
   */
  void setDefaultAddr(String userId, Long id);

  /**
   * 设置用户的默认的收货地址
   * @param userId
   * @return
   */
  UserAddr getUserDefaultAddr(String userId);
}
