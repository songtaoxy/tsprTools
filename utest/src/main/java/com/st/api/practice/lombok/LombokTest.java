package com.st.api.practice.lombok;

import lombok.*;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import com.st.utils.log.LogUtils;

/**
 * @author: st
 * @date: 2021/11/6 17:30
 * @version: 1.0
 * @description:
 */
public class LombokTest {

  @Test
  void lombokTest() {
    Studenta studenta = new Studenta();
    LogUtils.foal(studenta,"");


    Studenta studenta1 = new Studenta("s", "p", 10);
    studenta1.setName("x").setAge(20);
    //LogUtils.formatObjAndLogging(studenta1.toString(),"");
    LogUtils.foal(studenta1.toString(),null);

  }

  @Data
  @Accessors(chain = true)
  @NoArgsConstructor
  @AllArgsConstructor
  class Studenta {
    private String name;
    private String parents;
    private int age;
  }
}
