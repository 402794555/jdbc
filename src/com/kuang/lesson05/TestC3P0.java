package com.kuang.lesson05;

import com.kuang.lesson05.utils.JdbcUtils_C3P0;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//无论使用什么数据源，本质还是一样的，DataSource接口不会变，方法就不会变

//C3P0有日志输出
public class TestC3P0 {
    public static void main(String[] args){

        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;


        try {
            conn = JdbcUtils_C3P0.getConnection(); //C3P0 获取连接

            st=conn.createStatement();
            String sql="INSERT INTO users(id,`name`,`password`,`email`,`birthday`) " +
                    "VALUES(1,'xiao','123456','402794555@qq.com','2020-01-01')" +
                    ",(2,'xiao','123456','402794555@qq.com','2020-01-01')" +
                    ",(3,'xiao','123456','402794555@qq.com','2020-01-01')";
            int i = st.executeUpdate(sql);
            System.out.println("插入了："+i+"行");


        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            JdbcUtils_C3P0.release(conn,st,rs); //这里是手动释放了连接，其实连接池也会自己管理的
        }
    }
}
