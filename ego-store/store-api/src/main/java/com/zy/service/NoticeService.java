package com.zy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.Notice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface NoticeService extends IService<Notice> {

  IPage<Notice> findByPage(Page<Notice> page, Notice notice);

  /**
   * 加载首页的通知
   * @return
   */
  List<Notice> loadIndexTopNotice();
}
