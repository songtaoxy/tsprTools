package com.st.api.practice.charset;

import com.st.utils.bytes.ByteUtil;
import com.st.utils.string.S;
import com.st.utils.string.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author: st
 * @date: 2021/11/26 12:25
 * @version: 1.0
 * @description:
 */
public class CharsetDemo {

  public static void main(String[] args) {

    String str = "君";

    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

    for (byte i : bytes) {

      System.out.println((byte)655);
      System.out.println(ByteUtil.byte2Int((byte) -133));
      System.out.println(
          StringUtils.objs2Str(
              new Object[] {"byte ===> int(10)  ===> int(hex)\n",
                i, S.B, "===>", S.B, ByteUtil.byte2Int(i),S.B, "===>", ByteUtil.byteToHex(i)
              }));
    }




  }
}
