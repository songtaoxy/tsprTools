package com.st.practice.innerclass;

public class OuterClass {
  private String sex;
  public static String name = "chenssy";

  /**
   *静态内部类
   */
  static class InnerClass1{
    /* 在静态内部类中可以存在静态成员 */
    public static String _name1 = "chenssy_static";

    public void display(){
      /*
       * 静态内部类只能访问外部类的静态成员变量和方法
       * 不能访问外围类的非静态成员变量和方法
       */
      System.out.println("OutClass name :" + name);
      //System.out.println("OutClass name :" +sex); //error
    }
  }

  /**
   * 非静态内部类
   */
  class InnerClass2{
    /* 非静态内部类中不能存在静态成员 */
    public String _name2 = "chenssy_inner";

    private String _name3 = "private";
    //private static  String _name4 = "private"; error

    /* 非静态内部类中可以调用外部类的任何成员,不管是静态的还是非静态的 */
    public void display(){
      System.out.println("OuterClass name：" + name);
      System.out.println("OuterClass name：" + sex);
    }
  }

  /**
   * @desc 外部类方法
   * @return void
   */
  public void display(){

    /* 外部类访问静态内部类：内部类. */
    System.out.println(InnerClass1._name1);
    /* 静态内部类 可以直接创建实例不需要依赖于外围类 */
    new InnerClass1().display();


    /* 非静态内部的创建需要依赖于外围类 */
    OuterClass.InnerClass2 inner2 = new OuterClass().new InnerClass2();
    /* 访问非静态内部类的成员需要使用非静态内部类的实例 */
    System.out.println(inner2._name2); // 非私有的
    System.out.println(inner2._name3);// 私有的
    inner2.display();
  }

  public static void main(String[] args) {
    OuterClass outer = new OuterClass();
    outer.display();
  }
}