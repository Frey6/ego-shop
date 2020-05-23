package com.zy.aspect;



import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class WebLogError
{
  private static final Logger log = LoggerFactory.getLogger(WebLogError.class);

  @ExceptionHandler({AuthenticationException.class})
  public ResponseEntity<String> authException()
  {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("你的登录过期，请重新登录");
  }

  @ExceptionHandler({IllegalArgumentException.class})
  public ResponseEntity<String> paramException(IllegalArgumentException e)
  {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<String> runtimeException(RuntimeException e)
  {
    log.error("运行异常", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器维修中!");
  }

  @ExceptionHandler({UnauthorizedException.class})
  public ResponseEntity<String> unauthorized()
  {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("没有权限访问");
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException bindException)
  {
    BindingResult bindingResult = bindException.getBindingResult();
    System.out.println(bindingResult);
    StringBuilder sb = new StringBuilder("数据校验失败，原因是:");
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();
    for (FieldError fieldError : fieldErrors) {
      sb.append("字段:" + fieldError.getField() + "," + fieldError.getDefaultMessage() + "!");
    }
    return ResponseEntity.badRequest().body(sb.toString());
  }
}
