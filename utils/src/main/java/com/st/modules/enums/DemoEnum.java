package com.st.modules.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Getter
@Slf4j
public enum DemoEnum implements BaseEnum {

    PENDING("100", "一","Order is pending", true, "ext"),
    PROCESSING("200","二", "Order is being processed", true, "ext"),
    COMPLETED("300", "三","Order is completed", false, "ext"),
    CANCELLED("400", "四","Order is cancelled", false, "ext");

    private final String code;
    private final String name;
    private final String des;
    private final boolean active;
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
