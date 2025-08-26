package com.st.modules.enums.itf;

/**
 * 概述:
 * 业务场景枚举 示例实现 EnumBase
 * 功能清单:
 * 1 code 为字符串
 */
enum BizEnum implements EnumBase<String> {
    FGLS("FGLS"),
    NC("NC"),
    ERP("ERP");
    private final String code;
    BizEnum(String code) { this.code = code; }
    @Override public String getCode() { return code; }
}



