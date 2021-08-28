package com.kuang.lesson05.utils;



import com.mchange.v2.c3p0.ComboPooledDataSource;



import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//无论使用什么数据源，本质还是一样的，DataSource接口不会变，方法就不会变
public class JdbcUtils_C3P0 {
    private static ComboPooledDataSource dataSource=null;

    static {
        try{

            /*  //代码版配置
            dataSource=new ComboPooledDataSource();
            dataSource.setDriverClass();
            dataSource. setUser();
            dataSource. setPassword();
            dataSource. setJdbcUrl();
            dataSource. setMaxPoolSize();
            dataSource. setMinPoolSize( );*/



            //创建数据源     //ComboPooledDataSource 组合池数据源
            dataSource = new ComboPooledDataSource("MySQL"); //配置文件写法


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取连接
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection(); //从数据源中获取连接
    }

    //释放资源,//这里是手动释放了连接，其实连接池也会自己管理的
    public static void release(Connection conn, Statement st, ResultSet rs){
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(st!=null){
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(rs!=null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
