package com.jacky.practice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Description Here!
 * 
 * @author Jacky Huang
 * @date 2018-02-10 18:29
 * @since jdk1.8
 */
public class T_Copy {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List src1 = new ArrayList();
		src1.add(" a ");
		src1.add(" b ");
		src1.add(" c ");
		/** **************************** */
		List des1 = new ArrayList();
		Collections.addAll(des1,new String[3]);
		Collections.copy(des1, src1);
		
		System.out.println(src1.size());
		System.out.println(des1.size());
		
		des1.remove(1);
		des1.remove(0);
		System.out.println(src1.size());
		System.out.println(des1.size());
	}

}
