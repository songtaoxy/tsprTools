package com.st.modules.enums.case01;

public enum SexEnum implements EnumItem<String>{

    //实现EnumItem接口，并指定该枚举code的数据类型
    MALE("0", "男"),
    FEMALE("1", "女"),
    ;
    private final String code;
    private final String value;

    SexEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name();
    }
}
