package com.zy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.UserCollection;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface UserCollectionService extends IService<UserCollection> {
  /**
   * 查询用户是否收藏这个商品
   * @param userId
   * @param prodId
   * @return
   */
  Boolean isCollection(String userId, Long prodId);

  void addOrCancel(String userId, Long prodId);

  /**
   * 计算用户的收藏数
   * @param userId
   * @return
   */
  Integer getCollectionCount(String userId);

  /**
   * 得到用户收藏商品的ids
   * @param userId
   * @param page
   * @return
   */
  Page<Long> getProdList(String userId, Page<UserCollection> page);
}
