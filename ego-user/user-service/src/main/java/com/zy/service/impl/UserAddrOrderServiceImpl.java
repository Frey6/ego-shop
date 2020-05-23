package com.zy.service.impl;

import com.zy.entity.UserAddrOrder;
import com.zy.mapper.UserAddrOrderMapper;
import com.zy.service.UserAddrOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.Service;

/**
 * <p>
 * 用户订单配送地址 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
public class UserAddrOrderServiceImpl extends ServiceImpl<UserAddrOrderMapper, UserAddrOrder> implements UserAddrOrderService {

}
