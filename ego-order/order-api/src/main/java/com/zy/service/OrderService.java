package com.zy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.domain.OrderVo;
import com.zy.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.entity.OrderSettlement;
import com.zy.service.com.zy.model.OrderResult;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface OrderService extends IService<Order> {

  /**
   * 通过用户的id 查询该用户的订单情况
   * @param userId
   * @return
   */
  OrderResult getOrderCount(String userId);

  /**
   * 安装状态分页查询我们的订单
   * @param page
   * @param order
   * @return
   */
  IPage<Order> findByPage(Page<Order> page, Order order);

  /**
   * 完成支付
   * @param orderSettlement
   * @return
   */
  String toPay(OrderSettlement orderSettlement);

  /**
   * 设置订单的状态为已经支付
   * @param orderSn
   */
  void setPayed(String orderSn);

  /**
   * 设置订单的状态为已经支付
   * @param orderSn
   * @return
   */
  Boolean queryOrderIsPayed(String orderSn);

  /**
   * 提交订单并且生成订单
   * @param orderVo
   */
  void toOrderPay(OrderVo orderVo);
}
