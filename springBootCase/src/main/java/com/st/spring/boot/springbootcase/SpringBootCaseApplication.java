package com.st.spring.boot.springbootcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringBootCaseApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBootCaseApplication.class, args);
  }
}
