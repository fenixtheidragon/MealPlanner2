package backend.crud;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Properties;
import java.util.StringJoiner;

@Getter
@Slf4j
public class SQLExecutor {
    private final String TABLE_MEALS_NAME = "meals";
    private final String TABLE_INGREDIENTS_NAME = "ingredients";
    private final String TABLE_MEAL_TO_INGREDIENT_CONNECTION_NAME = "meal_to_ingredient_connection";
    private final String url;
    private final Properties properties;
    private final String[] queryKeyWords = new String[]{"INSERT", "DELETE", "UPDATE", "SELECT"};
    private Statement statement;

    public SQLExecutor(String url, String user, String password) {
        this.url = url;
        this.properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
    }

    public String execute(String sql) {
        String result = connectAndQuery(sql);
        if (result.matches("\\d+")) {
            return result + " rows were changed.";
        } else if (result.contains("duplicate key value violates unique constraint")) {
            return "duplicate key value";
        } else {
            return result;
        }
    }

    private String query(String sql) throws SQLException {
        for (String keyWord : queryKeyWords) {
            if (sql.contains(keyWord)) {
                if (sql.contains("SELECT")) {
                    try (ResultSet resultSet = executeQuery(sql)) {
                        StringJoiner result = new StringJoiner(System.lineSeparator());
                        while (resultSet.next()) {
                            if (sql.contains("meals")) {
                                StringJoiner tempResult = new StringJoiner("|");
                                tempResult.add(resultSet.getString("meal_id"));
                                tempResult.add(resultSet.getString("name"));
                                tempResult.add(resultSet.getString("category"));
                                result.add(tempResult.toString());
                            } else if (sql.contains("ingredients")) {
                                StringJoiner tempResult = new StringJoiner("|");
                                tempResult.add(resultSet.getString("ingredient_id"));
                                tempResult.add(resultSet.getString("name"));
                                result.add((tempResult.toString()));
                            }
                        }
                        return result.toString();
                    } catch (SQLException e) {
                        StringJoiner sj = new StringJoiner(System.lineSeparator());
                        sj.add("ResultSet creation exception:");
                        for (StackTraceElement s: e.getStackTrace()) {
                            sj.add(s.toString());
                        }
                        log.error(sj.toString());
                    }
                } /*else if (sql.contains("INSERT INTO")) {

                }*/ else {
                    return String.valueOf(executeUpdate(sql));
                }
            }
        }
        return "Invalid SQL query.";
    }

    //for INSERT, DELETE, UPDATE or CREATE, DROP
    private int executeUpdate(String sql) throws SQLException {
        return statement.executeUpdate(sql);
    }

    //for SELECT
    private ResultSet executeQuery(String sql) throws SQLException {
        return statement.executeQuery(sql);
    }

    private String connectAndQuery(String sql) {
        try (Connection connection = DriverManager.getConnection(url, properties)) {
            if (connection.isValid(5)) {
                try (Statement statement = connection.createStatement()) {
                    this.statement = statement;
                    try {
                        return query(sql);
                    } catch (SQLException e) {
                        log.error("SQLException " + e);
                    }
                }
            }
        } catch (SQLException exception) {
            log.error("Connection/Statement creation exception: " + exception.getMessage());
        }
        return "null";
    }
}