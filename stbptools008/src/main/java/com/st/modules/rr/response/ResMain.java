package com.st.modules.rr.response;

import com.st.modules.alibaba.vos.Person;

public class ResMain {
    public static void main(String[] args) {

        Person person = new Person();
        Response<Person> success = Response.success(person);

        String s = Response.format(success);

        System.out.println(s);
        System.out.println(Response.check(success));


        Response<Person> fail = Response.fail("fails",person);

        String s2 = Response.format(fail);

        System.out.println(s2);
        System.out.println(Response.check(fail));

    }
}
