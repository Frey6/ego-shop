package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.*;
import com.zy.mapper.*;
import com.zy.service.TransportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Slf4j
@Service(timeout = 10000)
@CacheConfig(cacheNames = "com.zy.service.impl.TransportServiceImpl")
public class TransportServiceImpl extends ServiceImpl<TransportMapper, Transport> implements TransportService{

  @Autowired
  private TransportMapper transportMapper ;

  @Autowired
  private TransfeeMapper transfeeMapper;

  @Autowired
  private TransfeeFreeMapper transfeeFreeMapper;

  @Autowired
  private TranscityMapper transcityMapper;

  @Autowired
  private TranscityFreeMapper transcityFreeMapper;

  @Autowired
  private  AreaMapper areaMapper;

  @Override
  public IPage<Transport> findByPage(Page<Transport> page, Transport condition) {
    page.addOrder(OrderItem.desc("create_time")) ;
    return transportMapper.selectPage(page,new LambdaQueryWrapper<Transport>().
      like(StringUtils.hasText(condition.getTransName()),Transport::getTransName,condition.getTransName()));
  }

  @Cacheable(key = "#id")
  public Transport getById(Serializable id) {
    log.info("查询id为{}的运费模板",id);
    Transport transport = super.getById(id); // 运费模板
    transport.setTransfeeFrees(getTransFeeFress(transport.getTransportId()));
    transport.setTransfees(getTransFees(transport.getTransportId()));
    return transport ;
  }

  /**
   * 收费的模板
   * @param transportId
   * @return
   */
  private List<Transfee> getTransFees(Long transportId) {
    List<Transfee> transfees = transfeeMapper.selectList(new LambdaQueryWrapper<Transfee>().eq(Transfee::getTransportId, transportId));
    if(transfees==null || transfees.isEmpty()){ // null 的处理
      return Collections.emptyList() ;
    }

    for (Transfee transfee : transfees) {
      List<Object> cityIds = transcityMapper.selectObjs(new LambdaQueryWrapper<Transcity>(). // 查询城市的id
        select(Transcity::getCityId).
        eq(Transcity::getTransfeeId, transfee.getTransfeeId()));
      if(cityIds!=null && !cityIds.isEmpty()){
        // 查询城市
        List<Area> areas = areaMapper.selectList(new LambdaQueryWrapper<Area>().in(Area::getAreaId, cityIds));
        transfee.setCityList(areas);
      }
    }
    return  transfees ;
  }

  /**
   * 免费的模板
   * @param transportId
   * @return
   */
  private List<TransfeeFree> getTransFeeFress(Long transportId) {
    List<TransfeeFree> transfeeFrees = transfeeFreeMapper.selectList(new LambdaQueryWrapper<TransfeeFree>().eq(TransfeeFree::getTransportId, transportId));
    if(transfeeFrees==null || transfeeFrees.isEmpty()){ // null 的处理
      return Collections.emptyList() ;
    }

    for (TransfeeFree transfeeFree : transfeeFrees) {
      List<Object> cityIds = transcityFreeMapper.selectObjs(new LambdaQueryWrapper<TranscityFree>(). // 查询城市的id
        select(TranscityFree::getFreeCityId).
        eq(TranscityFree::getTransfeeFreeId, transfeeFree.getTransfeeFreeId()));
      if(cityIds!=null && !cityIds.isEmpty()){
        // 查询城市
        List<Area> areas = areaMapper.selectList(new LambdaQueryWrapper<Area>().in(Area::getAreaId, cityIds));
        transfeeFree.setFreeCityList(areas);
      }
    }
    return  transfeeFrees ;
  }


  @Transactional
  public boolean save(Transport entity) {
    boolean save = super.save(entity); // 处理自己
    if(save){
      handlerFree(entity) ; // 处理包邮的
      handlerFee(entity) ; // 处理收费的
    }
    return save ;
  }

  @CacheEvict(key = "#id")
  @Transactional
  @Override
  public boolean removeById(Serializable id) {
    boolean b = super.removeById(id); // transport
    if(b){
      //删除收费的
      deleteFee(id) ;
      //删除包邮的
      deleteFree(id);
    }
    return b ;
  }

  private void deleteFree(Serializable id) {
    List<Object> transCityFreeIds = transfeeFreeMapper.selectObjs(new LambdaQueryWrapper<TransfeeFree>().
      select(TransfeeFree::getTransfeeFreeId).
      eq(TransfeeFree::getTransportId, id));
    if(transCityFreeIds!=null && !transCityFreeIds.isEmpty()){
      // 删除和城市的中间表
      transcityFreeMapper.delete(new LambdaQueryWrapper<TranscityFree>().in(TranscityFree::getTransfeeFreeId,transCityFreeIds)) ;
    }

    // 删除自己
    transfeeFreeMapper.delete(new LambdaQueryWrapper<TransfeeFree>().
      eq(TransfeeFree::getTransportId, id)) ;

  }

  private void deleteFee(Serializable id) {
    List<Object> transCityIds = transfeeMapper.selectObjs(new LambdaQueryWrapper<Transfee>().
      select(Transfee::getTransfeeId).
      eq(Transfee::getTransportId, id));
    if(transCityIds!=null && !transCityIds.isEmpty()){
      // 删除和城市的中间表
      transcityMapper.delete(new LambdaQueryWrapper<Transcity>().in(Transcity::getTransfeeId,transCityIds)) ;
    }

    // 删除自己
    transfeeMapper.delete(new LambdaQueryWrapper<Transfee>().
      eq(Transfee::getTransportId, id)) ;
  }

  @Transactional
  @CacheEvict(key = "#entity.transportId")
  public boolean updateById(Transport entity) {
    boolean flag = removeById(entity.getTransportId()) ;
    if(flag){
//      updateTransFees(entity);
//      updateTransFeeFrees(entity);
      save(entity) ;
      return  true ;
    }
   return false ;
  }

//  /**
//   * 修改免费的
//   * @param entity
//   */
//  private void updateTransFeeFrees(Transport entity) {
//    List<TransfeeFree> transfeeFrees = entity.getTransfeeFrees();
//    if (transfeeFrees==null||transfeeFrees.isEmpty()){
//      return;
//    }
//    List<Object> transCityFreeIds = transfeeFreeMapper.selectObjs(new LambdaQueryWrapper<TransfeeFree>().select(TransfeeFree::getTransfeeFreeId).eq(TransfeeFree::getTransportId, entity.getTransportId()));
//    ArrayList<Long> transCityFreeIdLongs = new ArrayList<>(transCityFreeIds.size());
//    transCityFreeIds.forEach((k)->transCityFreeIdLongs.add(Long.valueOf(k.toString())));
//    for (TransfeeFree transfeeFree : transfeeFrees) {
//      if (transfeeFree.getTransfeeFreeId()!=null){
//        transCityFreeIdLongs.remove(transfeeFree.getTransfeeFreeId());
//        updateTransfeeFree(transfeeFree);
//      }
//      else {
//        insertTransFeeFree(entity,transfeeFree);
//      }
//    }
//    if (!transCityFreeIdLongs.isEmpty()){
//      transcityMapper.delete(new LambdaQueryWrapper<Transcity>().in(Transcity::getTransfeeId,transCityFreeIdLongs)) ;
//      transfeeFreeMapper.delete(new LambdaQueryWrapper<TransfeeFree>().
//        eq(TransfeeFree::getTransfeeFreeId,transCityFreeIdLongs)) ;
//    }
//  }
//
//  private void updateTransfeeFree(TransfeeFree transfeeFree) {
//    int i = transfeeFreeMapper.updateById(transfeeFree);
//    if (i>0){
//      transcityFreeMapper.delete(new LambdaQueryWrapper<TranscityFree>().eq(TranscityFree::getTransfeeFreeId,transfeeFree.getTransfeeFreeId())) ;
//
//      List<Area> freeCityList = transfeeFree.getFreeCityList();
//      if(!freeCityList.isEmpty()){
//        for (Area area : freeCityList) {
//          TranscityFree transcityFree = new TranscityFree();
//          transcityFree.setFreeCityId(area.getAreaId());
//          transcityFree.setTransfeeFreeId(transfeeFree.getTransfeeFreeId().longValue());
//          transcityFreeMapper.insert(transcityFree) ;
//        }
//      }
//    }
//
//  }
//
//  /**
//   * 修改收费的
//   * @param entity
//   */
//  private void updateTransFees(Transport entity) {
//    List<Transfee> transfees = entity.getTransfees();
//    if (transfees==null||transfees.isEmpty()){
//      return;
//    }
//    List<Object> transCityIds = transfeeMapper.selectObjs(new LambdaQueryWrapper<Transfee>().
//      select(Transfee::getTransfeeId).
//      eq(Transfee::getTransportId, entity.getTransportId()));
//    ArrayList<Long> transCityIdLongs = new ArrayList<>(transCityIds.size());
//    transCityIds.forEach((k)->transCityIdLongs.add(Long.valueOf(k.toString())));
//    for (Transfee transfee : transfees) {
//      if (transfee.getTransfeeId()!=null){
//        transCityIdLongs.remove(transfee.getTransfeeId());
//        updateTransfeeFree(transfee);
//      }
//      else {
//        insertTransFeeFree(entity,transfee);
//      }
//    }
//    if (!transCityIdLongs.isEmpty()){
//      transcityMapper.delete(new LambdaQueryWrapper<Transcity>().in(Transcity::getTransfeeId,transCityIdLongs)) ;
//      transfeeMapper.delete(new LambdaQueryWrapper<Transfee>().
//        eq(Transfee::getTransfeeId,transCityIdLongs)) ;
//    }
//  }
//
//  private void updateTransfee(TransfeeFree transfeeFree) {
//    int i = transfeeFreeMapper.updateById(transfeeFree);
//    if (i>0){
//      transcityFreeMapper.delete(new LambdaQueryWrapper<TranscityFree>().eq(TranscityFree::getTransfeeFreeId,transfeeFree.getTransfeeFreeId())) ;
//
//      List<Area> freeCityList = transfeeFree.getFreeCityList();
//      if(!freeCityList.isEmpty()){
//        for (Area area : freeCityList) {
//          TranscityFree transcityFree = new TranscityFree();
//          transcityFree.setFreeCityId(area.getAreaId());
//          transcityFree.setTransfeeFreeId(transfeeFree.getTransfeeFreeId().longValue());
//          transcityFreeMapper.insert(transcityFree) ;
//        }
//      }
//    }
//
//  }

  /**
   *
   * @param entity
   */
  private void handlerFree(Transport entity) {
    if(entity.getIsFreeFee().equals((byte)1)){   //买家包邮
      return;
    }
    // 条件包邮
    List<TransfeeFree> transfeeFrees = entity.getTransfeeFrees();
    if(!transfeeFrees.isEmpty()){
      for (TransfeeFree transfeeFree : transfeeFrees) {
        insertTransFeeFree(entity, transfeeFree);
      }
    }
  }

  private void insertTransFeeFree(Transport entity, TransfeeFree transfeeFree) {
    transfeeFree.setTransportId(entity.getTransportId());
    int insert = transfeeFreeMapper.insert(transfeeFree);
    if(insert>0){
      List<Area> freeCityList = transfeeFree.getFreeCityList();
      if(!freeCityList.isEmpty()){
        for (Area area : freeCityList) {
          TranscityFree transcityFree = new TranscityFree();
          transcityFree.setFreeCityId(area.getAreaId());
          transcityFree.setTransfeeFreeId(transfeeFree.getTransfeeFreeId().longValue());
          transcityFreeMapper.insert(transcityFree) ;
        }
      }
    }
  }

  private void handlerFee(Transport entity) {
    if(entity.getIsFreeFee().equals((byte)1)){ // 若是包邮，里面有个收费的，只不过，收费为0 圆
      Transfee transfee = entity.getTransfees().get(0);
      transfee.setTransportId(entity.getTransportId());
      transfeeMapper.insert(transfee);
      return;
    }
    List<Transfee> transfees = entity.getTransfees(); // 所有的收费的情况
    if(!transfees.isEmpty()){
      for (Transfee transfee : transfees) {
        insertTransFee(entity, transfee);
      }
    }

  }

  private void insertTransFee(Transport entity, Transfee transfee) {
    transfee.setTransportId(entity.getTransportId());
    int insert = transfeeMapper.insert(transfee);
    if(insert>0){
      List<Area> cityList = transfee.getCityList();
      if(!cityList.isEmpty()){
        for (Area area : cityList) {
          Transcity transcity = new Transcity();
          transcity.setCityId(area.getAreaId());
          transcity.setTransfeeId(transfee.getTransfeeId());
          transcityMapper.insert(transcity);
        }
      }
    }
  }


}

