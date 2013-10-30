package pool;

import org.h2.tools.DeleteDbFiles;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbExample {

    public static void main(String[] args) throws Exception {
        // delete the database named 'test' in the user home directory
        DeleteDbFiles.execute("/tmp", "test", true);

        String driver = "org.h2.Driver";
        String url = "jdbc:h2:/tmp/test";
        String usr = "";
        String pwd = "";

        JdbcConnectionPool pool = new JdbcConnectionPool(driver, url, usr, pwd);
        Connection conn = null;
        try {
            conn = pool.borrowConnection();
            Statement stat = conn.createStatement();
            stat.execute("create table test(id int primary key, name varchar(255))");
            stat.execute("insert into test values(1, 'Hello')");
            ResultSet rs;
            rs = stat.executeQuery("select * from test");
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
            stat.close();
        } finally {
            pool.releaseConnection(conn);
        }

        Connection conn1 = pool.borrowConnection();
        Connection conn2 = pool.borrowConnection();
        Connection conn3 = pool.borrowConnection();

        pool.releaseConnection(conn1);
        pool.releaseConnection(conn2);
        pool.releaseConnection(conn3);

        Thread.sleep(2000);

        conn = pool.borrowConnection();
    }
}
