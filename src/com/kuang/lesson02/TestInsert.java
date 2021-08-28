package com.kuang.lesson02;

import com.kuang.lesson02.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestInsert {
    public static void main(String[] args){
        Connection conn=null;//由于要释放这个对象所以把它提到前面来，扩大作用域
        Statement st=null;
        ResultSet rs=null;

       //getConnection()  createStatement()  executeUpdate(sql)
       //这里可以选择try/catch处理异常，也可以抛出异常
        try {
            conn = JdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="INSERT INTO users(id,`name`,`password`,`email`,`birthday`) " +
                    "VALUES(4,'xiao','123456','402794555@qq.com','2020-01-01')" +
                    ",(55,'xiao','123456','402794555@qq.com','2020-01-01')" +
                    ",(66,'xiao','123456','402794555@qq.com','2020-01-01')";
            int i = st.executeUpdate(sql);
            System.out.println("插入了："+i+"行");

            //st.executeUpdate()一次只能执行一个SQL语句，必须用分号分隔的语句是两句SQL语句
            //可以INSERT INTO users(...) VALUES(...),(...),(...)  虽然插入了多行数据，但它实际上只是一条SQL语句
            //不可以INSERT INTO users(...) VALUES(...);INSERT INTO users(...) VALUES(...) 因为这是两条SQL语句了

            //st.execute() 虽然可以执行查询，增加删除更新，但是它也只能执行一条语句

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            JdbcUtils.release(conn,st,rs);
        }
    }
}
