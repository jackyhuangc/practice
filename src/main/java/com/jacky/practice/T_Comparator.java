package com.jacky.practice;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * JavaBean对象工具类
 *
 * @author huangchao
 * @create 2018/6/6 下午1:55
 * @desc
 **/
public class T_Comparator {

    public static void main(String[] args) throws UnknownHostException {
        List<Person> people = new ArrayList<Person>();
        people.add(new Person("C", 21));
        people.add(new Person("T", 20));
        people.add(new Person("B", 35));
        people.add(new Person("A", 22));

        String computerName = InetAddress.getLocalHost().getHostName();

        // 默认升序
        people.sort(Comparator.comparingInt(Person::getAge));
        people.forEach(System.out::println);

        people.sort((Person p1, Person p2) -> {
            return p1.getAge() - p2.getAge();
        });
        people.forEach(System.out::println);
    }
}

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.age + ")";
    }

}