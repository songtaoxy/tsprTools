package com.st.utils.json;

import com.st.utils.string.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

  @Test
  void isJson() {
    String s = "{\"name\":\"Michael\",\"age\":24,\"birthday\":\"2018-09-09\"}";
    String s2 = "[{\"name\":\"Michael\",\"age\":24,\"birthday\":\"2018-09-09\"}]";

    System.out.println(JsonUtils.isJson(s));
    System.out.println(JsonUtils.isJson(s2));
  }
}
