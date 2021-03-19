package com.fxg.learning.commons.pool.factory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionFactory extends BasePooledObjectFactory<Connection> {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	String url = "jdbc:mysql://localhost:3306/demo?characterEncoding=UTF8&useSSL=false&serverTimezone=GMT";
	String username = "root";
	String password = "123456";

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception ex) {
			throw new RuntimeException(ex + "驱动类加载失败！");
		}
	}

	@Override
	public Connection create() throws Exception {
		logger.info("创建新的链接");
		return DriverManager.getConnection(url, username, password);
	}

	@Override
	public PooledObject<Connection> wrap(Connection connection) {
		return new DefaultPooledObject<>(connection);
	}

	@Override
	public void destroyObject(PooledObject<Connection> p) throws Exception {
		Connection connection = p.getObject();
		if (Objects.nonNull(connection)) {
			try {
				connection.close();
				logger.error("关闭连接");
			} catch (SQLException e) {
				logger.error("关闭连接失败");
			}
		}
	}

	@Override
	public void passivateObject(PooledObject<Connection> p) throws Exception {
		logger.info("连接返回连接池");
	}
}
