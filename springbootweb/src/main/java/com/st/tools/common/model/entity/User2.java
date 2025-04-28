package com.st.tools.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * @TableName user2
 */

@ApiModel(description="用户对象user")
@TableName(value ="user2")
@Data
public class User2 implements Serializable {
    /**
     * 主键ID
     */
    @Schema(description = "用户的唯一标识符", required = true, example = "12345")
    @TableId
    private Long id;

    /**
     * 姓名
     */
    @ApiModelProperty(value="用户名",name="username")
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    @Schema(description = "用户的电子邮件地址", required = true, format = "email", example = "johndoe@example.com")
    private String email;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}