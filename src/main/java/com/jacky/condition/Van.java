package com.jacky.condition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/19 3:41 PM
 */
@Service
public class Van {
    @Autowired
    private Fighter fighter;

    public void fight(){
        System.out.println("van：boy next door,do you like 玩游戏");
        fighter.fight();
    }
}