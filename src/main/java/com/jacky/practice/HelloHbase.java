package com.jacky.practice;

import org.apache.commons.net.ntp.TimeStamp;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

public class HelloHbase {
    static Configuration conf = null;


    private static Admin admin;

    static {
        conf = HBaseConfiguration.create();
        // 本地hosts必须配置  127.0.0.1 myhbase ,hbase中会用到？？？
        conf.set("hbase.zookeeper.quorum", "myhbase");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("log4j.logger.org.apache.hadoop.hbase", "INFO");
        //集群配置↓
        //configuration.set("hbase.zookeeper.quorum", "101.236.39.141,101.236.46.114,101.236.46.113");
        //conf.set("hbase.master", "127.0.0.1:60000");
    }


    private static void addData(String tableName, String rowKey, String family, String[] columns, String[] values) {
        try {
            Connection connection = ConnectionFactory.createConnection(conf);
            Table table = connection.getTable(TableName.valueOf(tableName));

            // 上表中的 fn 和 ln 称之为 Column-key 或者 Qulifimer
            // RowKey , Family=CF ,Qulifier
            // 列簇Columns  Family：HBASE表中的每个列，都归属于某个列族。列族是表的schema的一部分(而列不是)，必须在使用表之前定义。列名都以列族作为前缀。例如courses：history，courses：math 都属于courses这个列族。
            // 列簇 如 f1:c1  ke:test
            // 列限定符（column qualifier）
            Put put = new Put(Bytes.toBytes(rowKey));
            for (int i = 0; i < columns.length; i++) {
                put.addColumn(Bytes.toBytes(family), Bytes.toBytes(columns[i]), Bytes.toBytes(values[i]));

            }
            table.put(put);
        } catch (Exception e) {
            System.out.println("add data exception:");
            e.printStackTrace();
        }
    }

    private static Result readData(String tableName, String rowKey, String family) {
        try {
            Connection connection = ConnectionFactory.createConnection(conf);
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addFamily(Bytes.toBytes(family));
            Result result = table.get(get);
            return result;
        } catch (Exception e) {
            System.out.println("get data exception:");
            e.printStackTrace();
        }
        return null;
    }
    private static String zkServer = "localhost";
    private static Integer port = 9095;
    private static TableName tableName = TableName.valueOf("testflink");
    private static final String cf = "ke";
    public static void main(String[] args) throws IOException {

//        Configuration config = HBaseConfiguration.create();
//
//        config.set("hbase.zookeeper.quorum","localhost");
//        config.set("hbase.zookeeper.property.clientPort","2181");
//
//        System.out.println("开始连接hbase");
//        Connection connect = ConnectionFactory.createConnection(config);
//        System.out.println(connect.isClosed());
//
//        Admin admin = connect.getAdmin();
//        System.out.println("连接成功");
////        admin.listTableNames();
////        Table table = connect.getTable(TableName.valueOf("midas_ctr_test"));
//        System.out.println("获取表数据成功");
////        for i :table.getScanner().iterator();
//
//
//        if (!admin.tableExists(tableName)) {
//            admin.createTable(new HTableDescriptor(tableName).addFamily(new HColumnDescriptor(cf)));
//        }
//        System.out.println("建表数据成功");
//
//        Table table = connect.getTable(tableName);
//        TimeStamp ts = new TimeStamp(new Date());
//        Date date = ts.getDate();
//        Put put = new Put(Bytes.toBytes(date.getTime()));
//        put.addColumn(Bytes.toBytes(cf), Bytes.toBytes("test"), Bytes.toBytes("test1"));
//        table.put(put);
//        table.close();
//        connect.close();

        String tableName = "tl";
        String family = "f1";
        String rowKey = "row1";
        System.out.println("add data");
        addData(tableName, rowKey, family, new String[]{"c1"}, new String[]{"v1"});
        System.out.println("read data");
        Result result = readData(tableName, rowKey, family);
        for (Cell cell : result.listCells()) {
            System.out.println("family:" + Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength()));
            System.out.println("qualifier:" + Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()));
            System.out.println("value:" + Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            System.out.println("Timestamp:" + cell.getTimestamp());
        }
        //test();
    }

    public static void test() {
        try {
            createTable("user_table", new String[]{"information", "contact"});
            User user = new User("001", "xiaoming", "123456", "man", "20", "13355550021", "1232821@csdn.com");
            insertData("user_table", user);
            User user2 = new User("002", "xiaohong", "654321", "female", "18", "18757912212", "214214@csdn.com");
            insertData("user_table", user2);
            List<User> list = getAllData("user_table");
            System.out.println("--------------------插入两条数据后--------------------");
            for (User user3 : list) {
                System.out.println(user3.toString());
            }
            System.out.println("--------------------获取原始数据-----------------------");
            getNoDealData("user_table");
            System.out.println("--------------------根据rowKey查询--------------------");
            User user4 = getDataByRowKey("user_table", "user-001");
            System.out.println(user4.toString());
            System.out.println("--------------------获取指定单条数据-------------------");
            String user_phone = getCellData("user_table", "user-001", "contact", "phone");
            System.out.println(user_phone);
            User user5 = new User("test-003", "xiaoguang", "789012", "man", "22", "12312132214", "856832@csdn.com");
            insertData("user_table", user5);
            List<User> list2 = getAllData("user_table");
            System.out.println("--------------------插入测试数据后--------------------");
            for (User user6 : list2) {
                System.out.println(user6.toString());
            }
            deleteByRowKey("user_table", "user-test-003");
            List<User> list3 = getAllData("user_table");
            System.out.println("--------------------删除测试数据后--------------------");
            for (User user7 : list3) {
                System.out.println(user7.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //连接集群
    public static Connection initHbase() throws IOException {

        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "127.0.0.1");
        //集群配置↓
        //configuration.set("hbase.zookeeper.quorum", "101.236.39.141,101.236.46.114,101.236.46.113");
        configuration.set("hbase.master", "127.0.0.1:60000");
        Connection connection = ConnectionFactory.createConnection(configuration);
        return connection;
    }

    //创建表
    public static void createTable(String tableNmae, String[] cols) throws IOException {

        TableName tableName = TableName.valueOf(tableNmae);
        admin = initHbase().getAdmin();
        if (admin.tableExists(tableName)) {
            System.out.println("表已存在！");
        } else {
            HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
            for (String col : cols) {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(col);
                hTableDescriptor.addFamily(hColumnDescriptor);
            }
            admin.createTable(hTableDescriptor);
        }
    }

    //插入数据
    public static void insertData(String tableName, User user) throws IOException {
        TableName tablename = TableName.valueOf(tableName);
        Put put = new Put(("user-" + user.getId()).getBytes());
        //参数：1.列族名  2.列名  3.值
        put.addColumn("information".getBytes(), "username".getBytes(), user.getUsername().getBytes());
        put.addColumn("information".getBytes(), "age".getBytes(), user.getAge().getBytes());
        put.addColumn("information".getBytes(), "gender".getBytes(), user.getGender().getBytes());
        put.addColumn("contact".getBytes(), "phone".getBytes(), user.getPhone().getBytes());
        put.addColumn("contact".getBytes(), "email".getBytes(), user.getEmail().getBytes());
        //HTable table = new HTable(initHbase().getConfiguration(),tablename);已弃用
        Table table = initHbase().getTable(tablename);
        table.put(put);
    }

    //获取原始数据
    public static void getNoDealData(String tableName) {
        try {
            Table table = initHbase().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            ResultScanner resutScanner = table.getScanner(scan);
            for (Result result : resutScanner) {
                System.out.println("scan:  " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //根据rowKey进行查询
    public static User getDataByRowKey(String tableName, String rowKey) throws IOException {

        Table table = initHbase().getTable(TableName.valueOf(tableName));
        Get get = new Get(rowKey.getBytes());
        User user = new User();
        user.setId(rowKey);
        //先判断是否有此条数据
        if (!get.isCheckExistenceOnly()) {
            Result result = table.get(get);
            for (Cell cell : result.rawCells()) {
                String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                if (colName.equals("username")) {
                    user.setUsername(value);
                }
                if (colName.equals("age")) {
                    user.setAge(value);
                }
                if (colName.equals("gender")) {
                    user.setGender(value);
                }
                if (colName.equals("phone")) {
                    user.setPhone(value);
                }
                if (colName.equals("email")) {
                    user.setEmail(value);
                }
            }
        }
        return user;
    }

    //查询指定单cell内容
    public static String getCellData(String tableName, String rowKey, String family, String col) {

        try {
            Table table = initHbase().getTable(TableName.valueOf(tableName));
            String result = null;
            Get get = new Get(rowKey.getBytes());
            if (!get.isCheckExistenceOnly()) {
                get.addColumn(Bytes.toBytes(family), Bytes.toBytes(col));
                Result res = table.get(get);
                byte[] resByte = res.getValue(Bytes.toBytes(family), Bytes.toBytes(col));
                return result = Bytes.toString(resByte);
            } else {
                return result = "查询结果不存在";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "出现异常";
    }

    //查询指定表名中所有的数据
    public static List<User> getAllData(String tableName) {

        Table table = null;
        List<User> list = new ArrayList<User>();
        try {
            table = initHbase().getTable(TableName.valueOf(tableName));
            ResultScanner results = table.getScanner(new Scan());
            User user = null;
            for (Result result : results) {
                String id = new String(result.getRow());
                System.out.println("用户名:" + new String(result.getRow()));
                user = new User();
                for (Cell cell : result.rawCells()) {
                    String row = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                    //String family =  Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength());
                    String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    user.setId(row);
                    if (colName.equals("username")) {
                        user.setUsername(value);
                    }
                    if (colName.equals("age")) {
                        user.setAge(value);
                    }
                    if (colName.equals("gender")) {
                        user.setGender(value);
                    }
                    if (colName.equals("phone")) {
                        user.setPhone(value);
                    }
                    if (colName.equals("email")) {
                        user.setEmail(value);
                    }
                }
                list.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //删除指定cell数据
    public static void deleteByRowKey(String tableName, String rowKey) throws IOException {

        Table table = initHbase().getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        //删除指定列
        //delete.addColumns(Bytes.toBytes("contact"), Bytes.toBytes("email"));
        table.delete(delete);
    }

    //删除表
    public static void deleteTable(String tableName) {

        try {
            TableName tablename = TableName.valueOf(tableName);
            admin = initHbase().getAdmin();
            admin.disableTable(tablename);
            admin.deleteTable(tablename);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

class User {

    private String id;
    private String username;
    private String password;
    private String gender;
    private String age;
    private String phone;
    private String email;

    public User(String id, String username, String password, String gender, String age, String phone, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.email = email;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}