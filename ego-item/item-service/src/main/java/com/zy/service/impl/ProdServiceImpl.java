package com.zy.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.domain.ProdCommSurvey;
import com.zy.entity.Prod;
import com.zy.entity.ProdComm;
import com.zy.entity.ProdTagReference;
import com.zy.entity.Sku;
import com.zy.mapper.*;
import com.zy.service.ProdService;
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
import java.util.*;

/**
 * <p>
 * 商品 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service(retries = 0,timeout = 10000)
@Slf4j
@CacheConfig(cacheNames ="com.zy.service.impl.ProdServiceImpl" )
public class ProdServiceImpl extends ServiceImpl<ProdMapper, Prod> implements ProdService {

  @Autowired
  private  ProdMapper prodMapper;
  @Autowired
  private SkuMapper skuMapper;
  @Autowired
  private ProdTagReferenceMapper prodTagReferenceMapper;
  @Autowired
  private ProdCommMapper prodCommMapper;

  @CacheEvict(key = "#id")
  @Override
  @Transactional
  public boolean removeById(Serializable id) {

    // 逻辑删除的实现 ，将状态修改为-1 就可以了
    Prod prod = getDeleteProd(id);
    boolean flag = super.updateById(prod);// 逻辑删除
    if(flag){ //  商品表的数据和很多的表都有关系
      removeAllRealtion(id);
    }
    return flag;
  }

  private Prod getDeleteProd(Serializable id) {
    Prod prod = new Prod();
    prod.setProdId((Long)id);
    prod.setStatus(-1);
    return prod;
  }


//  @Override
//  public boolean removeByIds(Collection<? extends Serializable> idList) {
//    List<Prod> prods = null ;
//    boolean flag = false;
//    if(idList!=null && !idList.isEmpty()){
//      prods = new ArrayList<Prod>(idList.size());
//      for (Serializable id : idList) {
//        prods.add(getDeleteProd(id)) ;
//      }
//      flag =  super.updateBatchById(prods);
//    }
//
//    removeAllRealtion(idList) ;
//    return flag ;
//  }


//  /**
//   * 移除相关的单个值
//   * @param id
//   */
//  private void removeAllRealtion(Serializable id) {
//
//  }

//  /**
//   * 移除相关的多个值
//   * @param ids
//   */
//  private void removeAllRealtion(Collection<? extends Serializable> ids) {
//
//  }


  @Override
  public IPage<Prod> findByPage(Page<Prod> page, Prod prod) {
    page.addOrder(OrderItem.desc("update_time"));
    return prodMapper.selectPage(page,new LambdaQueryWrapper<Prod>().
        select(Prod.class,tableFieldInfo -> {
          return  !tableFieldInfo.getColumn().equals("content");
        }).
      like(StringUtils.hasText(prod.getProdName()),Prod::getProdName,prod.getProdName()).
      eq(prod.getStatus()!=null,Prod::getStatus,prod.getStatus()).
      ne(prod.getStatus()==null,Prod::getStatus,-1)

    );
  }

  @Override
  public Prod getSimpleProd(Long relation) {
    return prodMapper.selectOne(new LambdaQueryWrapper<Prod>().select(Prod::getProdName,Prod::getPic).eq(Prod::getProdId,relation));
  }


  @Override
  public Integer getTotal(Date t1, Date t2) {
    Integer count = prodMapper.selectCount(new LambdaQueryWrapper<Prod>().
      between(t1 != null && t2 != null, Prod::getUpdateTime, t1, t2));
    return count;
  }


  @Override
  public IPage<Prod> selectPage(int current, Integer size, Date t1, Date t2) {
    // 小的优化，因为分页查询时，总是要统计总条数，所以会发一个统计总条数的sql
    Page<Prod> prodPage = new Page<>(current, size,false);
    IPage<Prod> prodIPage = prodMapper.selectPage(prodPage, new LambdaQueryWrapper<Prod>().
      between(t1 != null && t2 != null, Prod::getUpdateTime, t1, t2));
    List<Prod> records = prodIPage.getRecords();
    if(!records.isEmpty()){
      for (Prod record : records) {
        // 得到一个商品的标签
        List<Object> tagObjIds = prodTagReferenceMapper.selectObjs(new LambdaQueryWrapper<ProdTagReference>().
          select(ProdTagReference::getTagId).eq(ProdTagReference::getProdId, record.getProdId()));
        if(tagObjIds!=null && !tagObjIds.isEmpty()){
          List<Long> tagIds = new ArrayList<>(tagObjIds.size());
          tagObjIds.forEach((k)->{
            tagIds.add(Long.valueOf(k.toString())) ;
          });
          record.setTagList(tagIds);
        }
        // 得到一个商品的评率的预览
        ProdCommSurvey prodCommSurvey =  getProdCommSurvey(record.getProdId()) ;
        record.setPraiseNumber(prodCommSurvey.getPraiseNumber());
        record.setPositiveRating(prodCommSurvey.getPositiveRating());
      }
    }
    return prodIPage ;
  }

  @Override
  public ProdCommSurvey getProdCommSurvey(Long prodId) {
    // 总评论
    Integer totalComment = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>().
      eq(ProdComm::getProdId, prodId));
    // 好评
    Integer praiseNumber = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>().
      eq(ProdComm::getProdId, prodId).
      eq(ProdComm::getEvaluate, 0));
    Integer secondaryNumber = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>().
      eq(ProdComm::getProdId, prodId).
      eq(ProdComm::getEvaluate, 1));
    Integer negativeNumber = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>().
      eq(ProdComm::getProdId, prodId).
      eq(ProdComm::getEvaluate, 2));
    Integer picNumber = prodCommMapper.selectCount(new LambdaQueryWrapper<ProdComm>().
      eq(ProdComm::getProdId, prodId).
      isNotNull(ProdComm::getPics));
    ProdCommSurvey prodCommSurvey = new ProdCommSurvey(praiseNumber.longValue(), totalComment.longValue());
    prodCommSurvey.setSecondaryNumber((long) (secondaryNumber == null ? 0 : secondaryNumber)); // 中评
    prodCommSurvey.setNegativeNumber((long) (negativeNumber == null ? 0 : negativeNumber)); // 差评
    prodCommSurvey.setPicNumber((long) (picNumber == null ? 0 : picNumber)); // 有图的评
    return prodCommSurvey;
  }

  @Override
  public void decrStocks(Map<Long, Integer> prodStocks) {
    stockOperation(prodStocks,false)  ;
  }

  @Override
  public void addStocks(Map<Long, Integer> prodStocks) {
    stockOperation(prodStocks,true)  ;
  }

  private void stockOperation(Map<Long, Integer> prodStocks,boolean isAdd) {
    if(prodStocks!=null && !prodStocks.isEmpty()){
      prodStocks.forEach((skuId,stocks)->{
        Prod prod = prodMapper.selectById(skuId);
        Integer dbStock = prod.getTotalStocks();
        Integer currentStock = 0 ;
        if(isAdd){
          currentStock = dbStock + stocks ;
        }else{
          currentStock = dbStock - stocks ;
        }
        if(currentStock<0){
          throw  new IllegalArgumentException("库存不足") ;
        }else{
          prod.setTotalStocks(currentStock);
          prod.setUpdateTime(new Date());
          prodMapper.updateById(prod) ;
        }
      });
    }
  }


  /**
   * 新增一个商品
   * @param entity
   * @return
   */
  @Override
  @Transactional
  public boolean save(Prod entity) {
    log.info("新增一个商品，{}",entity.getProdName());
    Prod.DeliveryModeVo deliveryModeVo = entity.getDeliveryModeVo();
    entity.setDeliveryMode(JSONUtil.toJsonStr(deliveryModeVo)); // 商品的配送的物流 需要一个json的工具类
    entity.setCreateTime(new Date());
    entity.setUpdateTime(new Date());
//    Sku sku = skuMapper.selectOne(new LambdaQueryWrapper<Sku>().eq(Sku::getProdId, entity.getProdId()));
//    entity.setOriPrice(sku.getOriPrice());
    entity.setVersion(0);
    if(entity.getStatus().equals(1)){
      entity.setPutawayTime(new Date());

    }
    entity.setSoldNum(0);
    boolean save = super.save(entity);
    if(save){
      handlerSkuList(entity.getProdId() ,entity.getSkuList());
      handlerTagList(entity.getProdId(),entity.getTagList()) ;
    }
    return save ;
  }

  /**
   * 处理商品的sku 信息
   * @param skuList
   */
  private void handlerSkuList(Long prodId ,List<Sku> skuList) {
    if(skuList==null || skuList.isEmpty()){
      return;
    }
    for (Sku sku : skuList) {
      sku.setProdId(prodId);
      sku.setActualStocks(sku.getStocks()); // 实际的库存
      sku.setIsDelete((byte) 0);
      sku.setRecTime(new Date());
      sku.setUpdateTime(new Date());
      sku.setVersion(0);
      skuMapper.insert(sku) ;
    }
  }

  /**
   * 处理商品的标签数据
   * @param tagList
   */
  private void handlerTagList(Long prodId ,List<Long> tagList) {
    if(tagList==null || tagList.isEmpty()){
      return;
    }
    for (Long tagId : tagList) {
      ProdTagReference prodTagReference = new ProdTagReference();
      prodTagReference.setTagId(tagId);
      prodTagReference.setProdId(prodId);
      prodTagReference.setCreateTime(new Date());
      prodTagReference.setStatus(true);
      prodTagReferenceMapper.insert(prodTagReference) ;
    }
  }

  @Cacheable(key ="#id")
  @Override
  public Prod getById(Serializable id) {
    log.info("查询商品id{}的商品",id);
    Prod prod = super.getById(id);
    prod.setDeliveryModeVo(JSONUtil.toBean(prod.getDeliveryMode(),Prod.DeliveryModeVo.class));
    prod.setSkuList(getProdSkuList(id));
    prod.setTagList(getTagList(id));
    return prod;
  }

  /**
   * 查询商品的sku 信息
   * @param id
   * @return
   */
  private List<Sku> getProdSkuList(Serializable id) {
    List<Sku> skus = skuMapper.selectList(new LambdaQueryWrapper<Sku>().eq(Sku::getProdId, id));
    if(skus==null){
      return Collections.emptyList() ;
    }
    return  skus ;
  }

  private List<Long> getTagList(Serializable id) {
    List<Object> tagIdObjs = prodTagReferenceMapper.selectObjs(
      new LambdaQueryWrapper<ProdTagReference>().
        select(ProdTagReference::getTagId).
        eq(ProdTagReference::getProdId, id)
    );
    if(tagIdObjs==null){
      return  Collections.emptyList() ;
    }
    List<Long> tagIds = new ArrayList<>(tagIdObjs.size());
    tagIdObjs.forEach(k->{
      tagIds.add((Long)k) ;
    });
    return tagIds ;
  }



  @Override
  public boolean removeByIds(Collection<? extends Serializable> idList) {
    List<Prod> prods = null ;
    boolean flag = false;
    if(idList!=null && !idList.isEmpty()){
      prods = new ArrayList<Prod>(idList.size());
      for (Serializable id : idList) {
        prods.add(getDeleteProd(id)) ;
      }
      flag =  super.updateBatchById(prods);
    }

    removeAllRealtion(idList) ;
    return flag ;
  }


  /**
   * 移除相关的单个值
   * 商品商品，对于的sku ，tagRef 都都应该被上传
   * @param id
   */
  private void removeAllRealtion(Serializable id) {
    removeSkuByProdId(id);
    removeTagRefByProdId(id);

  }

  private void removeTagRefByProdId(Serializable id) {
    log.info("移除商品{}的tag 信息",id);
    // 这里Mybatis-plus 会把该删除操作自动变为为修改操作
    prodTagReferenceMapper.delete(new LambdaQueryWrapper<ProdTagReference>().eq(ProdTagReference::getProdId,id)) ;
  }

  private void removeSkuByProdId(Serializable id) {
    log.info("移除商品{}的sku 信息",id);
    // 这里Mybatis-plus 会把该删除操作自动变为为修改操作
    skuMapper.delete(new LambdaQueryWrapper<Sku>().eq(Sku::getProdId,id)) ;
  }

  /**
   * 移除相关的多个值
   * @param ids
   */
  private void removeAllRealtion(Collection<? extends Serializable> ids) {
    removeSkuByProdIds(ids);
    removeTagRefByProdIds(ids);
  }

  private void removeTagRefByProdIds(Collection<? extends Serializable> ids) {
    log.info("移除商品{}的tag 信息",ids);
    prodTagReferenceMapper.delete(new LambdaQueryWrapper<ProdTagReference>().in(ProdTagReference::getProdId,ids)) ;
  }

  private void removeSkuByProdIds(Collection<? extends Serializable> ids) {
    log.info("移除商品{}的sku 信息",ids);
    // 这里Mybatis-plus 会把该删除操作自动变为为修改操作
    skuMapper.delete(new LambdaQueryWrapper<Sku>().in(Sku::getProdId,ids)) ;
  }

  @CacheEvict(key = "entity.prodId")
  @Transactional
  @Override
  public boolean updateById(Prod entity) {
    log.info("修改商品数据{}",JSONUtil.toJsonPrettyStr(entity));
    entity.setCreateTime(null); // mysql 就不会覆盖它
    entity.setUpdateTime(new Date());
    if(entity.getStatus().equals(1)){
      entity.setPutawayTime(new Date());
    }
    entity.setVersion(entity.getVersion()+1);
    boolean b = super.updateById(entity);
    if(b){
      updateSkuList(entity.getProdId(),entity.getSkuList());
      // 中间表，结构简单，修改比较容易
      updateTagList(entity.getProdId(),entity.getTagList());
    }
    return b ;
  }

  private void updateTagList(Long prodId, List<Long> tagList) {
    prodTagReferenceMapper.delete(new LambdaQueryWrapper<ProdTagReference>().eq(ProdTagReference::getProdId,prodId));
    handlerTagList(prodId,tagList);
  }

  /**
   * 对sku的处理
   * @param prodId
   * @param skuList
   * 对sku的处理，可能包含2 个部分，该删除的删除，该修改的修改，该新增的新增
   */
  private void updateSkuList(Long prodId, List<Sku> skuList) {
//        skuList// 新增的，修改的数据 ，对于新增的，没有id，对于修改的，有id
    if(skuList==null || skuList.isEmpty()){ // 本次的商品的sku 数据，可能为null ，可能被删除
      // 我们这里对sku做删除的操作
      skuMapper.delete(new LambdaQueryWrapper<Sku>().eq(Sku::getProdId,prodId)) ;
    }
    // 查询本商品之前的sku的数据 ,之前的所有的数据： 存在2 种情况，要么被删除，要么被修改
    List<Object> objects = skuMapper.selectObjs(new LambdaQueryWrapper<Sku>().select(Sku::getSkuId).eq(Sku::getProdId, prodId));
    List<Integer> longs = Collections.emptyList() ;
    if(objects!=null && !objects.isEmpty()){
      longs = new ArrayList<>(objects.size());
      for (Object object : objects) {
        longs.add(Integer.valueOf(object.toString())) ;
      }
    }

    List<Sku> newSkus = new ArrayList<>() ;
    List<Sku> updateSkus = new ArrayList<>() ;
    for (Sku sku : skuList) {
      if(sku.getSkuId()==null){
        newSkus.add(sku);
      }else{
        updateSkus.add(sku) ;
        longs.remove(sku.getSkuId()) ;
      }
    }
    handlerSkuList(prodId,newSkus); // 新增的情况

    // 修改的情况
    for (Sku skus : updateSkus) {
      skus.setRecTime(null);
      skus.setUpdateTime(new Date());
      skuMapper.updateById(skus) ;
    }

    // 删除的情况
    skuMapper.delete(new LambdaQueryWrapper<Sku>().in(Sku::getSkuId,longs)) ;
  }


}
