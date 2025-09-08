package com.st;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan(basePackages={"com.st.modules.demo.dao.mapper"})
public class WebApplication {


  public static void main(String[] args) {

    SpringApplication.run(WebApplication.class, args);
    System.out.println("Server starting successfully!");

    // save proxy.
    //System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

  }
}
