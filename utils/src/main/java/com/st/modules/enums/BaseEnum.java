package com.st.modules.enums;

import java.time.LocalDateTime;

// 定义一个接口或抽象类来统一属性
public interface BaseEnum {

    String getCode();       // 获取枚举的标识符

    String getName();

    String getDes(); // 获取枚举的描述信息

    // 是否激活. 0 未激活; 1激活
    String getActive();      // 是否激活

    // 扩展信息
    String getExt();

//    LocalDateTime getCreatedTime(); // 创建时间
//
//    LocalDateTime getUpdatedTime(); // 更新时间

}
