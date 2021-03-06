package com.eugene.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by Ngô Mạnh Cường on 12/23/2016.
 */

/**
 * Custom Handler để chuyển trang theo quyền của người dùng
 */
@Component
public class NgoManhCuong_05_CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  /*Override lại hàm có săn của Spring để chuyển trang*/
  @Override
  public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
    Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

    if (authorities.iterator().next().getAuthority().equals("ROLE_ADMIN")) {
      httpServletResponse.sendRedirect("/admin");
    } else {
      httpServletResponse.sendRedirect("/");
    }
  }
}
