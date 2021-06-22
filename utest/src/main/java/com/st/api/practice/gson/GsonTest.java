package com.st.api.practice.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.omg.CosNaming.IstringHelper;

import javax.print.attribute.standard.JobSheets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: st
 * @date: 2021/4/7 15:18
 * @version: 1.0
 * @description:
 */

@Slf4j
public class GsonTest {

    public static void main(String[] args) {
//        map2json();
//        json2map();
        jsonObjectTest();
    }

    public static void json2map() {
        Gson gson = new Gson();
        Map map = new HashMap();
        map.put("colour", "red");
        map.put("weight", "10kg");
        String mapJson = gson.toJson(map);

        map = gson.fromJson(mapJson, Map.class);
        log.info(String.valueOf(map));
    }

    public static void map2json() {
        Gson gson = new Gson();
        Map map = new HashMap();
        map.put("colour", "red");
        map.put("weight", "10kg");

        String jsonStr = gson.toJson(map);
        System.out.println(map);

        log.info("json is:{}", jsonStr);
    }

    public static void jsonObjectTest() {

        JsonObject jsonObject_0 = new JsonObject();
        jsonObject_0.addProperty("f1", "v1");
        jsonObject_0.addProperty("f2", "v2");


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("f1", "v1");
        jsonObject.addProperty("f2", "v2");

        log.info("jsonObject is:{}", jsonObject);


        JsonArray jsonElements = new JsonArray();
        jsonElements.add("a1");


        jsonElements.add(jsonObject_0);


        log.info("jsonObject is:{}", jsonElements);

        jsonObject.add("array", jsonElements);
        log.info("jsonObject is:{}", jsonObject);


        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("k_1_2", "V_1_2");

        jsonElements.add(jsonObject1);
        log.info("jsonElements is:{}",jsonElements);


        jsonElements.remove(jsonObject1);

        log.info("jsonElements is:{}",jsonElements);
        jsonElements.add(jsonObject1);
        log.info("jsonElements is:{}",jsonElements);


        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObject1);

        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("js2", "js2_v");

        jsonObject2.add("att", jsonArray);
        log.info("jsonElements is:{}",jsonObject2);
        jsonObject2.remove("att");

        log.info("jsonElements is:{}",jsonObject2);

        jsonObject2.add("att", jsonArray);


        log.info("jsonElements is:{}",jsonObject2);


        String jstr = "";

        Map<Object, Object> map = new HashMap<>();
        map.put("att", jsonElements);

        Map<Object, Object> map1 = new HashMap<>();

        map1.put("data", map);


        String result = new Gson().toJson(map1);
        log.info("result is:{},{},{}",result,jsonObject1.toString(),jsonObject2.toString());

    }



}
