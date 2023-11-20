package com.st.modules.enums.common;



public enum SexEnum implements EnumItem<String> {

    //实现EnumItem接口，并指定该枚举code的数据类型
    MALE("0", "男","","",""),
    FEMALE("1", "女","","","");


    private final String code;
    private final String name;
    private final String key;
    private final String value;
    private final String des;

    SexEnum(String code, String name, String key, String value, String des) {
        this.code = code;
        this.name = name;
        this.key = key;
        this.value = value;
        this.des = des;
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
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
        return des;
    }

    @Override
    public String toString() {
        return "SexEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", des='" + des + '\'' +
                '}';
    }
}
