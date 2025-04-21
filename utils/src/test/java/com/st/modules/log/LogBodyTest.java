package com.st.modules.log;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.st.modules.json.jackson.JacksonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class LogBodyTest {

    @Test
    public void testCreatLogBody() {

        String topic = "/log/test/this is a test case";
//        JSONObject jsonObject = FastJsonUtil.buildJS();
        ObjectNode js = JacksonUtils.createObjectNode();

        //jsonObject.put("topic", topic);
        js.put("zip name", "x.zp");
        js.put("zip path", "/data/yonyou/nchome/nclogs.log");

        Person person = new Person();
        person.setAge(10);

        LogBody logBody = new LogBody();
        logBody.setTopic(topic);
        logBody.setInfos_obj(js);
        logBody.setInfos_obj(person);
        //log.info(FastJsonUtil.format(logBody));
        System.out.println(JacksonUtils.toPrettyJson(logBody));

       // 支持: 字符串
        log.info(JacksonUtils.toPrettyJson("we are"));
    }

}

@Data
class Person {
    private String name;
    private Integer age;
}