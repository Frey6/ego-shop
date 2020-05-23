package com.zy.service.impl;

import com.zy.entity.Transfee;
import com.zy.mapper.TransfeeMapper;
import com.zy.service.TransfeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class TransfeeServiceImpl extends ServiceImpl<TransfeeMapper, Transfee> implements TransfeeService {

}
