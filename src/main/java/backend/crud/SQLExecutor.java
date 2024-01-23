package backend.crud;

import static backend.crud.DataBaseNameConstants.*;
import static backend.crud.SQLConstants.*;

import backend.ExceptionLogger;
import lombok.Getter;

import java.sql.*;
import java.util.Properties;
import java.util.StringJoiner;

@Getter
public class SQLExecutor {
    private final String url;
    private final Properties properties;
    private final String[] queryKeyWords = new String[]{INSERT, DELETE, UPDATE, SELECT};
    private Statement statement;
    private String sql;
    private StringJoiner tempResult;
    private ResultSet resultSet;

    public SQLExecutor(String url, String user, String password) {
        this.url = url;
        this.properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
    }

    public String execute(String sql) {
        this.sql = sql;
        String result = connectAndQuery();
        if (result.contains("duplicate key value violates unique constraint")) {
            return "duplicate key value";
        } else {
            return result;
        }
    }

    private String connectAndQuery() {
        try (Connection connection = DriverManager.getConnection(url, properties)) {
            if (connection.isValid(5)) {
                try (Statement statement = connection.createStatement()) {
                    this.statement = statement;
                    return query();
                }
            } else return "Connection is invalid.";
        } catch (SQLException e) {
            return ExceptionLogger.getExceptionStackAsString(e, "Connection/statement/query exception:");
        }
    }

    private String query() throws SQLException {
        for (String keyWord : queryKeyWords) {
            if (sql.contains(keyWord)) {
                if (sql.contains(SELECT)) {
                    return querySelect();
                } /*else if (sql.contains("INSERT INTO")) {

                }*/ else {
                    return String.valueOf(executeUpdate(sql));
                }
            }
        }
        return "Invalid SQL query.";
    }

    private String querySelect() {
        try (ResultSet resultSet = executeQuery(sql)) {
            this.resultSet = resultSet;
            return getQuerySelectResult();
        } catch (SQLException e) {
            ExceptionLogger.getExceptionStackAsString(e, "querySelect exception:");
            return null;
        }
    }

    private String getQuerySelectResult() throws SQLException {
        StringJoiner result = new StringJoiner(System.lineSeparator());
        while (resultSet.next()) {
            tempResult = new StringJoiner("|");
            if (sql.contains(TABLE_MEALS)) {
                ifQueryContainsColumnAddToStringJoinerFromResultSet(COLUMN_MEAL_ID);
                ifQueryContainsColumnAddToStringJoinerFromResultSet(COLUMN_MEAL_NAME);
                ifQueryContainsColumnAddToStringJoinerFromResultSet(COLUMN_CATEGORY);
                if (sql.contains("*")) {
                    for (int a = 1; a <= 3; a++) tempResult.add(resultSet.getString(a));
                }
            } else if (sql.contains(TABLE_INGREDIENTS)) {
                //ifQueryContainsColumnAddToStringJoinerFromResultSet(COLUMN_INGREDIENT_ID, 0);
                ifQueryContainsColumnAddToStringJoinerFromResultSet(COLUMN_INGREDIENT_NAME);
            } else if (sql.contains(TABLE_MEAL_TO_INGREDIENT)) {
                ifQueryContainsColumnAddToStringJoinerFromResultSet(COLUMN_MEAL_ID);
                ifQueryContainsColumnAddToStringJoinerFromResultSet(COLUMN_INGREDIENT_ID);
            }
            result.add(tempResult.toString());
        }
        return result.toString();
    }

    private void ifQueryContainsColumnAddToStringJoinerFromResultSet(String columnName) throws SQLException {
        if (sql.contains(columnName)) tempResult.add(resultSet.getString(columnName));
    }

    //for INSERT, DELETE, UPDATE or CREATE, DROP
    private int executeUpdate(String sql) throws SQLException {
        return statement.executeUpdate(sql);
    }

    //for SELECT
    private ResultSet executeQuery(String sql) throws SQLException {
        return statement.executeQuery(sql);
    }
}