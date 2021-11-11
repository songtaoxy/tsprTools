package com.st.utils.string;

import org.junit.jupiter.api.Test;

class UtilsStringTest {

  @Test
  void objs2Str() {
    int x = 100;
    String y = "hi";
    String line = "\n";
    String[] strs = {"a", y};

    String xy =
        UtilsString.objs2Str(
            new Object[] {
              UtilsString.START_FUll,
              "a",
              "hi",
              100,
              UtilsString.LINE,
              null,
              UtilsString.SPLIT_LINE,
              "new",
              UtilsString.END_FULLE
            });

    System.out.println(xy);
  }
}
