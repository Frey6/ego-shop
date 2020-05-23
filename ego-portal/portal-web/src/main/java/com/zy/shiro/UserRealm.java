package com.zy.shiro;




import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zy.entity.User;
import com.zy.service.UserService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.UUID;

@Component
public class UserRealm extends AuthorizingRealm
{
  @Autowired
  private RestTemplate restTemplate;
  @Value("${wechat.appid}")
  private String appId;
  @Value("${wechat.secret}")
  private String appSecret;
  private static String WECHAT_AUTH_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
  @Reference(check=false)
  private UserService userService;

  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
  {
    return null;
  }

  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
    throws AuthenticationException
  {
    String code = token.getPrincipal().toString();

    String result = (String)this.restTemplate.getForObject(String.format(WECHAT_AUTH_URL, new Object[] { this.appId, this.appSecret, code }), String.class, new Object[0]);
    JSONObject jsonObject = JSONUtil.parseObj(result);
    String openId = jsonObject.getStr("openid");
    if (openId == null) {
      throw new AuthenticationException("验证码错误，请重试");
    }
    User user =userService.findUserByUserName(openId);
    user = createOrUpdateUser(openId, user);

    return new SimpleAuthenticationInfo(user, "WECHAT", openId);
  }

  private User createOrUpdateUser(String openId, User user)
  {
    String ip = getIpAddr();
    boolean isCreate = false;
    if (user == null)
    {
      user = new User();
      user.setUserId(openId);
//      user.setNickName("zxf"+ UUID.randomUUID().toString());
      user.setUserRegtime(new Date());
      user.setNickName("zxf");
      user.setUserRegip(ip);
      isCreate = true;
      user.setStatus(Integer.valueOf(1));
    }
    user.setModifyTime(new Date());
    user.setUserLastip(ip);
    user.setUserLasttime(new Date());
    if (isCreate) {
      userService.save(user);
    } else {
//      userService.updateById(user);
    }
    return user;
  }

  private String getIpAddr()
  {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return requestAttributes.getRequest().getRemoteHost();
  }
}
