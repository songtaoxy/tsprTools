package com.st.modules.enums.common;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<SexEnum> items = EnumUtils.getEnumItems(SexEnum.class);
        System.out.println(items.toString());
        System.out.println(EnumUtils.getEnumCodes(SexEnum.class));
        System.out.println(EnumUtils.getEnumValues(SexEnum.class));
        System.out.println(EnumUtils.fromCode(SexEnum.class,"0"));
        System.out.println(EnumUtils.fromName(SexEnum.class,"男"));
        System.out.println(EnumUtils.fromValue(SexEnum.class,"男"));
        System.out.println(EnumUtils.getEnumMapCode(SexEnum.class));
        System.out.println(EnumUtils.getEnumMapValue(SexEnum.class));
        System.out.println(EnumUtils.getEnumList(SexEnum.class));

        //System.out.println(EnumUtils.isEnum(null));
        System.out.println(EnumUtils.fromEnumName(SexEnum.class,"MALE").getCode());
    }

}
