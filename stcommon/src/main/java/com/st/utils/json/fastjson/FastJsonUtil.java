package com.st.utils.json.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.st.practice.com.Person;

import java.text.SimpleDateFormat;
import java.util.*;

public class FastJsonUtil {

    private static Object object;

    public static void main(String[] args) {

        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("e", "f");
        hashMap.put("f", "f");
        Person person = new Person(hashMap.toString(), 18, hashMap, o2j(hashMap));
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(person);
        objects.add(o2o(person,Person.class));
        System.out.println(format(objects));


        String s = list2jstr(objects);
        JSONArray objects1 = jStr2jsa(s);
        System.out.println(8);
        System.out.println(format(objects1));
        System.out.println(format(objects1.toJSONString()));


        JSONObject jsonObject = o2j(person);

        JSONObject jsonObject1 = o2o(person, JSONObject.class);

        System.out.println(jsonObject.toJSONString());
        System.out.println(jsonObject1.toJSONString());


        Person person1 = j2o(jsonObject, Person.class);
        System.out.println(3);
        System.out.println(person1.toString());


        System.out.println(4);
//        System.out.println(formatJson(person));

        System.out.println(5);
        String  ps = "{\n" +
                "\t\"age\":18,\n" +
                "\t\"name\":\"hi\"\n" +
                "}";

        System.out.println(format(ps));



        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("a", "b");
        objectObjectHashMap.put("c", "b");

        System.out.println(6);
        System.out.println(format(objectObjectHashMap));


        System.out.println(format("hi"));

        System.out.println(format(jsonObject.toJSONString()));

    }

    public static JSONObject buildJS() {
        return new JSONObject();
    }
    public static JSONArray buildJSA() {
        return new JSONArray();
    }

    /**
     * <pre>
     *     {@code
     *
     *         // case
     *         EquipmentListDto equipmentListDto = equipmentInfoMapper.selectBySnId(snId);
     *         ReturnMsgVo msgAndStatus = FastJsonUtil.o2o(equipmentListDto, ReturnMsgVo.class);
     *
     *          // case
     *          List<DeviceResultVo> deviceResultVoList = getDeviceInfo(exportPointVo.getCityId(), exportPointVo.getUserName(), currentUser);
     *          List<PlanDeviceDto> planDevices = FastJsonUtil.list2list(deviceResultVoList,PlanDeviceDto.class);
     *     }
     * </pre>
     * <li>简单的bean转换</li>
     * <li>bean的复制</li>
     *
     * @param object
     * @param c
     * @param <T>
     * @return
     */
    public static <T> T o2o(Object object, Class<T> c) {
        return JSONObject.parseObject(JSONObject.toJSONString(object), c);
    }

    public static <T> List<T> list2list(Object object, Class<T> c) {
        return JSON.parseArray(JSONObject.toJSONString(object), c);
    }


    public static JSONObject o2j(Object object) {
        return JSONObject.parseObject(JSONObject.toJSONString(object));
    }

    public static <T> T j2o(JSONObject object, Class<T> tClass) {
        return JSONObject.parseObject(JSONObject.toJSONString(object), tClass);
    }


    public static String o2jStr(Object object) {
        return JSONObject.toJSONString(object);
    }

    public static <T> T jStr2o(String string, Class<T> tClass) {
        return JSONObject.parseObject(string, tClass);
    }

    public static <T> List<T> jStr2list(String string, Class<T> tClass) {
        return JSON.parseArray(string, tClass);
    }

    public static JSONArray jStr2jsa(String string) {
        return JSONArray.parseArray(string);
    }

    public static String list2jstr(Object object) {
        return JSONArray.toJSONString(object);
    }


    public static boolean strIsJSONObject(String str){
        boolean b = true;
        try {
            JSONObject jsonObject = JSONObject.parseObject(str);
        } catch (Exception e) {
            b = false;
        }
        return b;
    }

    public static boolean strIsJSONArray(String str){
        boolean b = true;
        try {
            JSONArray jsonArray = JSONArray.parseArray(str);
        } catch (Exception e) {
            b = false;
        }
        return b;
    }


    public static String format(Object object) {

        FastJsonUtil.object = object;

        String jsonString = null;

        if (object instanceof JSONObject || object instanceof Map) {

            jsonString = JSONObject.toJSONString(object, true);
        } else if (object instanceof String) {

            if (strIsJSONObject((String)object)) {
                System.out.println("----string: jsonobject-----");
                jsonString = JSONObject.toJSONString(JSONObject.parseObject((String) object), true);
            } else if (strIsJSONArray((String) object)) {
                System.out.println("----string: jsonarray-----");
                jsonString = JSONArray.toJSONString(JSONArray.parseArray((String) object), true);

            } else {
                System.out.println(object + "既不是json, 也不是jsonarray");
                JSONObject jsonObject = buildJS();
                jsonObject.put("ps", (String) object);
                jsonString = JSONObject.toJSONString(jsonObject, true);
            }

        } else if (object instanceof JSONArray) {

            jsonString = JSONArray.toJSONString(object, true);

        } else {
            jsonString = JSONObject.toJSONString(object, true);
        }
        return jsonString;

    }
}
