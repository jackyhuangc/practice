package com.jacky.rpc.client;

import com.jacky.rpc.api.UserService;
import com.jacky.rpc.dto.UserDto;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/4 3:49 PM
 */
public class RpcClient {

    public static void main(String[] args) {

        UserService userService = RpcClientFactory.getProxyInstance(UserService.class);

        userService.insertUser(new UserDto());
    }
}
