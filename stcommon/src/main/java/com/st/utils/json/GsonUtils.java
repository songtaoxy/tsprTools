package com.st.utils.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;

/**
 * @author: st
 * @date: 2022/3/7 14:30
 * @version: 1.0
 * @description:
 */
public class GsonUtils {

  static String str =
      "{\"sqlStatment\":\"select shijian from GT2007AT3.GT2007AT3.zhu0307 \",\"paramList\":[],\"id\":\"73e1d143-22ce-451c-9fe6-413d256d505e\",\"tenantId\":\"0000KZNPMG1M6B0B9X0000\",\"uri\":\"GT2007AT3.report.jiaoben0307\",\"ytenant_id\":\"0000KZNPMG1M6B0B9X0000\",\"domain\":\"report\",\"status\":\"4\",\"yonqlInfo\":null}";

  static String str2 =
      "{\"sqlStatment\":\"select shijian from GT2007AT3.GT2007AT3.zhu0307 \"}\n" + "\n";

  public static void main(String[] args) {
    JsonObject departmentJsonObj = new JsonParser().parse(str2).getAsJsonObject();

    System.out.println(departmentJsonObj.toString());

    byte[] bytes = departmentJsonObj.toString().getBytes(StandardCharsets.UTF_8);

    // System.out.println(bytes);

    for (int i = 0; i < bytes.length; i++) {
      System.out.println(bytes[i]);
    }

    String s = new String(bytes);

    System.out.println(s);


    byte[] b = new byte[2];
    b[0]='n';
    b[1]=' ';

    for (int i = 0; i < b.length; i++) {
      System.out.println(b[i]);
    }

    String s2 = new String(b);

    System.out.println("["+s2+"]");

  }
}
