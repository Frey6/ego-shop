package com.zy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.entity.OrderRefund;
import com.zy.mapper.OrderRefundMapper;
import com.zy.service.OrderRefundService;
import org.apache.dubbo.config.annotation.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
public class OrderRefundServiceImpl extends ServiceImpl<OrderRefundMapper, OrderRefund> implements OrderRefundService {

}
