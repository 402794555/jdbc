package com.kuang.lesson03;
/*
关于继承：
        我们需要注意Java中不支持多继承，即一个类只可以有一个父类存在。另外Java中的构造函数是不可以继承的，
        如果构造函数被private修饰，那么就是不明确的构造函数，该类是不可以被其它类继承的，具体原因我们可以先来看下Java中类的初始化顺序：

        初始化父类中的静态成员变量和静态代码块
        初始化子类中的静态成员变量和静态代码块
        初始化父类中的普通成员变量和代码块，再执行父类的构造方法
        初始化子类中的普通成员变量和代码块，再执行子类的构造方法

        如果父类构造函数是私有（private）的，则初始化子类的时候不可以被执行，所以解释了为什么该类不可以被继承，
        也就是说其不允许有子类存在。我们知道，子类是由其父类派生产生的，那么子类有哪些特点呢？

        子类拥有父类非private的属性和方法
        子类可以添加自己的方法和属性，即对父类进行扩展
        子类可以重新定义父类的方法，即方法的覆盖/重写
*/

class Test_super {
    public static void main(String[] args) {
        Son son=new Son();
       Son son2=new Son(2);
    }
}

class Father{

    static {
        System.out.println("我是父类静态代码块");
    }

     private Father() {
         System.out.println("我是父类无参构造");
     }
     Father(String s) {
        System.out.println("我是父类有参构造"+s);
    }
}

class Son extends Father{

    private int i;
    static {
        System.out.println("我是子类静态代码块");
    }
    public Son() {
       super("22");
        System.out.println("我是子类无参构造");

    }
    public Son(int i) {
        super("22");
        //super("aaa");
        System.out.println("我是子类有参构造"+i);
    }
}