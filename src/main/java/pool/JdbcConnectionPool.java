package pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnectionPool extends ObjectPool {

    private String dsn, usr, pwd;

    public JdbcConnectionPool(String driver, String dsn, String usr, String pwd) {
        try {
            Class.forName(driver).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to load driver " + driver, e);
        }

        this.dsn = dsn;
        this.usr = usr;
        this.pwd = pwd;
    }

    @Override
    Object create() {
        try {
            return DriverManager.getConnection(dsn, usr, pwd);
        } catch (SQLException e) {
            return new RuntimeException("Unable to create jdbc connection", e);
        }
    }

    @Override
    boolean validate(Object o) {
        try {
            return ((Connection) o).isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    void expire(Object o) {
        try {
            ((Connection) o).close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection borrowConnection() {
        return (Connection) super.checkOut();
    }

    public void releaseConnection(Connection c) {
        super.checkIn(c);
    }
}
