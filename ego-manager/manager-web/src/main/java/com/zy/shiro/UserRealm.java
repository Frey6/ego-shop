package com.zy.shiro;

import com.zy.entity.SysUser;
import com.zy.service.impl.SysUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
public class UserRealm extends AuthorizingRealm {

  @Autowired
  private SysUserService sysUserService ;
  /**
   * 做授权
   * 授权可能会运行多次
   * @param principals
   * @return
   */
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    SysUser sysUser = (SysUser) principals.getPrimaryPrincipal();
    if (sysUser.getUsername().equals("admin")){
      SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
      simpleAuthorizationInfo.setStringPermissions(new HashSet<>(Arrays.asList("admin")));
      return  simpleAuthorizationInfo;
    }
    return null;
  }

  /**
   * 做登录
   * 用户登录一次
   * @param token
   * @return
   * @throws AuthenticationException
   */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    // token 里面包含用户名
    String username = token.getPrincipal().toString();
    // 通过用户名查询用户
    SysUser sysUser = sysUserService.findUserByusername(username);
    if(sysUser==null){ // 用户名不存在
      return  null ; // shiro 会处理null的情况，底层会抛一个用户不存在的异常
    }
    // 屏蔽用户的私人信息后，把用户给它
    sysUser.setEmail("*******");
    ByteSource byteSource = ByteSource.Util.bytes("whsxt"); // sysUser.getSalt()

    return new SimpleAuthenticationInfo(sysUser,sysUser.getPassword(),byteSource,sysUser.getUsername());
  }
}

