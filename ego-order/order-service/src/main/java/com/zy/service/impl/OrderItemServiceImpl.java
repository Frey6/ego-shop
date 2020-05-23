package com.zy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.entity.OrderItem;
import com.zy.mapper.OrderItemMapper;
import com.zy.service.OrderItemService;
import org.apache.dubbo.config.annotation.Service;

/**
 * <p>
 * 订单项 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

}
