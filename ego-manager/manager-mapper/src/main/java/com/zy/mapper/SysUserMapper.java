package com.zy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zy.entity.SysUserRole;

/**
 * <p>
 * 系统用户 Mapper 接口
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

  Integer selectCount(LambdaQueryWrapper<SysUserRole> in);
}
