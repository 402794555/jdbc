package com.kuang.lesson04;

import com.kuang.lesson02.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test_Transaction1 {
    public static void main(String[] args) {
        Connection conn=null;
        PreparedStatement pst=null;
        ResultSet rs=null;

        try {
            conn= JdbcUtils.getConnection();
            conn.setAutoCommit(false);//关闭数据库的自动提交，自动会开启事务

            String sql1="update users set name='jack' where id=5  ";
            pst=conn.prepareStatement(sql1);
            pst.executeUpdate();

            String sql2="update users set name='rose' where id=sa  ";
            pst=conn.prepareStatement(sql2);
            pst.executeUpdate();

            //业务完毕，提交事务

            conn.commit();
            System.out.println("成功！");

        } catch (SQLException e) {

            try {
                conn.rollback();  //如果失败则回滚事务,,,其实不写也会默认回滚的，因为事务没有自动提交
                System.out.println("失败！");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            e.printStackTrace();
        }finally {
            {
                JdbcUtils.release(conn,pst,rs);
            }
        }
    }
}
