package com.st.tools.springbootweb.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author: st
 * @date: 2021/11/12 15:06
 * @version: 1.0
 * @description:
 */
public class FilterDemo implements Filter {
  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    System.out.println("filter: dofilter");
    // 要继续处理请求，必须添加 filterChain.doFilter()
    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    System.out.println("filter: init");
  }

  /**
   * 程序关闭后, 该方法执行
   */
  @Override
  public void destroy() {
    System.out.println("filter: destory");
  }

}
