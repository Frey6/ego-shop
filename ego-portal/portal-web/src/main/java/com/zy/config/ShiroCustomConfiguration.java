package com.zy.config;

import com.zy.shiro.TokenSessionManager;
import com.zy.shiro.UserRealm;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义Shiro的配置
 */

@Configuration
public class ShiroCustomConfiguration
{
  @Value("${spring.redis.host}")
  private String redisHost;
  @Value("${spring.redis.port}")
  private Integer redisPort;

  @Bean
  public DefaultWebSecurityManager defaultWebSecurityManager(UserRealm realm, TokenSessionManager tokenSessionManager, RedisSessionDAO sessionDAO)
  {
    DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();

    defaultSecurityManager.setRealm(realm);
    tokenSessionManager.setSessionDAO(sessionDAO);
    defaultSecurityManager.setSessionManager(tokenSessionManager);
    return defaultSecurityManager;
  }

  @Bean
  public RedisSessionDAO redisSessionDAO(IRedisManager redisManager)
  {
    RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
    redisSessionDAO.setRedisManager(redisManager);
    redisSessionDAO.setExpire(604800);

    return redisSessionDAO;
  }

  @Bean
  public IRedisManager iRedisManager()
  {
    RedisManager redisManager = new RedisManager();
    if (StringUtils.hasText(this.redisHost)) {
      if (this.redisPort != null) {
        redisManager.setHost(this.redisHost + ":" + this.redisPort);
      } else {
        redisManager.setHost(this.redisHost + ":" + 6379);
      }
    }
    return redisManager;
  }

  @Bean
  public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager)
  {
    ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

    shiroFilterFactoryBean.setLoginUrl("login");

    shiroFilterFactoryBean.setUnauthorizedUrl("unauthorizedUrl");

    Map<String, String> filterChainDefinitionMap = new HashMap();
    filterChainDefinitionMap.put("/login", "anon");
    filterChainDefinitionMap.put("/unauthorizedUrl", "anon");
    filterChainDefinitionMap.put("/getCode", "anon");

    filterChainDefinitionMap.put("/**", "anon");
    shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
    return shiroFilterFactoryBean;
  }

  @Bean
  @DependsOn({"lifecycleBeanPostProcessor"})
  public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator()
  {
    DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();

    defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
    return defaultAdvisorAutoProxyCreator;
  }

  @Bean
  public RestTemplate restTemplate()
  {
    return new RestTemplate();
  }
}
