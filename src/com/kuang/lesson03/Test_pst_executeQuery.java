package com.kuang.lesson03;

import com.kuang.lesson02.utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test_pst_executeQuery {
    public static void main(String[] args) throws SQLException {
        Connection conn=null;
        PreparedStatement pst=null;
        ResultSet rs=null;

        conn= JdbcUtils.getConnection();
        String sql="select * from users where id=?";
        pst= conn.prepareStatement(sql); //预编译sql ，但先不执行


        pst.setInt(1,1);
        rs=pst.executeQuery();

        while (rs.next()){
            System.out.println("id="+rs.getObject("id"));
            System.out.println("name="+rs.getObject("name"));
            System.out.println("pwd="+rs.getObject("password"));
            System.out.println("email="+rs.getObject("email"));
            System.out.println("birth="+rs.getObject("birthday"));
            System.out.println("===============================");
        }

    }
}
