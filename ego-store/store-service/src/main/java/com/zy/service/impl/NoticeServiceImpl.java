package com.zy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.Notice;
import com.zy.mapper.NoticeMapper;
import com.zy.service.NoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.util.StringUtils;

import java.io.Serializable;
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
@Slf4j
@CacheConfig(cacheNames = "com.zy.service.impl.NoticeServiceImpl")
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService{

  private static final String INDEX_TOP_NOTICE = "'index-top-notice'" ;

  @Autowired
  private NoticeMapper noticeMapper ;



  @Override
  public IPage<Notice> findByPage(Page<Notice> page, Notice notice) {
    page.addOrder(OrderItem.desc("update_time")) ;
    return noticeMapper.selectPage(page,
      new LambdaQueryWrapper<Notice>().
        like(StringUtils.hasText(notice.getTitle()),Notice::getTitle,notice.getTitle()).
        eq(notice.getStatus()!=null,Notice::getStatus,notice.getStatus()).
        eq(notice.getIsTop()!=null,Notice::getIsTop,notice.getIsTop())
    );
  }

  @Cacheable(key=INDEX_TOP_NOTICE)
  @Override
  public List<Notice> loadIndexTopNotice() {
    return noticeMapper.selectList(new LambdaQueryWrapper<Notice>().
      eq(Notice::getIsTop,1).
      eq(Notice::getStatus,1).
      orderByAsc(Notice::getPublishTime)
    );
  }


  /**
   * 缓存是针对一个方法 ，缓存必须有key，也必须有value
   * key：我们可以自己定义，但是value 肯定是方法的返回值
   * @param id
   * @return
   */
//    @Cacheable   // 开启缓存 ，在执行该方法之前，先查询缓存，若缓存里面有值，直接返回缓存的值，若没有值，先执行方法的调用，调用成功后，把方法调用的结构放在缓存里面
//    //  解决缓存的脏读问题
//    @CachePut  // 把方法的执行结果放在缓存里面，每次都会触发方法的调用，以及缓存的放入
//    @CacheEvict // 删除缓存，可以设置方法执行之前或方法执行之后删除缓存
//
//    @Caching //若我们要操作多个缓存（多个key），以及操作形式都不同（开启，更新，删除缓存） ，我们在Caching做排列组合
//    @CacheConfig // 配置缓存，它的作用就是简化我（Cacheable，CachePut，CacheEvict写法）
//   @Cacheable(cacheNames = "com.sxt.service.impl.NoticeServiceImpl",key = "${id}") // cacheNames 缓存的前缀，类似一个命名空间，做缓存的隔离使用
  // 最后我们拼接出来：key完整为: com.sxt.service.impl.NoticeServiceImpl::id value:你方法的返回值
  @Cacheable(key = "#id")
  public Notice getById(Serializable id) {
    log.info("查询id{}的值",id);
    return super.getById(id);
  }


  @Caching(evict = {@CacheEvict(key=INDEX_TOP_NOTICE)})
  public boolean save(Notice entity) {
    return super.save(entity);
  }

  @Caching(evict = {@CacheEvict(key = "#id") ,@CacheEvict(key=INDEX_TOP_NOTICE)})
  // 删除缓存的操作
  public boolean removeById(Serializable id) {
    log.info("删除id为{}的公告",id);
    return super.removeById(id);
  }

  //    @CachePut(key = "#entity.id") // 这个不行，因为我们该方法的返回值不是Notice ，若在此缓存，则会缓存一个boolean 值
  //
  // 删除缓存 ,可以设置在方法之前删除缓存值，或者在方法之前删除缓存的值，默认为在方法之后删除换成的的值
  // beforeInvocation 代表在方法的执行之前删除它，之后可以不用操作
  @Caching(evict = {@CacheEvict(key = "#entity.id",beforeInvocation = true)  ,@CacheEvict(key=INDEX_TOP_NOTICE)})
  public boolean updateById(Notice entity) {
    log.info("修改id为{}的公告",entity.getId());
    entity.setUpdateTime(new Date());
    boolean b = super.updateById(entity);
//       // 如果你想强行添加缓存的话，你可以调用 getById(entity.getId()) ,但是没有效果
////        this.getById(entity.getId()) ; // 这是在方法内部调用 的？
    NoticeService noticeServiceProxy = (NoticeService)AopContext.currentProxy();// 它就是获取我们对象的代理对象
//         if(noticeServiceProxy!=null){
//             noticeServiceProxy.getById(entity.getId()) ;
//         }
    return b ;
  }

  /**
   * 这样的做法，还是有问题
   * 因为你无法保证Notice 里面的值是完整的、
   * 若我们只修改它里面的一个属性呢
   * notice.context ="xxx"
   * 只有从数据库里面查询一个该值，该值才是完整的
   * @param notice
   * @return
   */
//    @CachePut(key = "#notice.id")
//    public Notice update(Notice notice){
//        // 修改的操作；；
//        // 进行数据库的修改操作
//        return  notice ;
//    }
}

