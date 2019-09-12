package com.jacky.annotation;

import org.springframework.stereotype.Service;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/12 7:30 PM
 */
@Service("test")
public class UserService implements IUserService {

    @Override
    public String getUser(String name) {
        return name;
    }
}
