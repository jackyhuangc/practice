package com.jacky.practice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Description Here!
 *
 * @author Jacky Huang
 * @date 2018-02-06 16:42
 * @since jdk1.8
 */
public class T_Jdbc {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		// （1）加载JDBC驱动，便于DriverManage能找到
		Class.forName("com.mysql.jdbc.Driver");

		// （2）建立并获取数据库连接
		Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://rm-uf60ec1554fou12qk33150.mysql.rds.aliyuncs.com:3306/biz_mq?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&autoReconnectForPools=true&pinGlobalTxToPhysicalConnection=true", "biz_test",
				"1Swb3hAN0Hax9p"); // 连接数据库

		// （3）创建JDBC Statements对象,建议用PreparedStatement，预编译，效率高
		PreparedStatement ps = null;

		String sql = "select count(*) from queue ";

		// （4）设置SQL语句的传入参数
		// ps.setString(1, "");

		// （5）执行SQL语句并获得查询结果
		ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		// （6）对查询结果进行转换处理并将处理结果返回
		while (rs.next()) {
			System.out.println(rs.getString(1));
		}

		// （7）释放相关资源（关闭Connection，关闭Statement，关闭ResultSet）
		if (rs != null)
			rs.close();// ResultSet结果集

		if (ps != null)
			ps.close();// 关闭Statement

		if (conn != null)
			conn.close();// 关闭连接

	}
}
