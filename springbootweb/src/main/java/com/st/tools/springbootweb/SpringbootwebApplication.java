package com.st.tools.springbootweb;

import com.st.tools.springbootweb.pojo.UserConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan("com.st.tools.springbootweb.mapper")
public class SpringbootwebApplication {


  public static void main(String[] args) {

    SpringApplication.run(SpringbootwebApplication.class, args);

  }
}
