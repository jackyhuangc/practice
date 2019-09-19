package com.jacky.condition;

import org.springframework.stereotype.Service;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/19 3:43 PM
 */
@Service
public class Billy implements Fighter {

    public Billy() {

        System.out.println("Billy：正在构建中......");

    }

    @Override
    public void fight() {
        System.out.println("Billy：吾乃新日暮里的王，三界哲学的主宰。");

    }
}