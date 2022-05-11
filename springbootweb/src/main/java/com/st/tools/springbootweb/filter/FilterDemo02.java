package com.st.tools.springbootweb.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author: st
 * @date: 2021/11/12 15:06
 * @version: 1.0
 * @description:
 */
public class FilterDemo02 implements Filter {
  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    System.out.println("过滤器:["+this.getClass().getName()+"]-dofilter-之前的方法-可有可无");
    System.out.println("过滤器:["+this.getClass().getName()+"]-dofilter-执行该方法");
    // 要继续处理请求，必须添加 filterChain.doFilter()
    filterChain.doFilter(servletRequest, servletResponse);
    System.out.println("过滤器:["+this.getClass().getName()+"]-dofilter-之后的方法-可有可无");
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    System.out.println("过滤器:["+this.getClass().getName()+"]-init");
  }

  /**
   * 程序关闭后, 该方法执行
   */
  @Override
  public void destroy(){
    System.out.println("过滤器:["+this.getClass().getName()+"]-destroy");
  }

}
