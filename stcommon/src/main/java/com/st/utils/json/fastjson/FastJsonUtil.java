package com.st.utils.json.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.st.practice.com.Person;

import java.util.List;

public class FastJsonUtil {

    public static void main(String[] args) {

        Person person = new Person("hi", 18);

        JSONObject jsonObject = o2j(person);

        JSONObject jsonObject1 = o2o(person, JSONObject.class);

        System.out.println(jsonObject.toJSONString());
        System.out.println(jsonObject1.toJSONString());


        Person person1 = j2o(jsonObject, Person.class);
        System.out.println(3);
        System.out.println(person1.toString());

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

    public static <T> T jStr2o(String string,Class<T> tClass) {
        return JSONObject.parseObject(string,tClass);
    }

    public static <T> List<T> jStr2list(String string,Class<T> tClass) {
        return JSON.parseArray(string, tClass);
    }

    public static String list2jstr(Object object) {
        return JSONArray.toJSONString(object);
    }
}
