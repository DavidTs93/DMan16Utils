package me.DMan16.DMan16Utils.Classes;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class LocalDatabase {
	private final @NotNull File file;
	private HikariDataSource hikari;
	
	protected LocalDatabase(@NotNull Plugin plugin,@NotNull String databaseName) throws IOException,SQLException,ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		plugin.getDataFolder().mkdirs();
		file = new File(plugin.getDataFolder(),databaseName + ".db");
		if (!file.exists()) if (!file.createNewFile()) throw new IOException("Couldn't create db file!");
		connect();
		createTable();
	}
	
	protected LocalDatabase(@NotNull Plugin plugin) throws IOException,SQLException,ClassNotFoundException {
		this(plugin,"database");
	}
	
	private void connect() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl("jdbc:sqlite:" + file);
		hikariConfig.addDataSourceProperty("cachePrepStmts","true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize","250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit","2048");
		hikariConfig.setMaximumPoolSize(20);
		hikari = new HikariDataSource(hikariConfig);
	}
	
	public void close() throws SQLException {
		if (hikari == null) return;
		hikari.close();
		hikari = null;
	}
	
	@NotNull
	protected Connection getConnection() throws SQLException {
		if (hikari == null || hikari.isClosed()) connect();
		return hikari.getConnection();
	}
	
	protected abstract void createTable() throws SQLException;
}