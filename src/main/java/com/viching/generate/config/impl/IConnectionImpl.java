/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.viching.generate.config.impl;

import static com.viching.generate.db.util.messages.Messages.getString;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;

import com.viching.generate.config.Engine;
import com.viching.generate.config.IConnection;
import com.viching.generate.db.ObjectFactory;

/**
 * 获取数据库连接
 * @author Administrator
 *
 */
public class IConnectionImpl implements IConnection {
	
	@Autowired
    private Engine engine;

    /**
     * 初始化
     * @param engine
     */
    public IConnectionImpl() {
        super();
    }

    @Override
    public Connection getConnection() throws SQLException {

        Driver driver = getDriver();
        Connection conn = driver.connect(engine.getConnectionURL(), engine.getJDBCTarget());

        if (conn == null) {
            throw new SQLException(getString("RuntimeError.7")); //$NON-NLS-1$
        }

        return conn;
    }
    
    @Override
	public void closeConnection(Connection conn) throws SQLException {
    	if (conn != null) {
            try {
            	conn.close();
            } catch (SQLException e) {
                // ignore
                ;
            }
        }
	}
    
    @Override
	public void closeResultSet(ResultSet rs) throws SQLException{
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // ignore
                ;
            }
        }
    }

    private Driver getDriver() {
        Driver driver;

        try {
            Class<?> clazz = ObjectFactory.externalClassForName(engine.getDriverClass());
            driver = (Driver) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(getString("RuntimeError.8"), e); //$NON-NLS-1$
        }

        return driver;
    }
}
