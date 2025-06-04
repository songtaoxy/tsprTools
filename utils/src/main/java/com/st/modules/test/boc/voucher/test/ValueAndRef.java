package com.st.modules.test.boc.voucher.test;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;

import java.util.HashMap;
import java.util.Map;

public class ValueAndRef
{
    public static void main(String[] args) {
        Person m1 = null;
        Person m2 = null;
        System.out.println("m1 hash:"+m1); // null
        System.out.println("m2 hash:"+m2); // null

        Person ma = new Person();
        Person mb = new Person();
        // Person@2aafb23c
        System.out.println("ma hash:"+ma);
        // Person@2b80d80f
        System.out.println("mb hash:"+mb);

        m2 = ma;
        m1 = m2;
        // Person@2aafb23c
        System.out.println("m2 hash:"+m2);
        // Person@2aafb23c
        System.out.println("m1 hash:"+m1);

        m2=mb;
        // Person@2b80d80f
        System.out.println("m2 hash:"+m2);
        // Person@2aafb23c
        System.out.println("m1 hash:"+m1);

        Person person = new Person("小李");
        new ValueAndRef().testParam(person);
        new ValueAndRef().testParam2(person);
    }


    public void testParam(Person p){
        System.out.println(p.getName()); //小李
    }

    public void testParam2(Person p){
        p= new Person("王明");
        System.out.println(p.getName()); //王明
    }

}
