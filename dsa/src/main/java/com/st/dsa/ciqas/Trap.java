package com.st.dsa.ciqas;
// ref https://www.yuque.com/songtaoxy/gl7ku7/vnqn9tkoz6t7ze5u
public class Trap {

    public int trap(int[] height) {

        int n = height.length; //数组长度
        int sumHeight = 0;  // 柱子的总和(黑); 宽度是1, 面积: 1 * 高度, 就是柱子的高度

        int maxL = 0;
        int sumL = 0;
        for (int l = 0; l < n; l++) { // 从左到右
            maxL = height[l] > maxL ? height[l] : maxL;
            sumL += maxL; // 从左到右, 累加, 计算: 左边面积(蓝+黑)
            sumHeight += height[l]; // 从左到右, 累加, 计算: 面积(蓝); 从左到右一样
        }

        int maxR = 0;
        int sumR = 0;
        for (int r = n - 1; r >= 0; r--) {   // 从右到左
            maxR = height[r] > maxR ? height[r] : maxR;
            sumR += maxR; // 从右到左, 累加, 计算: 右边边面积(蓝+黑)
        }

        // 蓝色面积 = 左边面积 + 右边面积 - 矩形面积 - 柱子总和(黑)
        return sumL + sumR - maxL * n - sumHeight;
    }
}
