package me.DMan16.POPUtils.Classes;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public abstract class LocalDatabase {
	private final File file;
	private Connection connection;
	
	protected LocalDatabase(@NotNull Plugin plugin, @NotNull String databaseName) throws IOException,SQLException,ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		plugin.getDataFolder().mkdirs();
		file = new File(plugin.getDataFolder(),databaseName + ".db");
		if (!file.exists()) file.createNewFile();
		connection = null;
		connect();
		createTable();
	}
	
	protected LocalDatabase(@NotNull Plugin plugin) throws IOException,SQLException,ClassNotFoundException {
		this(plugin,"database");
	}
	
	public void connect() throws SQLException {
		if (connection == null) connection = DriverManager.getConnection("jdbc:sqlite:" + file);
	}
	
	public void close() throws SQLException {
		if (connection == null) return;
		connection.close();
		connection = null;
	}
	
	public void reconnect() throws SQLException {
		close();
		connect();
	}
	
	@NotNull
	protected Connection getConnection() throws SQLException {
		if (connection == null || connection.isClosed()) reconnect();
		return connection;
	}
	
	protected abstract void createTable() throws SQLException;
}