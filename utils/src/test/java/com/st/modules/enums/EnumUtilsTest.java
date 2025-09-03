package com.st.modules.enums;

import com.st.modules.enums.v1.BaseEnum;
import com.st.modules.enums.v2.EnumUtils;
import com.st.modules.json.jackson.JacksonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@Slf4j
class EnumUtilsTest {

    @Test
    void getEnumObjectV1() {
    }

    @Test
    void getEnumObjectV2() {

        log.info(DemoV2Enum.getNameByCode("200")); // 输出：二
        log.info(DemoV2Enum.getCodeByName("三"));   // 输出：300
        log.info(JacksonUtils.toPrettyJson(DemoV2Enum.getByCode("200")));
        log.info(JacksonUtils.toPrettyJson(DemoV2Enum.getByName("二")));
        log.info(JacksonUtils.toPrettyJson(DemoV2Enum.getByCode("200").toString()));
        log.info(JacksonUtils.toPrettyJson(DemoV2Enum.getByName("二").toString()));


    }
}


@AllArgsConstructor
@Getter
@Slf4j
enum DemoEnum implements BaseEnum {

    PENDING("100", "一","Order is pending", "1", "ext"),
    PROCESSING("200","二", "Order is being processed", "1", "ext"),
    COMPLETED("300", "三","Order is completed", "1", "ext"),
    CANCELLED("400", "四","Order is cancelled", "1", "ext");

    private final String code;
    private final String name;
    private final String des;
    private final String active;
    private final String ext;


/*
    // 构造函数
    private  DemoEnum(String code, String name, String des, boolean active, String ext) {
        this.code = code;
        this.name = name;
        this.des = des;
        this.active = active;
        this.ext = ext;

    }*/

    public static DemoEnum getEnumByCode(Integer code){
        Optional<DemoEnum> m1 = EnumUtils.getEnumObjectV1(DemoEnum.class, e -> e.getCode().equals(code));
        return m1.isPresent() ? m1.get() : null;
    }

    public static String getCodeByName(String name){
        Optional<DemoEnum> m1 = EnumUtils.getEnumObjectV1(DemoEnum.class, e -> e.getName().equals(name));
        return m1.isPresent() ? m1.get().getCode() : null;
    }

    public static String getNameByCode(String code){
        Optional<DemoEnum> m1 = EnumUtils.getEnumObjectV1(DemoEnum.class, e -> e.getCode().equals(code));
        return m1.isPresent() ? m1.get().getName() : null;
    }

}

@AllArgsConstructor
@Getter
@ToString
@Slf4j
enum DemoV2Enum implements BaseEnum {

    PENDING("100", "一","des", "1", "ext"),
    PROCESSING("200","二", "des", "1", "ext"),
    COMPLETED("300", "三","des", "1", "ext"),
    CANCELLED("400", "四","des", "1", "ext");

    private final String code;
    private final String name;
    private final String des;
    private final String active;
    private final String ext;


/*
    // 构造函数; 使用lombok @allconstructor
    private  DemoEnum(String code, String name, String des, boolean active, String ext) {
        this.code = code;
        this.name = name;
        this.des = des;
        this.active = active;
        this.ext = ext;

    }*/

    // 精简版接口调用
    public static DemoV2Enum getByCode(String code) {
        return EnumUtils.getByCode(DemoV2Enum.class, code).orElse(null);
    }

    public static DemoV2Enum getByName(String name) {
        return EnumUtils.getByName(DemoV2Enum.class, name).orElse(null);
    }

    public static String getNameByCode(String code) {
        return EnumUtils.getNameByCode(DemoEnum.class, code);
    }

    public static String getCodeByName(String name) {
        return EnumUtils.getCodeByName(DemoEnum.class, name);
    }



    /*public static void main(String[] args) {
        System.out.println(DemoV2Enum.getNameByCode("200")); // 输出：二
        System.out.println(DemoV2Enum.getCodeByName("三"));   // 输出：300

        DemoV2Enum e = DemoV2Enum.getByCode("100");
        if (e != null) {
            System.out.println(e.getDes()); // 输出：Order is pending
            System.out.println(e.toString());
        }
    }*/
}

