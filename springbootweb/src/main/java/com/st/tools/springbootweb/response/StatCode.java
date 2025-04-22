package com.st.tools.springbootweb.response;

import com.st.modules.enums.BaseEnum;
import com.st.modules.enums.EnumUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatCode implements BaseEnum {

    SUCCESS("200", "成功",null,"1",null,null),
    FAIL("500", "失败",null,null,null,null),
    PASSWORD_FAIL("3001", "密码错误",null,null,null,null),
    LOGIN_FAIL("3002", "用户验证失败",null,null,null,null),
    USER_IS_NOT_EXIST("3003", "用户不存在！",null,null,null,null),
    USER_IS_DISABLE("3004", "用户被禁用！",null,null,null,null),
    // 系统错误
    SYSTEM_ERROR("500", null,null,null,null,"error.internal"),
    // 参数校验财务
    VALIDATION_ERROR("400", null,null,null,null,"error.validation"),
    // 业务错误
    BIZ_ERROR("400",null,null,null,null,"error.biz"),
    // 空指针
    NULL_ERROR("410",null,null,null,null,"error.null"),
    // 找不到. 如文件, 文件查找内容, 数据库查不到等
    NOT_FOUND("404", null,null,null,null,"error.notfound");



    private final String code;
    private final String name;
    // 描述
    private final String des;
    // 是否激活. 0 未激活; 1激活
    private final String active;
    private final String ext;
    // 国际化. ref 国际化配置 {@code src/main/resources/i18n/message.properties}及相关配置
    private final String i18nKey;


    // 精简版接口调用
    public static StatCode getByCode(String code) {
        return EnumUtils.getByCode(StatCode.class, code).orElse(null);
    }

    public static StatCode getByName(String name) {
        return EnumUtils.getByName(StatCode.class, name).orElse(null);
    }

    public static String getNameByCode(String code) {
        return EnumUtils.getNameByCode(StatCode.class, code);
    }

    public static String getCodeByName(String name) {
        return EnumUtils.getCodeByName(StatCode.class, name);
    }

}