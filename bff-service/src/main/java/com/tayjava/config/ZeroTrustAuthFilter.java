package com.tayjava.config;

import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.security.DemoAuthHeaders;
import com.tayjava.common.security.DemoBearerAuthenticator;
import com.tayjava.common.security.DemoJwtSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class ZeroTrustAuthFilter extends OncePerRequestFilter {

  public static final String ATTR_PRINCIPAL = "tayjava.demoPrincipal";

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/actuator/") || path.equals("/api/v1/featured-courses");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    request.setAttribute("tayjava.ignoredUserIdHeader", request.getHeader(DemoAuthHeaders.USER_ID));
    request.setAttribute("tayjava.ignoredRolesHeader", request.getHeader(DemoAuthHeaders.ROLES));

    var principal = DemoBearerAuthenticator.authenticate(request.getHeader(DemoJwtSupport.HEADER));
    if (principal.isEmpty()) {
      response.setStatus(ErrorCode.UNAUTHORIZED.getHttpStatus().value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      byte[] body =
          """
          {"success":false,"code":"UNAUTHORIZED","message":"Bearer demo token required"}
          """
              .getBytes(StandardCharsets.UTF_8);
      response.getOutputStream().write(body);
      return;
    }
    request.setAttribute(ATTR_PRINCIPAL, principal.get());
    filterChain.doFilter(request, response);
  }
}
