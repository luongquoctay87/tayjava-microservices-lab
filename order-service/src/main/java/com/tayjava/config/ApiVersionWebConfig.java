package com.tayjava.config;

import com.tayjava.common.ApiVersions;
import com.tayjava.common.enums.ErrorCode;
import com.tayjava.common.exception.ErrorException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionWebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new ApiVersionInterceptor()).addPathPatterns(ApiVersions.PATH_V1 + "/**");
  }

  static final class ApiVersionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
        HttpServletRequest request, HttpServletResponse response, Object handler) {
      String version = request.getHeader(ApiVersions.HEADER);
      if (version != null && !version.isBlank() && !ApiVersions.V1.equals(version.trim())) {
        throw new ErrorException(
            ErrorCode.UNSUPPORTED_API_VERSION, "Supported API-Version values: " + ApiVersions.V1);
      }
      response.setHeader(ApiVersions.HEADER, ApiVersions.V1);
      return true;
    }
  }
}
