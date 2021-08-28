package com.kuang.lesson03;

import java.sql.Timestamp;
import java.util.Date;
//java.sql.Timestamp  的父类是 java.sql.Date 的父类是 java.util.Date
public class Test_util_Date {
    public static void main(String[] args) {
        Date date1 = new java.util.Date();

        System.out.println(date1); //Thu Aug 19 21:39:57 CST 2021
        System.out.println(date1.getTime());//1629380397118

        Date date2=new java.sql.Date(date1.getTime());
        System.out.println(date2);//2021-08-19

        Timestamp timestamp=new java.sql.Timestamp(date1.getTime());
        System.out.println(timestamp);//2021-08-19 21:39:57.118
    }

}
