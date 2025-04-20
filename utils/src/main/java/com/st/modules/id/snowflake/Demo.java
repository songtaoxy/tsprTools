package com.st.modules.id.snowflake;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Demo {
    public static void main(String[] args) throws UnknownHostException {

//        System.out.println( InetAddress.getLocalHost().getHostName());
        System.out.println("Long ID: " + SnowFlakeUtil.nextId());
//        System.out.println("Base62 ID: " + SnowFlakeUtil.nextIdBase62());
//        System.out.println("UUID ID: " + SnowFlakeUtil.nextIdAsUuid());
//        System.out.println("Instance Info: " + SnowFlakeUtil.getInstanceInfo());
    }
}
