package com.jacky.practice;

import org.junit.Test;

/**
 * 请输入描述
 *
 * @author Jacky
 * @date 2019/9/18 9:53 AM
 */
public class T_Test {

    @Test
    public void test1() {
        String str = "{\n" +
                "    \"type\": \"LoanSync\",\n" +
                "    \"key\": \"74e0949f17cf89cf50c991947f5bd4a4\",\n" +
                "    \"from_system\": \"BIZ\",\n" +
                "    \"data\": {\n" +
                "        \"order_no\": \"KN_TEST_order_000003\",\n" +
                "        \"channel_code\": \"V_company_code\",\n" +
                "        \"mem_acct_no\": \"enc_03_34634203690_604\",\n" +
                "        \"mem_name\": \"enc_04_34634203700_474\",\n" +
                "        \"mem_cert_no\": \"enc_02_34634203350_673\",\n" +
                "        \"mem_mobile\": \"enc_01_34634203320_534\",\n" +
                "        \"amount\":100 ,\n" +
                "        \"memo\": \"1231231123\"\n" +
                "    }\n" +
                "}\n";

        assert null != str;
    }

    @Test
    public void test2() {
        T1_Enum t_enum = T1_Enum.valueOf("Spring");
        assert null != t_enum;

        T2_Enum t2_enum=T2_Enum.valueOf("Spring");
        assert null != t2_enum;
    }
}

enum T1_Enum {
    Spring,
    Winter
}

enum T2_Enum {
    Spring("Spring", "XXX"),
    Winter("Winter", "YYY");

    private String code;
    private String desr;

    T2_Enum(String code, String desr) {
        this.code = code;
        this.desr = desr;
    }
}