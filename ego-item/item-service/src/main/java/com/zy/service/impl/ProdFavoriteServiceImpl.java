package com.zy.service.impl;

import com.zy.entity.ProdFavorite;
import com.zy.mapper.ProdFavoriteMapper;
import com.zy.service.ProdFavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.Service;

/**
 * <p>
 * 商品收藏表 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
public class ProdFavoriteServiceImpl extends ServiceImpl<ProdFavoriteMapper, ProdFavorite> implements ProdFavoriteService {

}
