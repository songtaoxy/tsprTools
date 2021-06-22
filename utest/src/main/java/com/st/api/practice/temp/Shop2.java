    package com.st.api.practice.temp;


    import java.util.Comparator;
    import java.util.Objects;
    import java.util.TreeSet;

    /**
     * @author: st
     * @date: 2021/3/11 10:00
     * @version: 1.0
     * @description:
     */
    public class Shop2 {


        // 主键ID
        private Long shopId;
        // 店铺名称
        private String shopName;

        public Shop2(Long shopId, String shopName) {
            this.shopId = shopId;
            this.shopName = shopName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Shop2 shop = (Shop2) o;
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

        public static void main(String[] args) {
            TreeSet<Shop2> set = new TreeSet<Shop2>(Comparator.comparing(Shop2::getShopId).
                    thenComparing(Shop2::getShopName));
            set.add(new Shop2(1L, "qinxiaoyu"));
            set.add(new Shop2(1L, "wangzhaoning"));
            System.out.println(set);
        }
    }
    // [Shop{shopId=1, shopName='qinxiaoyu'}, Shop{shopId=1, shopName='wangzhaoning'}]