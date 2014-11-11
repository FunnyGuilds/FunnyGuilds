package net.dzikoysk.funnyguilds.data.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.data.Settings;

public class Database {
	
	private static Database instance;
	
	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;
	private Connection connection;

	public Database(){
		instance = this;
		Settings c = Settings.getInstance();
		this.hostname = c.mysqlHostname;
		this.port = c.mysqlPort;
		this.database = c.mysqlDatabase;
		this.user = c.mysqlUser;
		this.password = c.mysqlPassword;
		this.connection = null;
		this.firstConnection();
	}

	public Connection openConnection() {
		try {
			if(checkConnection()) return connection;
			Class.forName("com.mysql.jdbc.Driver");
			String s = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database;
			connection = DriverManager.getConnection(s, this.user, this.password);
			return connection;
		} catch (Exception e){
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
		return null;
	}
	
	public void firstConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + this.hostname + ":" + this.port + "/?user=" + this.user + "&password=" + this.password;
			Connection conn = DriverManager.getConnection(url);
			Statement s = conn.createStatement();
			s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + this.database);
			conn.close();
		} catch (Exception e){
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
	}

	public boolean checkConnection(){
		try {
			return connection != null && !connection.isClosed();
		} catch (SQLException e){
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
		return connection != null;
	}

	public Connection getConnection() {
		if(!checkConnection()) openConnection();
		return connection;
	}

	public boolean closeConnection() {
		if(connection == null) return false;
		try {
			connection.close();
		} catch (Exception e){
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
		return true;
	}

	public ResultSet executeQuery(String query) {
		try {
			if(!checkConnection()) openConnection();
			if(connection == null) return null;
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query);
			return result;
		} catch (Exception e){
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
		return null;
	}

	public int executeUpdate(String query){
		try {
			if(!checkConnection()) openConnection();
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate(query);
			return result;
		} catch (SQLException e) {
			if(e.getSQLState().equals("42S21")) return 4221;
			if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
		}
		return 0;
	}

	public static Database getInstance(){
		if(instance == null) return new Database();
		return instance;
	}
}
