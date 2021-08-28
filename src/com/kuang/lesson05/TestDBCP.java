package com.kuang.lesson05;


import com.kuang.lesson05.utils.JdbcUtils_DBCP;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//无论使用什么数据源，本质还是一样的，DataSource接口不会变，方法就不会变

//DBCP(Database Connection Pool)数据库连接池
public class TestDBCP {
    public static void main(String[] args){

        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;


        try {
            conn = JdbcUtils_DBCP.getConnection(); //DBCP(Database Connection Pool)获取连接

            st=conn.createStatement();
            String sql="INSERT INTO users(id,`name`,`password`,`email`,`birthday`) " +
                    "VALUES(4,'xiao','123456','402794555@qq.com','2020-01-01')" +
                    ",(55,'xiao','123456','402794555@qq.com','2020-01-01')" +
                    ",(66,'xiao','123456','402794555@qq.com','2020-01-01')";
            int i = st.executeUpdate(sql);
            System.out.println("插入了："+i+"行");


        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            JdbcUtils_DBCP.release(conn,st,rs); //这里是手动释放了连接，其实连接池也会自己管理的
        }
    }
}
