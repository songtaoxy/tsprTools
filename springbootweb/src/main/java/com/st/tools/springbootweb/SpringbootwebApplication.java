package com.st.tools.springbootweb;

import com.st.tools.springbootweb.pojo.UserConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan(basePackages={"com.st.tools.springbootweb.mapper","com.st.tools.springbootweb.pojo.mapper"})
public class SpringbootwebApplication {


  public static void main(String[] args) {

    SpringApplication.run(SpringbootwebApplication.class, args);
    System.out.println("hi");

    //System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

  }
}
