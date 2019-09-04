package com.jacky.rpc.api;

import com.jacky.rpc.dto.UserDto;

/**
 * 1.定义接口
 *
 * @author Jacky
 * @date 2019/9/4 3:02 PM
 */

// 指定下具体的实现类
@RpcAnnotation(mapped = "com.jacky.rpc.server.UserServiceImpl")
public interface UserService {

    UserDto insertUser(UserDto userDto);
}
