package com.st.modules.enums.v1;

import com.st.modules.enums.v2.EnumUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * tables dict
 */
@AllArgsConstructor
@Getter
@ToString
@Slf4j
public enum DBsEnum implements BaseEnum{
    // 应付单; 应付单和凭证, 暂时没有找到关联关系
    ap_payablebill("100000", "ap_payablebill", "供应商应付单表(主)", null, null),
    ap_payableitem("100005", "ap_payableitem", "供应商应付单行(子)", null, null),
   // 凭证
    gl_voucher("100010", "gl_voucher", "凭证表", null, "pk_voucher"),
    gl_detail("100011", "gl_detail", "凭证分录", null, "pk_detail@pk_voucher"),
    gl_dtlfreevalue("100012", "gl_dtlfreevalue", "凭证分录自定义项", null, "pk_dtlfreevalue@pk_detail");


    private final String code;
    private final String name;
    private final String des;
    private final String active;
    private final String ext;


    public static DBsEnum getByCode(String code) {
        return EnumUtils.getByCode(DBsEnum.class, code).orElse(null);
    }

    public static DBsEnum getByName(String name) {
        return EnumUtils.getByName(DBsEnum.class, name).orElse(null);
    }

    public static String getNameByCode(String code) {
        return EnumUtils.getNameByCode(DBsEnum.class, code);
    }

    public static String getCodeByName(String name) {
        return EnumUtils.getCodeByName(DBsEnum.class, name);
    }
}
