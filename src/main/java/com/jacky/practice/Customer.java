package com.jacky.practice;

/**
 * 消费者实例!
 *
 * @author Jacky Huang
 * @date 2018-01-29 16:05
 * @since jdk1.8
 */
public class Customer implements Runnable {

	private int neednum; // 消费数量
	private Godown godown; // 产品仓库

	Customer(int neednum, Godown godown) {
		this.neednum = neednum;
		this.godown = godown;
	}

	@Override
	public void run() {

		while (true) {
			// 消费指定数量产品
			this.godown.consume(this.neednum);

			// 不间断的消费
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
