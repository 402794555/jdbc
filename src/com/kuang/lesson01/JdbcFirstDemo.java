package com.kuang.lesson01;

import com.mysql.cj.jdbc.Driver;

import java.sql.*;

//我的第一个JDBC程序
public class JdbcFirstDemo {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        //1.加载驱动
        Class.forName("com.mysql.cj.jdbc.Driver"); //固定写法，加载驱动
                    //DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver()); 则相当于注册了两次

        //jdbc4.0 以后是不用显式的去加载驱动，如果驱动包符合 SPI 模式就会自动加载
        //就是说程序会自动去项目中查找是否有驱动，当然没有驱动的话自然是连接不了的
        //那么为什么JDBC4.0之后，就不需要显性去加载这个驱动了，难道rt.jar 里的DriverManager可以自主注册驱动包么，
        //当然这是不可能的，为了解决这个问题，jdk采用了上下文加载 在你调用 DriverManager.getConnection方法时，
        //会加载DriverManager类并执行他的静态方法，（会一直调用其它方法完成加载驱动完成初始化）


        //2.用户信息和url
        //serverTimezone=UTC   &   useUnicode=true   &   characterEncoding=utf8   &   useSSL=true
        //还可以加一个rewriteBatchedStatements=true来加快批处理速度

        //jdbc:mysql://主机地址:端口号/数据库名:参数1&参数2&参数3
        String url="jdbc:mysql://localhost:3306/jdbcstudy?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=true";
        String username="root";
        String password="123456";

        //3.连接数据库成功，数据库对象
        Connection connection=DriverManager.getConnection(url, username, password);
        //getConnection()-->重载getConnection()-->ensureDriversInitialized()

        //connection代表数据库,数据库设置自动提交、事物提交、事物回滚
        //connection.setAutoCommit(true);
        //connection.commit();
        //connection.rollback();
        
        //4.执行SQL的对象       statement 执行sql的对象
        Statement statement = connection.createStatement();
        //jdbc中的statement对象用于向数据库发送SQL语句，想要完成对数据库的增删改查，
        //只需要通过这个对象向数据库发送增删改查语句即可。

        // statement.executeQuery(String sql);//查询操作返回ResultSet
        // statement.execute(String sql);//执行任何SQL （需要判断，故效率低一点）
        // statement.executeUpdate(String sql)  //更新、插入、删除。都是用这个，返回一个受影响的行数，返回一个int

        //connection.setAutoCommit(false);//当进行批处理更新时，通常应该关闭自动执行
        //statement.addBatch(String sql);
        //statement.addBatch(String sql);
        //statement.addBatch(String sql);
        //statement.executeBatch();   //批处理，返回一个int[],返回一个受影响的行数构成的数组
        // conn.commit();//执行完后，手动提交事务
        //connection.setAutoCommit(true);

        //statement.addBatch() 方法为调用语句的命令列表添加一个元素。如果批处理中包含有试图返回结果集的命令，
        //则当调用 statement.executeBatch() 时，将抛出 SQLException。
        // 只有 DDL 和 DML 命令（它们只返回简单的更新计数）才能作为批处理的一部分来执行, 不能有DQL命令
        // 如果应用程序决定不提交已经为某语句构造的命令批处理，则可以调用方法 Statement.clearBatch()来重新设置批处理。


        //5.执行SQL的对象 去 执行SQL，可能存在结果，查看返回结果
        String sql="SELECT * FROM users";

        ResultSet resultSet = statement.executeQuery(sql); //返回的结果集对象,结果集中封装了我们全部查询出来的结果（只有DQL才用这个）
        //集合中放的是哈希表

        //resultSet.getObject("列名");//在不知道列类型的情况下使用
        //resultSet.getString("列名");//如果知道列的类型就使用指定的类型
        //resultSet.getInt("列名");
        //resultSet.getFloat("列名");
        //resultSet.getDate("列名");

        while (resultSet.next()){
            System.out.println("id="+resultSet.getObject("id"));
            System.out.println("name="+resultSet.getObject("name"));
            System.out.println("pwd="+resultSet.getObject("password"));
            System.out.println("email="+resultSet.getObject("email"));
            System.out.println("birth="+resultSet.getObject("birthday"));
            System.out.println("===============================");
        }
        //没有数据<->有数据<->有数据<->有数据<->没有数据
        resultSet.beforeFirst(); //指向指向头结点（没有数据，在第一个有数据节点的前面）
        resultSet.next();
        System.out.println("id="+resultSet.getObject("id")); //输出id=1
        System.out.println("id="+resultSet.getObject("id")); //输出id=1

        resultSet.afterLast();  //指向最后的一个有数据的节点的后面
        resultSet.previous();
        System.out.println("id="+resultSet.getObject("id")); //输出id=3
        System.out.println("id="+resultSet.getObject("id")); //输出id=3

        resultSet.absolute(2); //移动到指定行，从1开始
        System.out.println("id="+resultSet.getObject("id"));//输出id=2


        //释放连接（释放对象）
        resultSet.close();
        statement.close();
        connection.close();
    }

}
