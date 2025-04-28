package com.st.utils.string;

import com.st.modules.string.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringUtilsTest {

  @ParameterizedTest
  @ValueSource(strings = {"Radar", "Rotor", "Tenet", "Madam", "Racecar"})
  void objs2Str(Object obj) {

    String xy =
        StringUtils.objs2Str(new Object[] {StringUtils.START_FUll, obj, StringUtils.END_FULLE});

    System.out.println(xy);
  }

  @Test
  void isNumeric() {

    String x = "12389";
    String y = "123x9";

    StringUtils.isNumeric(x);
    StringUtils.isNumeric(y);
  }

}
