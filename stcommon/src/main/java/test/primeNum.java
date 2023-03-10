package test;

/**
 * @author: st
 * @date: 2023/3/10 09:34
 * @version: 1.0
 * @description:
 */
public class primeNum {

	static int count = 0;      //素数个数

	public static void main(String args[]) {
		System.out.println("101到200之间的素数：");
		for (int i = 101; i <= 200; i++) {
			calcNum(i);
		}
		System.out.println("一共有：" + count + "个素数。");
	}


	/**
	 * @param i
	 */
	private static void calcNum(int i) {
		int j = 2;
		while (j <= i / 2) {
			if (i % j == 0)
				break;
			j++;
		}
		if (j == i / 2 + 1) {
			System.out.println(i);
			count++;
		}
	}


	private static void calcNum2(int i) {
		int j = 2;                  //j作为除数，因为1素数也可以整除，所以不从1开始
		while (j <= i / 2) {           //对于素数来说，只要除以它一半以内的数测试就好，因为一个数                          //必定由一个小于等于它一半的数和一个大于等于它一半的数相乘i                         // 而得（例如4=2*2，10=2*5）
			if (i % j == 0)            //如果等于0代表除了1和本身外还有别的数可以整除
				break;
			j++;
		}
		if (j == i / 2 + 1) {            //当j=i/2+1时循环进不去了，代表遍历完都没有发现别的可以整除的数
			System.out.println(i);
			count++;
		}
	}

}
