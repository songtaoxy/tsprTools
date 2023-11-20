package com.st.modules.enums.common.topic;

import com.alibaba.fastjson.JSONObject;
import com.st.modules.alibaba.fastjson.v1_2_76.FastJsonUtil;
import com.st.modules.enums.common.EnumItem;
import com.st.modules.formatter.Formatter;


import java.lang.reflect.Field;



/**
 * <li>各类日志字典</li>
 * <li>预定义字段</li>
 * <li>see: {@link }</li>
 */
public enum TopicEnum implements EnumItem<String> {

    /**
     * <li> 通用</li>
     */
    COM("0000", "00000", "RAP", "DCC", null, null, null, null),
    B0001("0001", "nc", "tax", "智能验票", "上传|接收列表", "入参", "detals...", "ps");
// 应用|服务/系统/模块/step/action
    // step 1, 1.1 1.2 2 2.1.2.2.
    // action:
    /**
     * 入参, 返回
     * 请求, 返回
     * 校验
     * 详情
     * 备注
     */
    /*--------------code---------------*/
    private final String code;
    /*--------------title---------------*/
    private final String service;
    /*--------------项目---------------*/
    private final String system;
    /*--------------模块---------------*/
    private final String module;
    /*--------------步骤---------------*/
    private final String step;
    /*--------------行为---------------*/
    private final String name;
    /*    *//*----------行为:结果.预期-----------*//*
        private final String validation;
        *//*--------行为:结果.非预期-----------*//*
        private final String invalidation;
        *//*----------行为:结果.实际-----------*//*
        private final String result;
        *//*----------行为:结果.详情----------*/
    private final String detail;
    /*--------------备注---------------*/
    private final String des;

    TopicEnum(String code, String service, String system, String module, String step, String name, String detail, String des) {
        this.code = code;
        this.service = service;
        this.system = system;
        this.module = module;
        this.step = step;
        this.name = name;
        this.detail = detail;
        this.des = des;
    }


    public static <T> TopicEnumObj buildJson(T topicEnum) {

        JSONObject jsonObject = FastJsonUtil.buildJS();

        for (Field field : topicEnum.getClass().getDeclaredFields()) {
            //把私有属性公有化
            field.setAccessible(true);

            Class<?> type = field.getType();

            //if (!type.isEnum()) {
            if (type.equals(String.class)) {
                String name = field.getName();
                String value = null;
                try {
                    value = (String) field.get(topicEnum);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                jsonObject.put(name, value);

            }
        }
        TopicEnumObj topicEnumObj = FastJsonUtil.o2o(jsonObject, TopicEnumObj.class);

        return topicEnumObj;
    }

    public static void main(String[] args) {
        TopicEnumObj topicEnumObj = buildJson(TopicEnum.B0001);
        String topic = topicEnumObj.buildTopic();


//        JsonObject jsonObject1 = GsonUtils.o2o(topicEnumObj, JsonObject.class);

//            LogUtils.printGson("test", jsonObject1);

        Formatter formatter = Formatter.init();
        formatter.setTopic(topic);

        JSONObject jsonObject = formatter.buildJS();
        jsonObject.put("key","value");

        String format = formatter.format();
        System.out.println(format);


        //Object o = GsonUtils.convertBean(LogEnum.L_gson_convert_listObjT2ListR, JsonObject.class);

//            log.info(GsonUtils.toJson(com.st.modules.log.LogEnum.L_gson_convert_listObjT2ListR));


    }


    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String getDes() {
        return null;
    }

    public String getService() {
        return service;
    }

    public String getSystem() {
        return system;
    }

    public String getModule() {
        return module;
    }

    public String getStep() {
        return step;
    }

    public String getDetail() {
        return detail;
    }
}
