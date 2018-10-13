package com.viching.generate.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IConnection {

    /**
     * 获取数据库链接
     * @return
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;
    
    /**
     * 关闭数据库连接
     * @param conn
     * @throws SQLException
     */
    void closeConnection(Connection conn) throws SQLException;
    
    /**
     * 关闭游标
     * @param rs
     * @throws SQLException
     */
    public void closeResultSet(ResultSet rs) throws SQLException;
}
