package backend.crud;

import static backend.crud.DBNames.*;
import static backend.crud.Constants.*;

import backend.ExceptionLogger;
import lombok.Getter;

import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;

@Getter
public class QueryExecutor {
	private final String url;
	private final Properties properties;
	private final String[] queryKeyWords = new String[]{INSERT, DELETE, UPDATE, SELECT};
	private Statement statement;
	private String sql;
	private StringJoiner tempResult;
	private ResultSet resultSet;

	public QueryExecutor(String url, String user, String password) {
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
			return ExceptionLogger.logAsError(e, "Connection/statement/query exception:");
		}
	}

	private String query() throws SQLException {
		for (String keyWord : queryKeyWords) {
			if (sql.contains(keyWord)) {
				if (sql.contains(SELECT)) return querySelect();
				else return String.valueOf(executeUpdate(sql));
			}
		}
		return "Invalid SQL query.";
	}

	private String querySelect() {
		try (ResultSet resultSet = executeQuery(sql)) {
			this.resultSet = resultSet;
			return getQuerySelectResult();
		} catch (SQLException e) {
			ExceptionLogger.logAsError(e, "querySelect exception:");
			return null;
		}
	}

	private String getQuerySelectResult() throws SQLException {
		StringJoiner result = new StringJoiner(System.lineSeparator());
		while (resultSet.next()) {
			tempResult = new StringJoiner("|");
			if (sql.contains(TABLE_MEALS)) {
				ifQueryContainsColumnAddToStringJoinerFromResultSet(List.of(COLUMN_MEAL_ID, COLUMN_MEAL_NAME, COLUMN_CATEGORY));
			} else if (sql.contains(TABLE_INGREDIENTS)) {
				ifQueryContainsColumnAddToStringJoinerFromResultSet(List.of(COLUMN_INGREDIENT_ID, COLUMN_INGREDIENT_NAME, COLUMN_AMOUNT_GRAMS));
			} else if (sql.contains(TABLE_MEAL_TO_INGREDIENT)) {
				ifQueryContainsColumnAddToStringJoinerFromResultSet(List.of(COLUMN_MEAL_ID, COLUMN_INGREDIENT_ID));
			} else if (sql.contains(TABLE_WEEKLY_PLAN)) {
				ifQueryContainsColumnAddToStringJoinerFromResultSet(List.of(COLUMN_DAY, COLUMN_MEAL_ID));
			}
			result.add(tempResult.toString());
		}
		return result.toString();
	}
//Переделать, чтобы не была завязана логика на ошибку
	private void ifQueryContainsColumnAddToStringJoinerFromResultSet(List<String> columnNames) throws SQLException {
		columnNames.forEach(columnName -> {
			try {
				if (sql.contains(columnName)) tempResult.add(resultSet.getString(columnName));
			} catch (SQLException e) {
				ExceptionLogger.logAsError(e, "probably resultSet doesn't have right column");
			}
		});
		if (sql.contains("*")) {
			for (int a = 1; a <= columnNames.size(); a++) tempResult.add(resultSet.getString(a));
		}
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