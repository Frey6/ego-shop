package com.zy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.entity.OrderSettlement;
import com.zy.mapper.OrderSettlementMapper;
import com.zy.service.OrderSettlementService;
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
public class OrderSettlementServiceImpl extends ServiceImpl<OrderSettlementMapper, OrderSettlement> implements OrderSettlementService {

}
