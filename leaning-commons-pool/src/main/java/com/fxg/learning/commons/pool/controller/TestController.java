package com.fxg.learning.commons.pool.controller;

import com.fxg.learning.commons.pool.factory.ConnectionFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * 测试
 */

@RestController
@RequestMapping("/test")
public class TestController {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private GenericObjectPool<Connection> connectionPool;
	private ConnectionFactory connectionFactory;

	@PostConstruct
	public void init() {
		GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
		poolConfig.setMaxTotal(10);
		poolConfig.setMaxIdle(10);
		poolConfig.setMinIdle(0);
		poolConfig.setLifo(false);
		connectionFactory = new ConnectionFactory();
		connectionPool = new GenericObjectPool<>(connectionFactory, poolConfig);
	}


	/**
	 * 查询初始化记录
	 */
	@GetMapping(value = "/test")
	public void test() {
		Connection connection = null;
		try {
			connection = connectionPool.borrowObject();
//			Thread.sleep((long) (Math.random() * 1000));
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select * from a1_user");
			logger.info("查询mysql成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("线程启动出错");
		} finally {
			if (Objects.nonNull(connection)) {
				connectionPool.returnObject(connection);
			}
		}
	}

	/**
	 * 查询初始化记录
	 */
	@GetMapping(value = "/test2")
	public void test2() {
		Connection connection = null;
		try {
			connection = connectionFactory.create();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select * from a1_user");
			logger.info("查询mysql成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("线程启动出错");
		} finally {
			if (Objects.nonNull(connection)) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
