package com.st.utils6.common;

import cn.hutool.core.util.ObjectUtil;
import com.st.practice.com.Person;

public class Main {

    public static void main(String[] args) {


        Person person1 = new Person();
        Person person2 = new Person();
        boolean equals = ObjectUtil.notEqual(person1, person2);
//        boolean equals = BaseUtil.equals(person1, person2);

        boolean empty = ObjectUtil.isEmpty(person1);


        System.out.println(equals);


    }





}
