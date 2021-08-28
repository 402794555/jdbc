package com.kuang.lesson02;
/* 如果使用了 addBatch() -> executeBatch() 还是很慢，那就得使用到这个参数了rewriteBatchedStatements=true (启动批处理操作)
在数据库连接URL后面加上这个参数：String url =  ”jdbc:mysql://localhost:3306/User? rewriteBatchedStatements=true“;
*/

/*MySQL的JDBC连接的url中要加rewriteBatchedStatements=true参数，并保证5.1.13以上版本的驱动，才能实现高性能的批量插入。
MySQL JDBC驱动在默认情况下会无视executeBatch()语句，把我们期望批量执行的一组sql语句拆散，
一条一条地发给MySQL数据库，批量插入实际上是单条插入，直接造成较低的性能。
只有把rewriteBatchedStatements参数置为true, 驱动才会帮你批量执行SQL ,另外这个选项对INSERT/UPDATE/DELETE都有效*/

//插入10万条数据
//不关闭自动提交（不开启事务）、不加参数（不用批处理）  用时：131.55秒   （多次更新多次commit）
//不关闭自动提交（不开启事务）、加参数（开启批处理）    用时：0.828秒    （一次更新一次commit）
//关闭自动提交（开启事务）、不加参数（不用批处理）     用时：4.831秒     （多次更新一次commit）
//关闭自动提交（开启事务）、加参数加参数（开启批处理）  用时：0.781秒     （一次更新一次commit）

//开启了批处理，就相当于也开启了事务，因为就更新一次commit一次

/*一共有4种方式分别是 ：
不用事务，不用批处理；（这是最最慢的，它一条一条sql语句发给数据库更新并commit，根本就没有起到批量的效果,多次更新多次commit
不用事务，只用批处理；（开启了批处理，一起性把所有sql语句发给数据库，就一次更新一次commit，所以相当于把开启事务的作用也使用到了，所以时间也是最快的）
只用事务，不用批处理；（不开启批处理的话 java会把sql语句拆散，一条一条地发给MySQL数据库更新，但是由于开启了事务所有不会每条都commit，多次更新一次commit
既用事务，也用批处理；（这个和第二个一样是都是最快的，就一次更新一次commit，，但是这个更容易理解一点，所以建议在处理大批量的数据时，同时使用批处理和事务）*/


//草了，一直测试两个都没多少却别，原来是rewriteBatchedStatements=true参数，少写了结尾的s

import java.sql.*;
import java.sql.Date;
import java.util.*;

//关于st.executeBatch()出现的异常进行了BatchUpdateException e捕获 问题
//Statement和PreparedStatement 不开启批处理（事务不开或开）  即 多次更新多次commit  或者 多次更新一次commit
//就算多条语句中有错误的语句，正确的也能执行，且正确返回数组[1,1...-3,-3..1,1]，因为是多次更新所有能够得到返回数据

//Statement和PreparedStatement 开启批处理（相当于同时开启了事务 ）即一次更新一次commit
//对于 Statement 如果有错的语句，正确只能执行到第一个错误位置，但是返回错误的数组[-1,-3,-3,-3....-3]（[1,1..-3,-3]）
//对于 PreparedStatement 如果有错的语句，则所有语句不执行，并且返回[-3,-3,-3...-3,-3,-3]数组（符合实际的效果）

//如果不捕获异常的话
//在不开启批处理，不开启事务时，即多次更新多次commit ，不捕获异常，会在控制台打印错误，但是所有正确的语句都能得到执行（st与pst一样）
//在开启批处理，不开启事务时，即一次更新一次commit ，不捕获异常，会在控制台打印错误，但是所有正确的语句都能得到执行（pst全部不执行）
//在不开启批处理，开启事务时，即多次更新一次commit，不捕获异常，会在控制台打印错误，并且所有语句无论错误正确都得不到执行（st与pst一样）
//开启批处理，开启事务时，即一次更新一次commit，不捕获异常，会在控制台打印错误，并且所有语句无论错误正确都得不到执行（st与pst一样）
//综上 Statement 不捕获异常的话，只要不开启事务还是能够执行的，而PreparedStatement只要不开启事务且不开启批处理 才能得到执行

public class Test_rewriteBatchedStatements {
    public static void main(String[] args) throws SQLException {


        /*String url="jdbc:mysql://localhost:3306/jdbcstudy?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=true";
        String username="root";
        String password="123456";
        Connection conn = DriverManager.getConnection(url, username, password);


        conn.setAutoCommit(false);//关闭自动提交
        String sql="insert into users(id,`name`,`password`,`email`,`birthday`)"+
                "values(?,?,?,?,?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        long start=System.currentTimeMillis();
        for (int i = 10; i < 20; i++) {
            pst.setInt(1,i);
            pst.setString(2,"i am name");
            pst.setString(3,"i am password");
            pst.setString(4,"i am email");

            //java.sql.Timestamp  的父类是 java.sql.Date 的父类是 java.util.Date
            //java.util.Data  是Java用的   new java.util.Date().getTime() 构造方法,调用getTime()函数获得时间戳
            //java.sql.Date   是数据库用的  new java.sql.Date() 构造方法传入时间戳，转化为SQL的日期部分，时间为00:00:00
            //java.sql.Timestamp
            //pst2.setDate(5, new java.sql.Date(new java.util.Date().getTime()))  //只要时间戳的日期部分，时间为00:00:00

            pst2.setTimestamp(5,new java.sql.Timestamp(new java.util.Date().getTime()));

            pst.addBatch();
        }


        int[] ints = pst.executeBatch();
        System.out.println(Arrays.toString(ints));
        conn.commit();
        conn.setAutoCommit(true);
        pst.close();
        conn.close();
        System.out.println("不加参数用时"+(System.currentTimeMillis() - start)/1000.00+"秒");
*/
//==============================================================================================

        String url2="jdbc:mysql://localhost:3306/jdbcstudy?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=true";
        String username2="root";
        String password2="123456";
        Connection conn2 = DriverManager.getConnection(url2, username2, password2);


       // conn2.setAutoCommit(false);//关闭自动提交
        String sql2="insert into users(id,`name`,`password`,`email`,`birthday`)"+
                "values(?,?,?,?,?)";
        PreparedStatement pst2 = conn2.prepareStatement(sql2);
        long start2=System.currentTimeMillis();
        for (int i = 10; i < 20; i++) {
            pst2.setInt(1,i);
            pst2.setString(2,"i am name");
            pst2.setString(3,"i am password");
            pst2.setString(4,"i am email");

            //java.sql.Timestamp  的父类是 java.sql.Date 的父类是 java.util.Date
            //java.util.Data  是Java用的   new java.util.Date().getTime() 构造方法,调用getTime()函数获得时间戳
            //java.sql.Date   是数据库用的  new java.sql.Date() 构造方法传入时间戳，转化为SQL的日期部分，时间为00:00:00
            //java.sql.Timestamp
            //pst2.setDate(5, new java.sql.Date(new java.util.Date().getTime()))  //只要时间戳的日期部分，时间为00:00:00

            pst2.setTimestamp(5,new java.sql.Timestamp(new java.util.Date().getTime()));

            pst2.addBatch();
        }
        for (int i = 1; i < 30; i++) {
            pst2.setInt(1,i);
            pst2.setString(2,"i am name");
            pst2.setString(3,"i am password");
            pst2.setString(4,"i am email");

            //java.sql.Timestamp  的父类是 java.sql.Date 的父类是 java.util.Date
            //java.util.Data  是Java用的   new java.util.Date().getTime() 构造方法,调用getTime()函数获得时间戳
            //java.sql.Date   是数据库用的  new java.sql.Date() 构造方法传入时间戳，转化为SQL的日期部分，时间为00:00:00
            //java.sql.Timestamp
            //pst2.setDate(5, new java.sql.Date(new java.util.Date().getTime()))  //只要时间戳的日期部分，时间为00:00:00

            pst2.setTimestamp(5,new java.sql.Timestamp(new java.util.Date().getTime()));

            pst2.addBatch();
        }

        try {
            pst2.executeBatch();
        }catch (BatchUpdateException e){
            int[] result = e.getUpdateCounts();
            System.out.println("看看执行情况"+ Arrays.toString(result));
        }
       // conn2.commit();
      // conn2.setAutoCommit(true);
        pst2.close();
        conn2.close();
        System.out.println("加参数用时"+(System.currentTimeMillis() - start2)/1000.00+"秒");

//=========================================================================================================

     /*  String url3="jdbc:mysql://localhost:3306/jdbcstudy?rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=true";
        String username3="root";
        String password3="123456";
        Connection conn3 = DriverManager.getConnection(url3, username3, password3);
        Statement st3 = conn3.createStatement();
       conn3.setAutoCommit(false);//关闭自动提交

        long start3=System.currentTimeMillis();
        for (int i = 10; i < 20; i++) {
            st3.addBatch("INSERT INTO users(id,`name`,`password`,`email`,`birthday`)VALUES("+i+",'Test_Batch','123456','402794555@qq.com','2020-01-01')");
        }
        for (int i = 1; i < 30; i++) {
            st3.addBatch("INSERT INTO users(id,`name`,`password`,`email`,`birthday`)VALUES("+i+",'Test_Batch','123456','402794555@qq.com','2020-01-01')");
        }

        try {
            st3.executeBatch();
        }catch (BatchUpdateException e) {
            int[] result = e.getUpdateCounts();
            System.out.println("看看执行情况"+Arrays.toString(result));
        }

        conn3.commit();//执行完后，手动提交事务,注意这里一定要手动提交，不然虽然pst.executeBatch()后更新了数据，知道影响了多少行
                           //但是，如果不手动commit就不会持久化到硬盘上，当然如果不想commit也可以选择rollback
        conn3.setAutoCommit(true);//再把自动提交打开,注意如果上面没有commit也没有rollback，这里开启自动提交后,会把批处理结果自动提交了
        System.out.println("加参数用时"+(System.currentTimeMillis() - start3)/1000.00+"秒");
*/

    }
}
  /**/