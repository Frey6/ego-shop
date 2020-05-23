package com.zy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.IndexImg;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 主页轮播图 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface IndexImgService extends IService<IndexImg> {

  IPage<IndexImg> findByPage(Page<IndexImg> page, IndexImg condition);

  List<IndexImg> loadIndexImgs();
}
