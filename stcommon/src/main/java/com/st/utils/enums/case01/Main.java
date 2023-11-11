package com.st.utils.enums.case01;

import java.util.List;

public class Main {


    public static void main(String[] args) {
        List<SexEnum> items = EnumUtils.getEnumItems(SexEnum.class);
        System.out.println(items.toString());
        System.out.println(EnumUtils.getEnumCodes(SexEnum.class));
        System.out.println(EnumUtils.getEnumValues(SexEnum.class));
        System.out.println(EnumUtils.fromCode(SexEnum.class,"0"));
        System.out.println(EnumUtils.fromValue(SexEnum.class,"男"));
        System.out.println(EnumUtils.getEnumMapCode(SexEnum.class));
        System.out.println(EnumUtils.getEnumMapValue(SexEnum.class));
        System.out.println(EnumUtils.getEnumList(SexEnum.class));
    }
    /*
        [MALE, FEMALE]
        [0, 1]
        [男, 女]
        MALE
        MALE
        {0=男, 1=女}
        {女=1, 男=0}
        [{code=0, name=MALE, value=男}, {code=1, name=FEMALE, value=女}]
     */

}
