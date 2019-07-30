package com.jacky.practice;

/**
 * 生产者实例!
 *
 * @author Jacky Huang
 * @date 2018-01-29 16:04
 * @since jdk1.8
 */
public class SuperMarket implements Runnable {

	private int neednum; // 生产数量
	private Godown godown; // 产品仓库

	SuperMarket(int neednum, Godown godown) {
		this.neednum = neednum;
		this.godown = godown;
	}

	@Override
	public void run() {

		while (true) {
			// 生产指定数量产品
			this.godown.produce(this.neednum);

			// 不间断的生产
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
