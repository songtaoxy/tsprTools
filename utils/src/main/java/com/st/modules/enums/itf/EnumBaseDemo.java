package com.st.modules.enums.itf;

public class EnumBaseDemo {

    /**
     * 概述:
     * 演示 EnumBase 的核心能力
     * 功能清单:
     * 1 getByCode getByName
     * 2 getNameByCode getCodeByName
     * 3 有效性校验 find Optional
     * 4 列举与映射
     * 使用示例:
     * 直接运行 main
     * 注意事项:
     * 控制台输出用于演示
     * 入参与出参与异常:
     * 无
     */
    public static void main(String[] args) {
        BizEnum a = EnumBase.getByCode(BizEnum.class, "ERP");                             // ERP
        BizEnum b = EnumBase.getByName(BizEnum.class, "fgls", true);                      // FGLS
        String n1 = EnumBase.getNameByCode(BizEnum.class, "NC");                          // NC
        String c1 = EnumBase.getCodeByName(BizEnum.class, "ERP", false);                  // ERP
        boolean okName = EnumBase.isValidName(BizEnum.class, "NC", false);                // true
        boolean okCode = EnumBase.isValidCode(BizEnum.class, "ABC");                      // false
        BizEnum opt = EnumBase.findByCode(BizEnum.class, "FGLS").orElse(null);            // FGLS
        System.out.println(a);
        System.out.println(b);
        System.out.println(n1);
        System.out.println(c1);
        System.out.println(okName);
        System.out.println(okCode);
        System.out.println(opt);
        System.out.println(EnumBase.names(BizEnum.class));                                 // [FGLS, NC, ERP]
        System.out.println(EnumBase.codes(BizEnum.class));                                 // [FGLS, NC, ERP]
        System.out.println(EnumBase.mapNameToCode(BizEnum.class));                         // {FGLS=FGLS, NC=NC, ERP=ERP}
        System.out.println(EnumBase.mapCodeToName(BizEnum.class));                         // {FGLS=FGLS, NC=NC, ERP=ERP}
    }
}
