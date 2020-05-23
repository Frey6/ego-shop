package com.zy.controller;

import com.zy.entity.User;
import com.zy.params.LoginParam;
import com.zy.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController
{
  @Reference(check=false)
  private UserService userService;

  @PostMapping({"/login"})
  public ResponseEntity<Map<String, Object>> login(@RequestBody LoginParam loginParam)
  {
    Subject subject = SecurityUtils.getSubject();
    UsernamePasswordToken passwordToken = new UsernamePasswordToken(loginParam.getPrincipal(), "WECHAT");
    Map<String, Object> result = new HashMap();
    try
    {
      subject.login(passwordToken);
      User user = (User)subject.getPrincipal();
      result.put("nickName", user.getNickName());
      result.put("userStutas", user.getStatus());
      result.put("access_token", subject.getSession().getId());
    }
    catch (AuthenticationException e)
    {
      ResponseEntity.badRequest().body(e.getMessage());
    }
    return ResponseEntity.ok(result);
  }

  @PutMapping({"/p/user/setUserInfo"})
  @ApiOperation("更新用户的头像和昵称")
  public ResponseEntity<Void> updateUser(@RequestBody @Validated User user)
  {
    User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
    user.setUserId(currentUser.getUserId());
//    userService.updateById(user);
    return ResponseEntity.ok().build();
  }
}
