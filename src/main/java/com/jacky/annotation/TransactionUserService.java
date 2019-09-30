package com.jacky.annotation;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/30 3:03 PM
 */
@Service("transactionUserService")
@Data
// @Transactional 该类的所有public方法都将启用事务
public class TransactionUserService implements IUserService {

    @Transactional // 配合@EnableTransactionManagement使用
    @Override
    public String getUser(String name) {
        return name;
    }
}
