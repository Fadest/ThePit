package dev.fadest.pit.storage.msql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.fadest.pit.utils.PluginUtils;
import org.bukkit.configuration.ConfigurationSection;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLManager {

    private static HikariDataSource source;

    public static void init(ConfigurationSection configurationSection) {
        if (source == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + configurationSection.getString("host", "localhost") + ":" +
                    configurationSection.getInt("port", 3306) + "/?autoReconnect=true&allowMultiQueries=true" +
                    "&characterEncoding=utf-8&serverTimezone=UTC&useSSL=false");
            config.setDriverClassName("com.mysql.jdbc.Driver");
            config.setUsername(configurationSection.getString("username"));
            config.setPassword(configurationSection.getString("password"));
            config.setConnectionTimeout(configurationSection.getInt("timeout"));
            config.addDataSourceProperty("cachePrepStmts", "true");
            source = new HikariDataSource(config);
        }
    }

    public static void performAsyncQuery(String query, QueryCallback successCallback, Object... replacements) {
        performAsyncQuery(query, successCallback, null, replacements);
    }

    public static void performAsyncQuery(String query, QueryCallback successCallback, ExceptionCallback exceptionCallback, Object... replacements) {
        PluginUtils.runAsync(() -> performQuery(query, successCallback, exceptionCallback, replacements));
    }

    public static CachedRowSet performQuery(String query, Object... replacements) {
        return performQuery(query, null, replacements);
    }

    public static void performQuery(String query, QueryCallback successCallback, ExceptionCallback exceptionCallback, Object... replacements) {
        CachedRowSet set = performQuery(query, exceptionCallback, replacements);

        if (set != null) {
            try {
                successCallback.accept(set);
            } catch (SQLException ex) {
                if (exceptionCallback != null) {
                    exceptionCallback.accept(ex);
                }

                ex.printStackTrace();
            }
        }
    }

    public static CachedRowSet performQuery(String query, ExceptionCallback exceptionCallback, Object... replacements) {
        try (Connection connection = source.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            int i = 0;

            for (Object replacement : replacements) {
                preparedStatement.setObject(++i, replacement);
            }

            ResultSet set = preparedStatement.executeQuery();
            CachedRowSet cachedSet = new FixedCachedRowSetImpl();

            cachedSet.populate(set);
            return cachedSet;
        } catch (SQLException ex) {
            if (exceptionCallback != null) {
                exceptionCallback.accept(ex);
            }

            ex.printStackTrace();
        }

        return null;
    }

    public static void performAsyncUpdate(String query, Object... replacements) {
        performAsyncUpdate(query, null, null, replacements);
    }

    public static void performAsyncUpdate(String query, Runnable successCallback, Object... replacements) {
        performAsyncUpdate(query, successCallback, null, replacements);
    }

    public static void performAsyncUpdate(String query, ExceptionCallback exceptionCallback, Object... replacements) {
        performAsyncUpdate(query, null, exceptionCallback, replacements);
    }

    public static void performAsyncUpdate(String query, Runnable successCallback, ExceptionCallback exceptionCallback, Object... replacements) {
        PluginUtils.runAsync(() -> performUpdate(query, successCallback, exceptionCallback, replacements));
    }

    public static void performUpdate(String query, Object... replacements) {
        performUpdate(query, null, null, replacements);
    }

    public static void performUpdate(String query, Runnable successCallback, Object... replacements) {
        performUpdate(query, successCallback, null, replacements);
    }

    public static void performUpdate(String query, ExceptionCallback exceptionCallback, Object... replacements) {
        performUpdate(query, null, exceptionCallback, replacements);
    }

    public static void performUpdate(String query, Runnable successCallback, ExceptionCallback exceptionCallback, Object... replacements) {
        try (Connection connection = source.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            int i = 0;

            for (Object replacement : replacements) {
                preparedStatement.setObject(++i, replacement);
            }

            preparedStatement.executeUpdate();

            if (successCallback != null) {
                successCallback.run();
            }
        } catch (SQLException ex) {
            if (exceptionCallback != null) {
                exceptionCallback.accept(ex);
            }

            ex.printStackTrace();
        }
    }

}
