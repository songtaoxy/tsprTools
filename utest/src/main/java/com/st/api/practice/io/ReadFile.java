package com.st.api.practice.io;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @author: st
 * @date: 2021/7/29 12:24
 * @version: 1.0
 * @description:
 */
@Slf4j
public class ReadFile {

  /** 读出城市列表文件 */
  @Test
  public void readCityFile() {
    File file02 = new File("/Users/songtao/downloads/x.txt");
    //File file02 = new File("/Users/songtao/downloads/j.json");
    FileInputStream is = null;
    StringBuilder stringBuilder = null;
    try {
      if (file02.length() != 0) {
        /** 文件有内容才去读文件 */
        is = new FileInputStream(file02);
        InputStreamReader streamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        stringBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
          // stringBuilder.append(line);
          stringBuilder.append(line);
        }
        reader.close();
        is.close();
      } else {
        log.info("null");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    log.info(String.valueOf(stringBuilder));

    String rawStr = String.valueOf(stringBuilder);

    rawStr = rawStr.substring(1, rawStr.length()-1);
    rawStr = rawStr.replaceAll("\\\\", "");

    //log.info("=======================================================");
    //JSONObject jsonObject = JSONObject.parseObject(rawStr);
    //log.info(JSONObject.toJSONString(jsonObject,true));
     JsonObject asJsonObject = new JsonParser().parse(rawStr).getAsJsonObject();
     log.info(asJsonObject.toString());
    // return String.valueOf(stringBuilder);

  }
}
