package com.st.api.practice.gson;



public class ReflectTest {
    public void getClasses() throws ClassNotFoundException {

        Class<?> johnClass = Class.forName("com.st.proxy.John");
        //Class<Tom> tomClass = Tom.class;

        Class<Class> aClass = Class.class;


        Class<String> stringClass = String.class;
        Class<? extends Class> aClass1 = stringClass.getClass();


        Class<?> aClass2 = String.class.getClassLoader().loadClass("java.lang.String");

    }
}