package com.st.api.practice.regx;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.jupiter.api.Test;

/**
 * @author: st
 * @date: 2021/7/28 20:58
 * @version: 1.0
 * @description:
 */
@Slf4j
public class ReplaceBackticks {


	@Test
	public void replaceBackticks(){

    String s =
        "{ \"msg\": \"SUCCESS\", \"data\": { \"tenantId\": \"edr19n2r\", \"nodes\": [{ \"labelId\": \"ff5e835d-54d7-4fa7-b0c8-a1a6a7e371f3\", \"orders\": 0, \"labelName\": \"数据中台\", \"labelCode\": \"DMP\", \"applications\": [{ \"applicationIcon\": \"./appstore/app-22.png\", \"orders\": 0, \"serviceCodeList\": [\"0a9d769f-e4d8-4c84-9a66-dae83c2c107a\"], \"applicationId\": \"a2fc0763-23bb-496a-9bc4-b4b2aafd78c4\", \"applicationCode\": \"GT76713AT1\", \"bizObjList\": [], \"applicationName\": \"new0621\", \"domainKeys\": [] }, { \"applicationIcon\": \"./appstore/app-6.png\", \"orders\": 0, \"serviceCodeList\": [\"62e7a21b-5349-4006-bbe0-04df0188eb34\", \"d6800dc1-2bbb-43ef-ac6d-d58ce2d1abb4\"], \"applicationId\": \"9e6c4aa2-a65b-40cd-829a-4c3b2606aabe\", \"applicationCode\": \"GT78265AT8\", \"bizObjList\": [], \"applicationName\": \"生态标准全\", \"domainKeys\": [] }, { \"applicationIcon\": \"./appstore/app-3.png\", \"orders\": 0, \"serviceCodeList\": [\"336f43c1-d5e3-4d2b-9a29-c1ff4115b3ee\"], \"applicationId\": \"69ad98a4-b831-4cb3-abee-4bd1903c4434\", \"applicationCode\": \"GT70948AT144\", \"bizObjList\": [], \"applicationName\": \"isv数据迁移\", \"domainKeys\": [] }, { \"applicationDesc\": \"\", \"applicationIcon\": \"https://file-cdn.yonyoucloud.com/workapplation/default3.png\", \"orders\": 80, \"serviceCodeList\": [\"intellivworkbenchplatform-20\", \"intellivworkbenchplatform-27\", \"intellivworkbenchplatform-21\", \"intellivworkbenchplatform-26\", \"intellivworkbenchplatform-22\", \"intellivworkbenchplatform-51\", \"intellivworkbenchplatform-55\", \"intellivworkbenchplatform-56\", \"intellivworkbenchplatform-41\", \"intellivworkbenchplatform-42\", \"intellivworkbenchplatform-43\", \"intellivworkbenchplatform-44\", \"intellivworkbenchplatform-25\", \"intellivworkbenchplatform-71\", \"intellivworkbenchplatform-72\", \"intellivworkbenchplatform-73\", \"intellivworkbenchplatform-24\", \"intellivworkbenchplatform-76\"], \"applicationId\": \"b74eb909-2ab7-43ff-b803-c2cdf6ce84f4\", \"applicationCode\": \"PFRPT\", \"bizObjList\": [], \"applicationName\": \"智能分析\", \"domainKeys\": [] }, { \"applicationDesc\": \"\", \"applicationIcon\": \"https://file-cdn.yonyoucloud.com/workapplation/default2.png\", \"orders\": 82, \"serviceCodeList\": [\"intellivworkbenchplatform-23\", \"intellivworkbenchplatform-31\"], \"applicationId\": \"09488c45-c131-44bf-901c-81a211c7eb85\", \"applicationCode\": \"PFRPT_DF\", \"bizObjList\": [], \"applicationName\": \"智能分析_数据填报\", \"domainKeys\": [] }] }] }, \"status\": \"1\" }";


		JsonObject asJsonObject = new JsonParser().parse(s).getAsJsonObject();


		JSONObject parseObject = JSONObject.parseObject(s);


		JSONObject jsonObject = JSONObject.parseObject(s);

    log.info(JSONObject.toJSONString(jsonObject, true));
	log.info(jsonObject.toJSONString());
	}

}
