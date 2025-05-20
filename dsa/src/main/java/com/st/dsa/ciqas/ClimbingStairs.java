package com.st.dsa.ciqas;

/**
 * <pre>
 *     - 动态规划: 爬楼梯
 *     - 假设你正在爬楼梯，每次可以爬 1阶 或 2阶。问：爬到第 n 阶有多少种不同的方法？
 *     - 动态规划. ref obsidian
 * </pre>
 */
public class ClimbingStairs {

    /**
     * <pre>
     *     - 动态规划
     *     -时间复杂度：O(n)，需要遍历 n 次。
     *     -空间复杂度：O(n)（数组存储所有状态）。
     * </pre>
     * @param n
     * @return
     */
    public int m1(int n) {
        if (n <= 1) return 1; // 边界条件
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    /**
     * <pre>
     *     - 动态规划 + 保存前面的解
     *     -时间复杂度：O(n)，需要遍历 n 次。
     *     -空间复杂度：O(1)（仅用3个变量）
     *     - test ref {@code ClimbingStairsTest}
     * </pre>
     * @param n
     * @return
     */
    public int m2(int n) {
        if (n <= 1) return 1; // 边界条件
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }
}
