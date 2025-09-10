package org.example.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static volatile HikariDataSource dataSource;

    private Database() {}

    public static HikariDataSource getDataSource() {
        if (dataSource == null) {
            synchronized (Database.class) {
                if (dataSource == null) {
                    HikariConfig config = new HikariConfig();
                    // 连接信息：优先环境变量，其次 app.properties，最后默认值
                    String host = Config.get("DB_HOST", "127.0.0.1");
                    int port = Integer.parseInt(Config.get("DB_PORT", "3306"));
                    String database = Config.get("DB_NAME", "users");
                    String user = Config.get("DB_USER", "root");
                    String password = Config.get("DB_PASSWORD", "ning749A");

                    String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                    config.setJdbcUrl(jdbcUrl);
                    config.setUsername(user);
                    config.setPassword(password);
                    config.setMaximumPoolSize(10);
                    config.setMinimumIdle(1);
                    config.setPoolName("game-users-pool");
                    dataSource = new HikariDataSource(config);

                    initializeSchema();
                }
            }
        }
        return dataSource;
    }

    private static void initializeSchema() {
        String ddl = "CREATE TABLE IF NOT EXISTS players (" +
                "phone VARCHAR(20) PRIMARY KEY," +
                "nickname VARCHAR(50) NOT NULL," +
                "password_hash VARCHAR(100) NOT NULL," +
                "seed1 INT NOT NULL DEFAULT 0," +
                "seed2 INT NOT NULL DEFAULT 0," +
                "seed3 INT NOT NULL DEFAULT 0," +
                "crop1 INT NOT NULL DEFAULT 0," +
                "crop2 INT NOT NULL DEFAULT 0," +
                "crop3 INT NOT NULL DEFAULT 0," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            s.executeUpdate(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("初始化数据表失败", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }
}


