package com.st.practice.sort;

import com.google.common.primitives.Ints;

import java.util.Arrays;
import java.util.BitSet;
import java.util.OptionalInt;

/**
 * @author: st
 * @date: 2022/3/12 19:32
 * @version: 1.0
 * @description:
 */
public class TestDemo {

  public static void main(String[] args) {

    int[] array = new int[] {5, 6, 7, 8, 9, 4, 3, 2, 1};



    BitSet bitSet = new BitSet();

    for (int j : array) {

      bitSet.set(j);
    }

    OptionalInt max = bitSet.stream().max();

    OptionalInt max2 = Arrays.stream(array).max();
    int i = Arrays.asList(array).indexOf(max2.getAsInt());
    System.out.println("i----"+i);
    int i1 = Ints.indexOf(array, max.getAsInt());
    System.out.println(i1);



    System.out.println(max2.getAsInt());

    System.out.println(max.getAsInt());

    int max1 = max(array, 0, array.length - 1);
    System.out.println(max1);
  }

  public static int max(int[] array, int start, int end){

    while (start<end){
      int middle = start+(end-start)/2;
      if (array[middle] < array[middle + 1]) {
        start = middle + 1;
      } else {
        end = middle;
      }
    }
    return start;

}








}
