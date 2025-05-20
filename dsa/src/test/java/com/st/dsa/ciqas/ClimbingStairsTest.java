package com.st.dsa.ciqas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClimbingStairsTest {

    @Test
    void m2() {
        ClimbingStairs solution = new ClimbingStairs();
        System.out.println(solution.m2(2)); // 输出：2（1+1 或 2）
        System.out.println(solution.m2(3)); // 输出：3（1+1+1, 1+2, 2+1）
        System.out.println(solution.m2(5)); // 输出：8
    }
}