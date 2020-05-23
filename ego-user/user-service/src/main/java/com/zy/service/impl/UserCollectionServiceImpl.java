package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.UserCollection;
import com.zy.mapper.UserCollectionMapper;
import com.zy.service.UserCollectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements UserCollectionService {

  @Autowired
  private UserCollectionMapper userCollectionMapper;

  @Override
  public Boolean isCollection(String userId, Long prodId) {
    Integer count = userCollectionMapper.selectCount(new LambdaQueryWrapper<UserCollection>().eq(UserCollection::getUserId, userId).eq(UserCollection::getProdId, prodId));
    if (count!=null&&count>0){
      return  Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  @Override
  public void addOrCancel(String userId, Long prodId) {
    Boolean collection = isCollection(userId, prodId);
    if (collection){
      userCollectionMapper.delete(new LambdaQueryWrapper<UserCollection>().
        eq(UserCollection::getUserId,userId).
        eq(UserCollection::getProdId,prodId));
    }
    else {
      UserCollection userCollection = new UserCollection();
      userCollection.setCreateTime(new Date());
      userCollection.setUserId(userId);
      userCollection.setProdId(prodId);
       userCollectionMapper.insert(userCollection);
    }
  }

  @Override
  public Integer getCollectionCount(String userId) {
    Integer count = userCollectionMapper.selectCount(new LambdaQueryWrapper<UserCollection>().eq(UserCollection::getUserId, userId));
    return count==null?0:count;
  }

  @Override
  public Page<Long> getProdList(String userId, Page<UserCollection> page) {
    IPage<UserCollection> userCollectionIPage = userCollectionMapper.selectPage(page, new LambdaQueryWrapper<UserCollection>().
      select(UserCollection::getProdId).
      eq(UserCollection::getUserId,userId));
    Page<Long> objectPage = new Page<>(page.getCurrent(), page.getSize());
    objectPage.setTotal(userCollectionIPage.getTotal());
    if (userCollectionIPage.getRecords().isEmpty()){
      return  objectPage;
    }
    List<UserCollection> records = userCollectionIPage.getRecords();
    ArrayList<Long> listData = new ArrayList<>();
    for (UserCollection record : records) {
      listData.add(record.getProdId());
    }
    objectPage.setRecords(listData);
    return  objectPage;
  }
}
