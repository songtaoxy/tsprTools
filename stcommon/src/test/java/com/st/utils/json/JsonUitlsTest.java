package com.st.utils.json;

import com.alibaba.fastjson.JSONObject;
import com.st.utils.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class JsonUitlsTest {

  @Test
  void jsonStr2JsonObj() {
    String jsonStr = "{\"name\":\"Tom\",\n" + "\"age\":20}";

    JSONObject jsonObject = JsonUtils.jsonStr2fastjsonObj(jsonStr);

    String jsonStrPretty = JSONObject.toJSONString(jsonObject, true);
    log.info(jsonStrPretty);

    LogUtils.foal(jsonStrPretty, "");

    LogUtils.foal(jsonObject,"");
  }
}
