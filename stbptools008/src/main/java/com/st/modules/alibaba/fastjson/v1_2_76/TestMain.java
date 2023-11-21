package com.st.modules.alibaba.fastjson.v1_2_76;

import com.st.modules.alibaba.vos.Person;
import com.st.modules.enums.common.EnumItem;
import com.st.modules.enums.common.SexEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestMain {

    List<Person> personList;
    //    EnumItem<String> enumItem;
    SexEnum sexEnum;

    Person person;

    public static void main(String[] args) {
        TestMain testMain = new TestMain();

        Person person1 = new Person();
        person1.setName("we");

        Person person2 = new Person();
        person2.setName("we2");
        ArrayList<Person> people = new ArrayList<>();
        people.add(person1);
        people.add(person2);

        testMain.setPersonList(people);
//        testMain.setEnumItem(SexEnum.FEMALE);
        testMain.setSexEnum(SexEnum.FEMALE);

        Person person3 = new Person();
        person3.setName("we3");
        testMain.setPerson(person3);

        String s = FastJsonUtil.o2jStr(testMain);

        System.out.println(s);
        System.out.println("===============");

        TestMain testMain1 = FastJsonUtil.jStr2o(s, TestMain.class);
        System.out.println(
                testMain1
        );


    }

}
