package com.zy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.ProdComm;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品评论 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface ProdCommService extends IService<ProdComm> {

  IPage<ProdComm> findByPage(Page<ProdComm> page, ProdComm prodComm);

  IPage<ProdComm> findPortalPage(Page<ProdComm> page, Long prodId, Integer evaluate);
}
