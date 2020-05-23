package com.zy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.PickAddr;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户配送地址 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface PickAddrService extends IService<PickAddr> {

  IPage<PickAddr> findByPage(Page<PickAddr> page, PickAddr condition);
}
