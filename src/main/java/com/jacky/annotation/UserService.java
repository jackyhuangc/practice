package com.jacky.annotation;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/12 7:30 PM
 */
@Service("test")
@Data
//@Scope("prototype") // 每次调用都将生成新的实例
public class UserService implements IUserService {

    @Autowired // testService 未启用事务，也未加入aop拦截配置，生产的只是pojo原生对象
    TestService testService;

    @Override
    public String getUser(String name) {
        return name;
    }
}
