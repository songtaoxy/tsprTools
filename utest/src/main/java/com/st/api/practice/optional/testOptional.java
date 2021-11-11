package com.st.api.practice.optional;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * @author: st
 * @date: 2021/11/8 17:18
 * @version: 1.0
 * @description:
 */
@Slf4j
public class testOptional {

  @Test
  void testOptional() {
    String strOrginal = null;
    String str = Optional.ofNullable(strOrginal).map(s -> "100").orElse("100");

  }

}
