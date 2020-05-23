package com.zy.aspect;


import cn.hutool.json.JSONUtil;
import com.zy.anno.Log;
import com.zy.entity.SysLog;
import com.zy.entity.SysUser;
import com.zy.service.impl.SysLogService;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Component
@Aspect
public class LogRecordAspect {

  @Autowired
  private SysLogService sysLogService ;
  /**
   * 对使用Log 这个注解标记的方法，我们需要给他一个环绕通知
   *
   */
  @Around("@annotation(com.zy.anno.Log)") // 切面的表达式
  public Object logRecord(ProceedingJoinPoint joinPoint) throws Throwable {
    // 只要用用户操作有切面的方法，就会自动的进来，
    // 进来后，我们无法就是想写一个日志
    // Subject 是主体，他和线程绑定，只要运行的线程不变，在任意的地方能去掉
    SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    Log log = method.getAnnotation(Log.class);// 这个注解绝对不为null，因为有注解我们才进来的
    long start = System.currentTimeMillis() ;
    // 执行方法的调用
    Object result = joinPoint.proceed(joinPoint.getArgs()); //我们不需要处理异常，因为全局异常处理器可以帮我们处理
    long end = System.currentTimeMillis() ;
    //Request 对象也是和线程绑定的我们可以从线程里面获取它
    // 这个方法方法非常的重要，曾经面试过好的次了
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = requestAttributes.getRequest();


//    SysLog sysLog = new SysLog().build().
//      setUsername(sysUser.getUsername()).
//      setOperation(log.operation()).
//      setCreateDate(new Date()).
//      setIp(request.getRemoteHost()).
//      setParams(joinPoint.getArgs() == null ? "" : JSONUtil.toJsonPrettyStr(joinPoint.getArgs())).
//      setTime(end - start).
//      setMethod(method.getName()).
//      build();




    SysLog sysLog =new SysLog().builder().
      username(sysUser.getUsername()).
      operation(log.operation()). // 用户的操作，该用户的操作，我们可以写@Log("删库走人")
      createDate(new Date()).
      ip(request.getRemoteHost()). // 怎么得到一个用户的ip 地址？，ip 在Request 对象里面，那怎么得到一个Request？
//      params(joinPoint.getArgs().toString()).
      params(joinPoint.getArgs()==null?"": JSONUtil.toJsonPrettyStr(joinPoint.getArgs())).
      time(end-start). // 这个方法以后要执行多久
      method(method.getName()).build();
    sysLogService.save(sysLog) ; // 保存的数据库里面
    return result ;

  }
}
