package com.st.modules.rr.response;

import com.st.modules.alibaba.vos.Person;

public class ResMain {
    public static void main(String[] args) {

        Person person = new Person();
        Response<Person> success = Response.success(person);

        String s = Response.formatResponse(success);

        System.out.println(s);
    }
}
