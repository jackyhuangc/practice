package com.jacky.annotation;

import com.jacky.common.util.LogUtil;
import org.springframework.stereotype.Service;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/29 6:01 PM
 */
@Service
public class TestService {


    public TestService() {
        LogUtil.info("这是构造函数......");
    }

    public void init() {
        LogUtil.info("调用init方法......");
    }

    public String getTest() {
        return "";
    }

    public void destory() {
        LogUtil.info("调用destory方法......");
    }

    private String name;

    public String getName() {
        LogUtil.info("get name......");
        return name;
    }

    public void setName(String name) {
        LogUtil.info("set name......");
        this.name = name;
    }
}
