package com.zy.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.zy.param.LoginParam;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 前后端分离开发里面，登录成功后，后端不需要跳转
 */
@ApiOperation("登录的数据接口")
@RestController // @RequestBody + @Controller
public class LoginController {

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  public static final  String VALIDATE_CODE_PREFIX="validate:";
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
   * @return
   */
  @ApiOperation("登录")
  @PostMapping("/login")
  public ResponseEntity<String> doLogin(
    @RequestBody @Validated LoginParam loginParam
    ){
    try {
    checkValidateCode(loginParam);
    UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(loginParam.getPrincipal(), loginParam.getCredentials());
    Subject subject = SecurityUtils.getSubject();

      subject.login(usernamePasswordToken); // 做登录
      // 登录成了，它把用户的登录信息写在sessions 里面，而sessions 里面key时SessionId
      String token = subject.getSession().getId().toString();
      // 只要有上面的key，我就是登录过来
      return  ResponseEntity.ok(token) ;
    } catch (AccountException e) { // 用户不存在
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户名不存在，或用户名错误");
    }catch (CredentialsException credentialsException){ // 密码错误
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("密码过期或错误");
    }catch (UnsupportedTokenException unsupportedTokenException){ // token 不支持
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("TOKEN 不被支持");
    }
    catch (AuthenticationException validateCodeException){ // 验证码错误
      //System.out.println("valicateCodeException:"+validateCodeException.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validateCodeException.getMessage());
    }catch (RuntimeException runtimeException){
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器错误");
    }
  }

  private void checkValidateCode(LoginParam loginParam) {
    String sessionUUID = loginParam.getSessionUUID();
    String imageCode = loginParam.getImageCode();
    if (StringUtils.hasText(sessionUUID)&&StringUtils.hasText(imageCode)){
      String redisCode = stringRedisTemplate.opsForValue().get(VALIDATE_CODE_PREFIX + sessionUUID);
      if(imageCode.equals(redisCode)){
        stringRedisTemplate.delete(VALIDATE_CODE_PREFIX+sessionUUID);
        return;
      }
    }
    throw  new AuthenticationException("验证码错误，请重新获取");
  }

//  @SneakyThrows // 自动的关闭资源
  @ApiOperation("获取验证码")
  @GetMapping("/captcha.jpg")
  public ResponseEntity<Void> getCaptcha(String uuid, HttpServletResponse response){
    // 使用hutool 生成一个图片
    LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 50, 4, 3);
    String validateCode = lineCaptcha.getCode();
    stringRedisTemplate.opsForValue().set(VALIDATE_CODE_PREFIX+uuid,validateCode,1,TimeUnit.MINUTES);
    ServletOutputStream outputStream = null ;
    try {
      outputStream = response.getOutputStream();
      lineCaptcha.write(outputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return  ResponseEntity.ok().build();
  }

  /**
   * 登出的功能
   * 把shiro中储存在redis里面的session删除
   * @return
   */
  @PostMapping("/sys/logout")
  public  ResponseEntity<Void> logout(){
    Subject subject = SecurityUtils.getSubject();
    subject.logout();
    return  ResponseEntity.ok().build();
  }
}


