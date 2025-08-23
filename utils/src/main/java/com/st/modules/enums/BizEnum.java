package com.st.modules.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@AllArgsConstructor
@Getter
@ToString
@Slf4j
public enum BizEnum implements BaseEnum {

    FGLS("100","下发凭证至经费总账","经费总账",null,null),
    FAMS("110","下发凭证至固定资产","固定资产",null,null);

    private final String code;
    private final String name;
    private final String des;
    private final String active;
    private final String ext;


    public static BizEnum getByCode(String code) {
        return EnumUtils.getByCode(BizEnum.class, code).orElse(null);
    }

    public static BizEnum getByName(String name) {
        return EnumUtils.getByName(BizEnum.class, name).orElse(null);
    }

    public static String getNameByCode(String code) {
        return EnumUtils.getNameByCode(BizEnum.class, code);
    }

    public static String getCodeByName(String name) {
        return EnumUtils.getCodeByName(BizEnum.class, name);
    }
}
