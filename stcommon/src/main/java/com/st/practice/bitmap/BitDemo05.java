package com.st.practice.bitmap;

import java.util.BitSet;

/**
 * @author: st
 * @date: 2022/3/9 17:07
 * @version: 1.0
 * @description:
 */
public class BitDemo05 {
	public static void main(String[] args) {

		// create 2 bitsets
		BitSet bitset1 = new BitSet(8);
		BitSet bitset2 = new BitSet(8);

		// assign values to bitset1
		bitset1.set(0);
		bitset1.set(1);
		bitset1.set(2);
		bitset1.set(3);
		bitset1.set(4);
		bitset1.set(5);

		// assign values to bitset2
		bitset2.set(2);
		bitset2.set(4);
		bitset2.set(6);
		bitset2.set(8);
		bitset2.set(10);

		// print the sets
		System.out.println("Bitset1:" + bitset1);
		System.out.println("Bitset2:" + bitset2);

		// set more values
		bitset1.set(1, 10, false);
		bitset2.set(5, 15, true);

		// print the sets
		System.out.println("Bitset1:" + bitset1);
		System.out.println("Bitset2:" + bitset2);
	}
}
