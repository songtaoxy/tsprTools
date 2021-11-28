package com.st.practice.okhttp;

import com.st.utils.json.JsonUitls;
import com.st.utils.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

/**
 * @author: st
 * @date: 2021/11/28 17:25
 * @version: 1.0
 * @description:
 */
@Slf4j
public class OkhttpDemo {

  public static void main(String[] args) throws IOException {
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body =
        RequestBody.create(
            mediaType,
            "{\n    \"env\":\"\",\n    \"db\":\"metadata\",\n    \"collection\":\"metaclass\",\n    \"condition\":{\"uri\":\"voucher.order.OrderPrice\"},\n    \"projection\":{\"ownedAttribute\":0}\n}");
    Request request =
        new Request.Builder()
            .url("http://metadata-extension.pre.app.yyuap.com/ext/query/exec")
            .method("POST", body)
            //.addHeader("User-Agent", "apifox/1.0.0 (https://www.apifox.cn)")
            .addHeader("Content-Type", "application/json")
            .build();
    Response response = client.newCall(request).execute();


    LogUtils.formatObjAndLogging(
        JsonUitls.jsonStr2fastjsonObj(response.body().string()), "respoons");
    //
  }
}
