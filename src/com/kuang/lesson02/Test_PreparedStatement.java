package com.kuang.lesson02;

// 用PreparedStatement 可以象下面这样写代码：
// 关闭自动执行
//conn.setAutoCommit(false);
//PreparedStatement stmt = con.prepareStatement("INSERT INTO employees VALUES (?, ?)");
//st.setInt(1, 2000);
//st.setString(2, "Kelly");
//st.addBatch();
// 提交要执行的批处理
//int[] updateCounts = st.executeBatch();
//conn.commit();//执行完后，手动提交事务
//conn.setAutoCommit(true);

//PrepareStatement 也是接口     PrepareStatement extends Statement
//PrepareStatement 本身没有 int[] executeBatch() throws SQLException 方法
//而是继承了Statement的方法，且它们都是接口没有实际实现方法，但Statement接口对executeBatch()方法做了规范


/*PreparedStatement对象可以防止sql注入，而Statement不能防止sql注入（SQL被恶意拼接）
prepareStatement对象防止sql注入的方式是把用户非法输入的单引号用\反斜杠做了转义，从而达到了防止sql注入的目的
Statement对象就没那么好心了，它才不会把用户非法输入的单引号用\反斜杠做转义呢！
PreparedStatement可以有效防止sql注入，所以生产环境上一定要使用PreparedStatement，而不能使用Statement
当然啦，你可以仔细研究下PreparedStatement对象是如何防止sql注入的，我自己把最终执行的sql语句打印出来了，
看到打印出来的sql语句就明白了，原来是mysql数据库产商，在实现PreparedStatement接口的实现类中的
setString(int parameterIndex, String x)函数中做了一些处理，把单引号做了转义(只要用户输入的字符串中有单引号，
那mysql数据库产商的setString()这个函数，就会把单引号做转义)*/


//conn.createStatement()不需要参数, st.executeUpdate("sql")需要sql语句
//conn.prepareStatement(sql)需要一个sql参数，预编译sql ，然后pst.setType(第几个占位符，值)（给每个参数赋值）
//然后pst.executeUpdate()不需要sql语句就可以直接执行
//Statement 和 prepareStatement 都可以用 批处理，只是形式上有些区别，Statement没有预编译所有 要st.addBatch(sql)必须有sql语句
//而prepareStatement 可以在循环中pst.setType(第几占位符，值)（给每个参数赋值），然后pst.addBatch()不需要sql语句,


import com.kuang.lesson02.utils.JdbcUtils;


import java.sql.*;
import java.util.Arrays;

public class Test_PreparedStatement {
    public static void main(String[] args) throws SQLException {
        Connection conn=null;
        PreparedStatement pst=null;


        conn= JdbcUtils.getConnection();

        //PreparedStatement 的普通executeUpdate()
        //如果是select语句，则用pst.executeQuery()，注意查询语句不能用批处理pst.addBatch()
        String sql="INSERT INTO users(id,`name`,`password`,`email`,`birthday`)VALUES(?,?,'123456','402794555@qq.com','2020-01-01')";
        pst= conn.prepareStatement(sql); //预编译sql ，但先不执行


        for (int i = 1000; i < 1050; i++) {
            pst.setInt(1,i);   //手动给参数赋值
            pst.setString(2,"Test_PreparedStatement");
            int n= pst.executeUpdate();  //每条语句提交一次
            System.out.print("这条语句影响了"+n+"行   ");
        }
        System.out.println();


        //PreparedStatement 使用批处理executeBatch()，没有使用事务
        String sql2="delete from users where id=? and password=?";
        pst=conn.prepareStatement(sql2);
        for (int i = 1000; i < 1050; i++) {
            pst.setInt(1,i);
            pst.setString(2,"123456");  //其实可以就用id来删除，这里为了展示才这样做
            pst.addBatch();//添加到同一个批处理中
        }
        int[] ns = pst.executeBatch();//执行批处理
        System.out.println("每条语句影响了"+ Arrays.toString(ns));  //1,1,1,1,1,1...共50条有语句，每条语句删除一行数据

        //没有使用事务，没有使用事务的话可能会出现的问题是：批处理50条语句，可是第11条语句处出现问题
        //但前面10条都已经自动提交完成执行了，若关闭了自动提交，这里的批处理相当于使用了事务，可以把这50条看作一个整体
        //executeBatch()后使用conn.commit()提交，出现问题可以选择回滚


        //PreparedStatement 使用批处理executeBatch()，使用事务
        conn.setAutoCommit(false);//关闭自动提交

        String sql3="update users set email=? where id=?";
        pst=conn.prepareStatement(sql3);
        for (int i = 1; i < 5; i++) {
            pst.setString(1,"email");
            pst.setInt(2,i);  //其实可以就用id来删除，这里为了展示才这样做
            pst.addBatch();//添加到同一个批处理中
        }
        int[] ns2 = pst.executeBatch();//执行批处理
        System.out.println("每条语句影响了"+ Arrays.toString(ns2));  //1,1,1,1,1,1...共50条有语句，每条语句删除一行数据
        conn.commit();  //执行完后，手动提交事务,注意这里一定要手动提交，不然虽然pst.executeBatch()后更新了数据，知道影响了多少行
                        //但是，如果不手动commit就不会持久化到硬盘上，当然如果不想commit也可以选择rollback
        conn.setAutoCommit(true);//再把自动提交打开,注意如果上面没有commit也没有rollback，这里开启自动提交后,会把批处理结果自动提交了


        //注意可以对pst.executeBatch()出现的异常进行了BatchUpdateException e捕获，
        // 如果像上面一样不捕获的话，一条有问题，此次批处理全部不执行，更不会commit，会直接中断程序并控制台打印异常
        // 如果捕获异常的话,正确的语句会被执行更新，甚至可以commit让它持久化,
        // 还可以通过 int[] result = e.getUpdateCounts(); 看看执行更新的情况
        // 会将所有的语句全部执行完，然后数组中成功的返回影响条数，失败的返回负数，比如-3。

        pst.close();
        conn.close();


    }
}
