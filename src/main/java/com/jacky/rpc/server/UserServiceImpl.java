package com.jacky.rpc.server;

import com.jacky.rpc.api.UserService;
import com.jacky.rpc.dto.UserDto;

import java.util.Random;

/**
 * 2.接口实现
 *
 * @author Jacky
 * @date 2019/9/4 3:05 PM
 */
public class UserServiceImpl implements UserService {
    @Override
    public UserDto insertUser(UserDto userDto) {
        userDto.setAge(new Random().nextInt(100));
        return userDto;
    }
}
