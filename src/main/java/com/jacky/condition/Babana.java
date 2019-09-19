package com.jacky.condition;

import org.springframework.stereotype.Service;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/19 3:42 PM
 */
@Service
public class Babana implements Fighter {
    @Override
    public void fight() {
        System.out.println("Banana: 自由的气息，蕉迟但到");
    }
}