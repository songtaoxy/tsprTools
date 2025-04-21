package com.st.modules.response.response;


import com.st.modules.enums.common.EnumItem;

/**
 * @Author ywl
 * @Description 响应状态枚举
 * @Date 2022/4/12 10:41
 */
public enum ResponseEnum implements EnumItem<String> {
    SUCCESS("200", "成功",null,null,null),
    FAIL("500", "失败",null,null,null),
    PASSWORD_FAIL("3001", "密码错误",null,null,null),
    LOGIN_FAIL("3002", "用户验证失败",null,null,null),
    USER_IS_NOT_EXIST("3003", "用户不存在！",null,null,null),
    USER_IS_DISABLE("3004", "用户被禁用！",null,null,null);

    // 状态码
    private final String code;

    // 响应信息
    private final String name;

    private final String value;
    private final String extInfo;
    private final String des;



    ResponseEnum(String code, String name, String extInfo, String value, String des) {
        this.code = code;
        this.name = name;
        this.extInfo = extInfo;
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
    public String getValue() {
        return value;
    }

    @Override
    public String getExtInfo() {
        return extInfo;
    }

    @Override
    public String getDes() {
        return des;
    }
}
