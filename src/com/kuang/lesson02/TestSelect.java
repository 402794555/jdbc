package com.kuang.lesson02;

import com.kuang.lesson02.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestSelect {
    public static void main(String[] args){
        Connection conn=null;//由于要释放这个对象所以把它提到前面来，扩大作用域
        Statement st=null;
        ResultSet rs=null;

        //getConnection()  createStatement()  executeUpdate(sql)
        //这里可以选择try/catch处理异常，也可以抛出异常
        try {
            conn = JdbcUtils.getConnection();
            st=conn.createStatement();
            String sql="select * from users";
            rs = st.executeQuery(sql);

            //没有数据<->有数据<->有数据<->有数据<->没有数据
            while(rs.next()){
                rs.getInt("id");
                System.out.println(rs.getString("name"));
                System.out.println(rs.getString("password"));
                System.out.println(rs.getString("email"));
                System.out.println(rs.getDate("birthday"));
                System.out.println("===============================");

            }

            //executeQuery(sql) 只能执行一句SQL语句

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            JdbcUtils.release(conn,st,rs);
        }
    }
}
