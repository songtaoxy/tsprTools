package com.st.practice.innerclass;

public class OuterClass {
	private String out_nostatic_sex = "out_nostatic_sex";
	public static String out_static_name = "out_static_name";

	/**
	 * 静态内部类
	 */
	static class Static_InnerClass {
		/* 在静态内部类中可以存在静态成员 */
		String Static_InnerClass_nostatic_name = "Static_InnerClass_nostatic_name";
		public static String Static_InnerClass_static_name = "Static_InnerClass_static_name";

		/**
		 * 静态内部类-非静态方法: 访问外部
		 */
		public void static_innerclass_nostatic_display() {


            /* 访问外部: 静态成员 */
			System.out.println("OutClass name :" + out_static_name);
            out_static_display();


            /* 访问外部: 非静态成员 */
			OuterClass outerClass = new OuterClass();
            outerClass.out_nostatic_display();
			//System.out.println("OutClass name :" +out_nostatic_sex); //error
			String sex1 = new OuterClass().out_nostatic_sex;

			new OuterClass().out_nostatic_display();

		}



		/**
		 * 静态内部类-静态方法: 访问外部
		 */
		public static void static_innerclass_static_display() {
            /* 访问外部: 静态成员 */
            System.out.println("OutClass name :" + out_static_name);
            out_static_display();


            /* 访问外部: 非静态成员 */
            OuterClass outerClass = new OuterClass();
            outerClass.out_nostatic_display();
            //System.out.println("OutClass name :" +out_nostatic_sex); //error
            String sex1 = new OuterClass().out_nostatic_sex;

            new OuterClass().out_nostatic_display();
		}
	}

	/**
	 * 非静态内部类-非静态方法: 访问外部
	 */
	class Nostatic_InnerClass {


		/* 非静态内部类中不能存在静态成员 */
		public String nostatic_InnerClass_Nostatic_Name = "nostatic_InnerClass_Nostatic_Name";
		private String nostatic_InnerClass_Nostatic_Name_private = "nostatic_InnerClass_Nostatic_Name_private";
		//private static  String _name4 = "private"; error


		/* 非静态内部类-非静态方法: 可以调用外部类的任何成员,不管是静态的还是非静态的 */
		public void nostatic_innerclass_nostatic_display() {

            /** 非静态调的 **/
            System.out.println("OuterClass name：" + out_nostatic_sex);
            out_nostatic_display();

            /** 静态调的 **/
            System.out.println("OuterClass name：" + out_static_name);
			out_static_display();


			/** 非静态内部类, 返回外部类有两种方式: **/
			/** 静态内部类,不管是非静态方法还是静态方法,  返回外部类只能用方式2: **/
			/* 其一 */
			OuterClass outerClass = OuterClass.this;
			/* 其二 */
			OuterClass outerClass1 = new OuterClass();
		}
	}

	/**
	 * @return void
	 * @desc 外部类方法
	 */
	public void out_nostatic_display() {

		/* 外部类非静态方法: 访问静态内部类静态成员. */
		System.out.println(Static_InnerClass.Static_InnerClass_static_name);


        /* 外部类非静态方法: 访问静态内部类非静态成员. */
        /* 静态内部类 可以直接创建实例不需要依赖于外围类 */
		//Static_InnerClass.Static_InnerClass_nostatic_name // error
		new Static_InnerClass().static_innerclass_nostatic_display();


        /* 外部类非静态方法: 访问非静态内部类非静态成员: 方式1. */
		/* 非静态内部的创建需要依赖于外围类 */
		Nostatic_InnerClass nostatic_innerClass = new Nostatic_InnerClass();
		String name2 = nostatic_innerClass.nostatic_InnerClass_Nostatic_Name;
		System.out.println(name2);


        /* 外部类非静态方法: 访问非静态内部类非静态成员: 方式2. */
        /* 非静态内部的创建需要依赖于外围类 */
		Nostatic_InnerClass inner2 = new OuterClass().new Nostatic_InnerClass();
		System.out.println(inner2.nostatic_InnerClass_Nostatic_Name); // 非私有的
		System.out.println(inner2.nostatic_InnerClass_Nostatic_Name_private);// 私有的
		inner2.nostatic_innerclass_nostatic_display();
	}


	public static void out_static_display() {

        /* 外部类静态方法: 访问静态内部类静态成员. */
        System.out.println(Static_InnerClass.Static_InnerClass_static_name);


        /* 外部类静态方法: 访问静态内部类非静态成员. */
        /* 静态内部类 可以直接创建实例不需要依赖于外围类 */
        //Static_InnerClass.Static_InnerClass_nostatic_name // error
        new Static_InnerClass().static_innerclass_nostatic_display();



        /* 外部类静态方法: 访问非静态内部类非静态成员: 方式1-不行. */
        /* 非静态内部的创建需要依赖于外围类 */
        //Nostatic_InnerClass nostatic_innerClass = new Nostatic_InnerClass(); // error
        //String name2 = nostatic_innerClass._name2;
        //System.out.println(name2);


        /* 外部类静态方法: 访问非静态内部类非静态成员: 方式2. */
        /* 非静态内部的创建需要依赖于外围类 */
        Nostatic_InnerClass inner2 = new OuterClass().new Nostatic_InnerClass();
        System.out.println(inner2.nostatic_InnerClass_Nostatic_Name); // 非私有的
        System.out.println(inner2.nostatic_InnerClass_Nostatic_Name_private);// 私有的
        inner2.nostatic_innerclass_nostatic_display();
	}

    /**
     *  用法, see: OuterClass.out_static_display
     * @param args
     */
	public static void main(String[] args) {
		OuterClass outer = new OuterClass();
		outer.out_nostatic_display();

		String name1 = Static_InnerClass.Static_InnerClass_static_name;
		Nostatic_InnerClass innerClass2 = new OuterClass().new Nostatic_InnerClass();


	}
}



class OutClass2 {

    /**
     * 变量类型:
     *
     * 标准格式:静态内部类和非静态内部类, 都是一样的: 外部类的名字.内部类的名字
     * 非静态内部类的变量
     * OuterClass.Nostatic_InnerClass nostatic_innerClass = new OuterClass().new Nostatic_InnerClass();
     *
     * 静态内部类的变量
     * OuterClass.Static_InnerClass static_innerClass = new OuterClass.Static_InnerClass();
     * OuterClass.Static_InnerClass.Static_InnerClass_static_name;
     *
     * 省略场景:
     * OuterClass.Nostatic_InnerClass nostatic_innerClass = new OuterClass().new Nostatic_InnerClass();
     * 如果都在外部类中, 外部类引用内部类, 则变量类型前面的OuterClass可以省略. 即:Static_InnerClass static_innerClass
     * 如果不在外部类中, 其他类想引用内部类, 则变量类型前面的OuterClass不能省略.即, 标准格式.
     *
     */
    public void test() {

        OuterClass.Static_InnerClass static_innerClass = new OuterClass.Static_InnerClass();
        String static_innerClass_nostatic_name = static_innerClass.Static_InnerClass_nostatic_name;

        String static_innerClass_static_name = OuterClass.Static_InnerClass.Static_InnerClass_static_name;


        // error
        //OuterClass.Nostatic_InnerClass nostatic_innerClass = new OuterClass.Nostatic_InnerClass();
        OuterClass.Nostatic_InnerClass nostatic_innerClass = new OuterClass().new Nostatic_InnerClass();

    }

    public static void static_test() {

        OuterClass.Static_InnerClass static_innerClass = new OuterClass.Static_InnerClass();
        String static_innerClass_nostatic_name = static_innerClass.Static_InnerClass_nostatic_name;

        String static_innerClass_static_name = OuterClass.Static_InnerClass.Static_InnerClass_static_name;



        // error
        //OuterClass.Nostatic_InnerClass nostatic_innerClass = new OuterClass.Nostatic_InnerClass();
        OuterClass.Nostatic_InnerClass nostatic_innerClass = new OuterClass().new Nostatic_InnerClass();

    }

	private class Pe {
	}

}
