package com.st.modules.enums;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Getter
@ToString
@Slf4j
public enum DemoV2Enum implements BaseEnum {

    PENDING("100", "一","des", true, "ext"),
    PROCESSING("200","二", "des", true, "ext"),
    COMPLETED("300", "三","des", false, "ext"),
    CANCELLED("400", "四","des", false, "ext");

    private final String code;
    private final String name;
    private final String des;
    private final boolean active;
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



    public static void main(String[] args) {
        System.out.println(DemoV2Enum.getNameByCode("200")); // 输出：二
        System.out.println(DemoV2Enum.getCodeByName("三"));   // 输出：300

        DemoV2Enum e = DemoV2Enum.getByCode("100");
        if (e != null) {
            System.out.println(e.getDes()); // 输出：Order is pending
            System.out.println(e.toString());
        }
    }
}
