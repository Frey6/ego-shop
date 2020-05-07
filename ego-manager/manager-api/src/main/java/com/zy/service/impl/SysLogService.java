package com.zy.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.entity.SysLog;

/**
 * <p>
 * 系统日志 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface SysLogService extends IService<SysLog> {

  /**
   * 通过用户名称和用户的操作查询日志
   * @param page
   * @param sysLog
   * @return
   */
  IPage<SysLog> findByPage(Page<SysLog> page, SysLog sysLog);
}
