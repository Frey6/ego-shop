package com.zy.config;

import com.zy.shiro.TokenSessionManager;
import com.zy.shiro.UserRealm;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义Shiro的配置
 */
@Configuration
public class ShiroCustomConfiguration {

  @Value("${spring.redis.host}")
  private  String redisHost;
  @Value("${spring.redis.port}")
  private  String redisPort;
  ////////////////////// shiro 里面最基本的配置/////////////////////////////////////////
  /**
   * 安全管理器
   */
  @Bean
  public DefaultWebSecurityManager defaultWebSecurityManager(
    UserRealm realm,  // realm 负责登录和授权
    TokenSessionManager tokenSessionManager,  // 负责token的管理
    RedisSessionDAO sessionDAO,  // 负责token的存储
    CredentialsMatcher credentialsMatcher // 负责密码的校验
  ){
    DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();

    realm.setCredentialsMatcher(credentialsMatcher);
    defaultSecurityManager.setRealm(realm);// 自定义Realm
    tokenSessionManager.setSessionDAO(sessionDAO); // session 怎么存储由SessionDAO 决定
    defaultSecurityManager.setSessionManager(tokenSessionManager);
    return  defaultSecurityManager ;
  }

  /**
   * 密码的验证器
   * @return
   */
  @Bean
  public CredentialsMatcher credentialsMatcher(){
    /**
     * 底层使用MD5 加密
     */
    HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher("MD5");
    /**
     * 散列2 次
     */
    hashedCredentialsMatcher.setHashIterations(2);
    return hashedCredentialsMatcher ;
  }


  /**
   * 资源的放行问题
   * shiro 里面，默认的登录页面是login
   * 放行和拦截以及登录
   */

  @Bean
  public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultSecurityManager defaultWebSecurityManager){
    ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
    // shiro的登录请求地址，当shiro 发现用户没有登录访问某个url 地址时，shiro 会把用户重定向到这个login地址里面！
    shiroFilterFactoryBean.setLoginUrl("login");
    // 用户登录成功，但是用户访问一个自己没有权限访问的资源时，shiro 会把它重定向到这个url地址里面
    shiroFilterFactoryBean.setUnauthorizedUrl("unauthorizedUrl");
    // 登录成功后，跳转到那个页面里面，在前后端分离开发里面，不使用，因为前端的跳转，由前端自己的js qu 完成
//        shiroFilterFactoryBean.setSuccessUrl("");
    Map<String,String> filterChainDefinitionMap = new HashMap<>() ;
    filterChainDefinitionMap.put("/login","anon");
    filterChainDefinitionMap.put("/captcha.jpg","anon");
    filterChainDefinitionMap.put("/unauthorizedUrl","anon");
    filterChainDefinitionMap.put("/getCode","anon") ; //放行的资源
    filterChainDefinitionMap.put("/**","authc"); // 拦截的资源
    shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
    return  shiroFilterFactoryBean ;
  }

  ////////////////////// shiro 里面最基本的配置/////////////////////////////////////////
  @Bean
  public RedisSessionDAO redisSessionDAO(IRedisManager redisManager){
    RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
    redisSessionDAO.setRedisManager(redisManager); // 去存储在那个redis 里面
    redisSessionDAO.setExpire(7*24*3600); // 设置session的过期时间
//        redisSessionDAO.setKeyPrefix(""); // 设置存储到redis 里面的前缀
    return redisSessionDAO ;
  }

  /**
   * 决定使用那个Redis
   * @return
   */
  @Bean
  public IRedisManager iRedisManager(){
    RedisManager redisManager = new RedisManager();
    if(StringUtils.hasText(redisHost)){
      if(redisPort!=null){
        redisManager.setHost(redisHost+":"+redisPort); // 连接那个Redis
      }else{
        redisManager.setHost(redisHost+":"+6379); // 连接那个Redis
      }
    }
    return redisManager ;
  }

}

