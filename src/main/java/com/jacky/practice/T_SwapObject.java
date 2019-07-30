package com.jacky.practice;

public class T_SwapObject {

	private String name;
	private String sex;

	public T_SwapObject(String x, String y) {
		this.name = x;
		this.sex = y;
	}

	public String toString() {
		return name + " " + sex;
	}

	public static void swapObject(T_SwapObject t1, T_SwapObject t2) {
		T_SwapObject tmp = t1;
		t1 = t2;
		t2 = tmp;
	}

	public static void changeObject(T_SwapObject t1, T_SwapObject t2) {

		// 交换不能改变任何东西，因为java无引用传递概念，传递的是地址
		T_SwapObject tmp = t1;
		t1 = t2;
		t2 = tmp;

		// 但可以改变内部的数据或值
		t1.name = "xxxxxxxxxx";
	}

	public static void swapObjectArray(T_SwapObject[] t1, T_SwapObject[] t2) {
		T_SwapObject[] tmp = t1;
		t1 = t2;
		t2 = tmp;
	}

	public static void changeObjectArray(T_SwapObject[] t1, T_SwapObject[] t2) {
		T_SwapObject[] tmp = t1;
		t1 = t2;
		t2 = tmp;

		t1[0].name = "test";
	}

	public static void main(String args[]) {

		T_SwapObject t1 = new T_SwapObject("abc", "fale");
		T_SwapObject t2 = new T_SwapObject("def", "male");
		System.out.println("转换前：" + t1.toString());
		System.out.println("转换前：" + t2.toString());
		T_SwapObject.swapObject(t1, t2);
		System.out.println("转换后：" + t1.toString());
		System.out.println("转换后：" + t2.toString());

		T_SwapObject.changeObject(t1, t2);
		System.out.println("转换后：" + t1.toString());
		System.out.println("转换后：" + t2.toString());

		T_SwapObject[] arrays1 = new T_SwapObject[2];
		arrays1[0] = new T_SwapObject("a1", "1");
		arrays1[1] = new T_SwapObject("a2", "2");

		T_SwapObject[] arrays2 = new T_SwapObject[2];
		arrays2[0] = new T_SwapObject("b1", "1");
		arrays2[1] = new T_SwapObject("b2", "2");

		System.out.println(arrays1[0].toString() + " " + arrays1[1].toString());
		System.out.println(arrays2[0].toString() + " " + arrays2[1].toString());
		T_SwapObject.swapObjectArray(arrays1, arrays2);

		System.out.println(arrays1[0].toString() + " " + arrays1[1].toString());
		System.out.println(arrays2[0].toString() + " " + arrays2[1].toString());

		System.out.println(arrays1[0].toString() + " " + arrays1[1].toString());
		System.out.println(arrays2[0].toString() + " " + arrays2[1].toString());
		T_SwapObject.changeObjectArray(arrays1, arrays2);

		System.out.println(arrays1[0].toString() + " " + arrays1[1].toString());
		System.out.println(arrays2[0].toString() + " " + arrays2[1].toString());
	}
}
