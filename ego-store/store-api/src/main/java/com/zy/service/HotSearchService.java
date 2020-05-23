package com.zy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.HotSearch;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 热搜 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface HotSearchService extends IService<HotSearch> {

  IPage<HotSearch> findByPage(Page<HotSearch> page, HotSearch hotSearch);

  /**
   * 加载前n条数据
   * @param number
   * @param sort
   * @return
   */
  List<HotSearch> loadTopNHotSearch(Integer number, Integer sort);
}
