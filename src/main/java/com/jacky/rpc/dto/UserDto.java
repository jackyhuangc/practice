package com.jacky.rpc.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jacky
 * @date 2019/9/4 2:57 PM
 */
@Data
public class UserDto implements Serializable {
    private static final long serialVersionUID = 2568025869019852378L;

    private String name;
    private Integer age;
}
