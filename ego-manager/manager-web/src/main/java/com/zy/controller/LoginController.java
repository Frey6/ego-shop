package com.zy.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import lombok.SneakyThrows;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 前后端分离开发里面，登录成功后，后端不需要跳转
 */
@RestController // @RequestBody + @Controller
public class LoginController {

  /**
   * 1 补齐Shiro 里面的2 个路径
   */
  @GetMapping("/login")
  public ResponseEntity<String> toLogin(){
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户还没有登录，请先登录!");
  }

  @GetMapping("/unauthorizedUrl")
  public ResponseEntity<String> unauthorizedUrl(){
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("该用户权限不够，请联系管理员提示权限！") ;
  }

  /**
   * 完成Shiro的登录功能
   * @param username
   * @param password
   * @return
   */
  @PostMapping("/login")
  public ResponseEntity<String> doLogin(
    @RequestParam(required = true) String username,
    @RequestParam(required = true) String password
  ){
    UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
    Subject subject = SecurityUtils.getSubject();
    try {
      subject.login(usernamePasswordToken); // 做登录
      // 登录成了，它把用户的登录信息写在sessions 里面，而sessions 里面key时SessionId
      String token = subject.getSession().getId().toString();
      // 只要有上面的key，我就是登录过来
      return  ResponseEntity.ok(token) ;
    } catch (AccountException e) { // 用户不存在
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名不存在，或用户名错误");
    }catch (CredentialsException credentialsException){ // 密码错误
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码过期或错误");
    }catch (UnsupportedTokenException unsupportedTokenException){ // token 不支持
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("TOKEN 不被支持");
    }catch (RuntimeException runtimeException){
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器错误");
    }
  }
  @SneakyThrows // 自动的关闭资源
  @GetMapping("/captcha.jpg")
  public ResponseEntity<Void> getCaptcha(String uuid, HttpServletResponse response){
    // 使用hutool 生成一个图片
    LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 50, 4, 3);
    ServletOutputStream outputStream = null ;
    try {
      outputStream = response.getOutputStream();
      lineCaptcha.write(outputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return  ResponseEntity.ok().build();
  }
}


