package com.zy.shiro;

import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.UUID;

@Component
public class TokenSessionManager extends DefaultWebSessionManager {
  public static final String AUTH_HEADER="EGO_TOKEN";
  /**
   * shiro默认的行为，是从request里面取出tomcat的session作为key
   * @param request
   * @param response
   * @return
   */
  @Override
  protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
    /**
     * 不管你是app/pc端，你发请求时，都有一个请求头，这个头里面存储了表示
     * 我们使用头可以屏蔽参数
     */
//    HttpSession session = WebUtils.toHttp(request).getSession();  //tomcat的session
//    String id = session.getId();  //sessionid
//    return super.getSessionId(request, response);
    String token = WebUtils.toHttp(request).getHeader(AUTH_HEADER);
    if (StringUtils.hasText(token)){
      return  token;
    }
     token = UUID.randomUUID().toString();
    return  token;
  }
}
