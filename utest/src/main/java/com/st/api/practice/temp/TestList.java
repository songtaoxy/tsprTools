package com.st.api.practice.temp;

import jdk.nashorn.internal.ir.ContinueNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.spi.LocaleServiceProvider;

/**
 * @author: st
 * @date: 2021/3/29 13:37
 * @version: 1.0
 * @description:
 */
public class TestList {

    public static void main(String[] args) {
        List<String> filterStr = new ArrayList<>();

        List<String> sites = new ArrayList<String>();
        sites.add("Google");
        sites.add("Google_1");
        sites.add("Google_2");
        sites.add("Runoob");
        sites.add("Taobao");
        sites.add("Weibo");
        sites.add("Google_r");
        sites.add("Weibo_2");

        System.out.println(sites);

        sites.forEach(site -> {
            if (site.startsWith("G")) {
//                filterStr.add(site);
                return;
            }

            filterStr.add(site);
        });

        System.out.println(filterStr);
    }


}
