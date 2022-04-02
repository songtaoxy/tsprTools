package com.st.practice.snowflake;

import cn.hutool.core.net.NetUtil;

/**
 * @author: st
 * @date: 2022/3/25 02:23
 * @version: 1.0
 * @description:
 */
public class SnowFlakeDemo {

  public static void main(String[] args) {

    long l = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
    System.out.println(NetUtil.getLocalhostStr());
    System.out.println(NetUtil.getLocalhost());
  }
}
