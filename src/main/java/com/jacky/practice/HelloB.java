package com.jacky.practice;

import org.junit.Test;

/**
 * Description Here!
 *
 * @author Jacky Huang
 * @date 2018-02-02 10:43
 * @since jdk1.8
 */
class HelloA {

	// 3、将执行基类/子类构造函数（构造器）
	public HelloA() {
		System.out.println("HelloA");
	}

	// 2、将执行基类/子类代码构造块
	{
		System.out.println("I'm A class");
	}

	// 1、先执行基类/子类static修饰语句
	static {
		System.out.println("static A");
	}



}

/**
 * （1）类加载之后，按从上到下（从父类到子类）执行被static修饰的语句；（2）当static语句执行完之后,再执行main方法；（3）如果有语句new了自身的对象，将从上到下执行构造代码块、构造器（两者可以说绑定在一起）。
 *
 * @author huang
 *
 */
public class HelloB extends HelloA {
	public HelloB() {
		System.out.println("HelloB");
	}

	{
		System.out.println("I'm B class");
	}

	static {
		System.out.println("static B");
	}

	public static void main(String[] args) {

		System.out.println("-------main start-------");

		// 2、再执行main方法，并且new对象，将从上到下执行构造代码块、构造器
		new HelloB();
		new HelloB();
		System.out.println("-------main end-------");
	}
}