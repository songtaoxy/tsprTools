package com.st.api.practice.temp;

import java.util.Objects;
import java.util.TreeSet;

/**
 * @author: st
 * @date: 2021/3/11 10:05
 * @version: 1.0
 * @description:
 */

public class Shop1 implements Comparable<Shop1> {
    // 主键ID
    private Long shopId;
    // 店铺名称
    private String shopName;

    public Shop1(Long shopId, String shopName) {
        this.shopId = shopId;
        this.shopName = shopName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shop1 shop = (Shop1) o;
        return Objects.equals(shopId, shop.shopId) &&
                Objects.equals(shopName, shop.shopName);

    }

    @Override
    public int hashCode() {
        return Objects.hash(shopId, shopName);
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    @Override
    public String toString() {
        return "Shop{" +
                "shopId=" + shopId +
                ", shopName='" + shopName + '\'' +
                '}';
    }

    @Override
    public int compareTo(Shop1 that) {
        return shopId.compareTo(that.shopId);
    }


    public static void main(String[] args) {

        TreeSet<Shop1> set = new TreeSet<>();
        set.add(new Shop1(1L, "qinxiaoyu"));
        System.out.println(set);
        set.add(new Shop1(1L, "wangzhaoning"));
        System.out.println(1+1);
        test();
        System.out.println(set);
    }

    public static void test() {

        System.out.println();
        test1();
        System.out.println();

    }


    public static void test1() {
        System.out.println();
        test2();

    }

    public static void test2() {
        System.out.println();

    }

}
//output:
//[Shop{shopId=1, shopName='qinxiaoyu'}]
