package com.zy.shiro;



import java.io.Serializable;
import java.util.UUID;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TokenSessionManager
  extends DefaultWebSessionManager
{
  private static final String AUTH_HEADER = "Authorization";

  protected Serializable getSessionId(ServletRequest request, ServletResponse response)
  {
    String token = WebUtils.toHttp(request).getHeader("Authorization");
    if (StringUtils.hasText(token)) {
      return token;
    }
    token = UUID.randomUUID().toString();
    return token;
  }
}
