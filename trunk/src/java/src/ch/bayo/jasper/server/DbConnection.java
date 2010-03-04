package ch.bayo.jasper.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

	private String driver;
	private String connection_str;
	private String usr;
	private String pwd;
	
	public DbConnection(String driver, String conn, String usr, String pwd) {
		this.driver = driver;
		this.connection_str = conn;
		this.usr = usr;
		this.pwd = pwd;
	}
	
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(connection_str, usr, pwd);
		return conn;
	}

}
