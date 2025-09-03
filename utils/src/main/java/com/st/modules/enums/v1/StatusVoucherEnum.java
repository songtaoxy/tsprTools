package com.st.modules.enums.v1;


import com.st.modules.enums.v2.EnumUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 *     状态说明
 *     - 凭证初始状态
 *     - 下发经费总账: 过滤条件 100, 120
 *     - 下发固定资产: 下发给经费总账成功的,才下发给固定资产. 过滤条件 110
 *
 *     table & field
 *     - table:gl_voucher
 *     - field:free9
 * </pre>
 */
@AllArgsConstructor
@Getter
@ToString
@Slf4j
public enum StatusVoucherEnum implements BaseEnum{
    INIT("100","未下发","初始态",null,null),
    FGLS_OK("110","下发经费总账成功",null,null,null),
    FGLS_FAIL("120","下发经费总账失败",null,null,null),
    FAMS_OK("130","下发固定资产成功","下发经费总账成功,下发固定资产成功",null,null),
    FAMS_FAIL("140","下发固定资产失败","下发经费总账成功,下发固定资产失败",null,null);

    private final String code;
    private final String name;
    private final String des;
    private final String active;
    private final String ext;


    public static StatusVoucherEnum getByCode(String code) {
        return EnumUtils.getByCode(StatusVoucherEnum.class, code).orElse(null);
    }

    public static StatusVoucherEnum getByName(String name) {
        return EnumUtils.getByName(StatusVoucherEnum.class, name).orElse(null);
    }

    public static String getNameByCode(String code) {
        return EnumUtils.getNameByCode(StatusVoucherEnum.class, code);
    }

    public static String getCodeByName(String name) {
        return EnumUtils.getCodeByName(StatusVoucherEnum.class, name);
    }
}
