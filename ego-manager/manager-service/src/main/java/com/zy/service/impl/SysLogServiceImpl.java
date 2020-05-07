package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.SysLog;
import com.zy.mapper.SysLogMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 系统日志 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {
  @Autowired
  private  SysLogMapper sysLogMapper;

  @Override
  public IPage<SysLog> findByPage(Page<SysLog> page, SysLog sysLog) {
    return sysLogMapper.selectPage(page,
      new LambdaQueryWrapper<SysLog>().
        like(StringUtils.hasText(sysLog.getUsername()),SysLog::getUsername,sysLog.getUsername()).
        like(StringUtils.hasText(sysLog.getOperation()),SysLog::getOperation,sysLog.getOperation())

    );
  }
}
