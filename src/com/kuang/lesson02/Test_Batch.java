package com.kuang.lesson02;
//JDBC使用MySQL处理大数据的时候,自然而然的想到要使用批处理，
//普通的执行过程是：每处理一条数据，就访问一次数据库；
//而批处理是：累积到一定数量，再一次性提交到数据库，减少了与数据库的交互次数，所以效率会大大提高
//至于事务：事务指逻辑上的一组操作，组成这组操作的各个单元，要不全部成功，要不全部不成功，默认是关闭事务的。

//没有使用事务的话可能会出现的问题是：批处理50条语句，可是第11条语句处出现问题
//但前面10条都已经自动提交完成执行了，若关闭了自动提交，这里的批处理相当于使用了事务，可以把这50条看作一个整体
//executeBatch()后使用conn.commit()提交，出现问题可以选择回滚

//JDBC提供了数据库batch处理的能力，在数据大批量操作（新增、删除等）的情况下可以大幅度提升系统的性能。
// 我以前接触的一个项目，在没有采用batch处理时，删除5万条数据大概要半个小时左右，
// 后来对系统进行改造，采用了batch处理的方式，删除5万条数据基本上不会超过1分钟。


//connection.setAutoCommit(false);//当进行批处理更新时，通常应该关闭自动执行
//Statement statement = connection.createStatement()
//statement.addBatch(String sql);
//statement.addBatch(String sql);
//statement.addBatch(String sql);
//提交一批要执行的更新命令
//int[] updateCounts = statement.executeBatch();  //批处理，返回一个int[],返回一个受影响的行数构成的数组
//conn.commit();//执行完后，手动提交事务
//connection.setAutoCommit(true);

//本例中禁用了自动执行模式，从而在调用 Statement.executeBatch() 时可以防止 JDBC 执行事务处理。（把所以语句当成一个事务）
// 禁用自动执行使得应用程序能够在发生错误及批处理中的某些命令不能执行时决定是否执行事务处理。因此，当进行批处理更新时，通常应该关闭自动执行。

//在JDBC 2.0 中，Statement 对象能够记住可以一起提交执行的命令列表。创建语句时，与它关联的命令列表为空。
//statement.addBatch() 方法为调用语句的命令列表添加一个元素。只能加入增、删、改 命令，不能加入查 命令
// 如果批处理中包含有试图返回结果集的命令，则当调用 statement.executeBatch() 时，将抛出 SQLException。
// 只有DDL 和 DML（INSERT、UPDATE、DELETE 命令）（它们只返回简单的更新计数）才能作为批处理的一部分来执行, 不能有DQL（SELECT命令）
// 如果应用程序决定不提交已经为某语句构造的命令批处理，则可以调用方法 Statement.clearBatch()来重新设置批处理。

//如果批处理中的某个命令无法正确执行，则 ExecuteBatch() 将抛出BatchUpdateException。
//可以调用BatchUpdateException.getUpdateCounts() 方法来为批处理中成功执行的命令返回更新计数的整型数组。
// 因为当有第一个命令返回错误时，Statement.executeBatch() 就中止，而且这些命令是依据它们在批处理中的添加顺序而执行的。
// 所以如果 BatchUpdateException.getUpdateCounts() 所返回的数组包含 N 个元素，这就意味着在调用 executeBatch() 时批处理中的前 N 个命令被成功执行。


import com.kuang.lesson02.utils.JdbcUtils;

import java.sql.*;
import java.util.Arrays;

public class Test_Batch {
    public static void main(String[] args) throws SQLException {
        Connection conn=null;
        Statement st=null;
        ResultSet rs=null;

        conn= JdbcUtils.getConnection();
        st= conn.createStatement();


       // Statement 使用批处理executeBatch()，使用事务
        conn.setAutoCommit(false);//关闭自动提交
        st.addBatch("INSERT INTO users(id,`name`,`password`,`email`,`birthday`)VALUES(20,'Test_Batch','123456','402794555@qq.com','2020-01-01')"+
                    ",(21,'Test_Batch','123456','402794555@qq.com','2020-01-01') ");
        st.addBatch("INSERT INTO users(id,`name`,`password`,`email`,`birthday`)VALUES(22,'Test_Batch','123456','402794555@qq.com','2020-01-01')");
        st.addBatch("INSERT INTO users(id,`name`,`password`,`email`,`birthday`)VALUES(23,'Test_Batch','123456','402794555@qq.com','2020-01-01')");
        st.addBatch("INSERT INTO users(id,`name`,`password`,`email`,`birthday`)VALUES(24,'Test_Batch','123456','402794555@qq.com','2020-01-01')");
        int[] ints = st.executeBatch();
        System.out.println("每条语句影响了"+ Arrays.toString(ints));
        conn.commit();
        conn.setAutoCommit(true);

//==================================================================================================
        //如果不捕获异常的话
        //在不开启批处理，不开启事务时，即多次更新多次commit ，不捕获异常，会在控制台打印错误，但是所有正确的语句都能得到执行（st与pst一样）
        //在开启批处理，不开启事务时，即一次更新一次commit ，不捕获异常，会在控制台打印错误，但是所有正确的语句都能得到执行（pst全部不执行）

        //在不开启批处理，开启事务时，即多次更新一次commit，不捕获异常，会在控制台打印错误，并且所有语句无论错误正确都得不到执行（st与pst一样）
        //开启批处理，开启事务时，即一次更新一次commit，不捕获异常，会在控制台打印错误，并且所有语句无论错误正确都得不到执行（st与pst一样）

        //综上 Statement 不捕获异常的话，只要不开启事务还是能够执行的，而PreparedStatement只要不开启事务且不开启批处理 才能得到执行


        // Statement 使用批处理executeBatch()，使用事务
        //conn.setAutoCommit(false);//关闭自动提交
        st.addBatch("INSERT INTO users(id,`name`,`password`,`email`,`birthday`)VALUES(50,'Test_Batch','123456','402794555@qq.com','2020-01-01')"+
                ",(51,'Test_Batch','123456','402794555@qq.com','2020-01-01') ");
                st.addBatch("INSERT INTO users(id,`name`,`password`,`email`,`birthday`)VALUES(52,'Test_Batch','123456','402794555@qq.com','2020-01-01')");
                st.addBatch("INSERT INTO users(id,`name`,`password`,`email`,`birthday`)VALUES(23,'Test_Batch','123456','402794555@qq.com','2020-01-01')");
                st.addBatch("INSERT INTO users(id,`name`,`password`,`email`,`birthday`)VALUES(53,'Test_Batch','123456','402794555@qq.com','2020-01-01')");

                //注意这里相当于对st.executeBatch()出现的异常进行了BatchUpdateException e捕获，
                // 可以通过 int[] result = e.getUpdateCounts(); 看看执行更新的情况
                // 会将所有的语句全部执行完，然后数组中成功的返回影响条数，失败的返回负数，比如-3。
                try {
                     int[] ints1 = st.executeBatch();//执行批处理
                //System.out.println("每条语句影响了"+ Arrays.toString(ints1));
                //放在这里不会输出，因为上一句出现了异常，转到catch,甚至连数组ints1都得不到返回值

                }catch (BatchUpdateException e) {  //会将所有的语句全部执行完，然后数组中成功的返回影响条数，失败的返回负数，比如-3。
                     int[] result = e.getUpdateCounts();
                     System.out.println("看看执行情况"+Arrays.toString(result));
                }

        //conn.commit();  //执行完后，手动提交事务,注意这里一定要手动提交，不然虽然pst.executeBatch()后更新了数据，知道影响了多少行
                           //但是，如果不手动commit就不会持久化到硬盘上，当然如果不想commit也可以选择rollback
        //conn.setAutoCommit(true);//再把自动提交打开,注意如果上面没有commit也没有rollback，这里开启自动提交后,会把批处理结果自动提交了*/

    }
}
