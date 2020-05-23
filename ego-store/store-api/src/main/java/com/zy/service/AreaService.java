package com.zy.service;

import com.zy.entity.Area;
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
public interface AreaService extends IService<Area> {

  List<Area> listByPid(Long pId);
}
