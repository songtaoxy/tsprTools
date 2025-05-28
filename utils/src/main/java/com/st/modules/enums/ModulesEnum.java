package com.st.modules.enums;

import lombok.*;
import lombok.extern.slf4j.Slf4j;


@AllArgsConstructor
@Getter
@ToString
@Slf4j
public enum ModulesEnum implements BaseEnum {

    FGLS("100","fgls","经费总账",null,null),
    FAMS("110","fams","固定资产",null,null);

    private final String code;
    private final String name;
    private final String des;
    private final String active;
    private final String ext;


    public static ModulesEnum getByCode(String code) {
        return EnumUtils.getByCode(ModulesEnum.class, code).orElse(null);
    }

    public static ModulesEnum getByName(String name) {
        return EnumUtils.getByName(ModulesEnum.class, name).orElse(null);
    }

    public static String getNameByCode(String code) {
        return EnumUtils.getNameByCode(ModulesEnum.class, code);
    }

    public static String getCodeByName(String name) {
        return EnumUtils.getCodeByName(ModulesEnum.class, name);
    }
}
