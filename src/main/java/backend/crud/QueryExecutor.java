package backend.crud;

import static backend.crud.DBNames.*;
import static backend.crud.ConstantsForStringBuilding.*;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;

@Getter @Slf4j
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
		var result = connectAndQuery();
		if (result == null) {
			return "Exception occurred";
		}
		if (result.contains("duplicate key value violates unique constraint")) {
			return "duplicate key value";
		} else {
			return result;
		}
	}

	private String connectAndQuery() {
		try (var connection = DriverManager.getConnection(url, properties)) {
			if (connection.isValid(5)) {
				try (var statement = connection.createStatement()) {
					this.statement = statement;
					return query();
				}
			} else {
				return "Connection is invalid.";
			}
		} catch (SQLException e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}

	private String query() throws SQLException {
		for (var keyWord : queryKeyWords) {
			if (sql.contains(keyWord)) {
				if (sql.contains(SELECT)) {
					return querySelect();
				}
				else {
					return String.valueOf(executeUpdate(sql));
				}
			}
		}
		return "Invalid SQL query.";
	}

	private String querySelect() {
		try (var resultSet = executeQuery(sql)) {
			this.resultSet = resultSet;
			return getQuerySelectResult();
		} catch (SQLException e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}

	private String getQuerySelectResult() throws SQLException {
		var result = new StringJoiner(System.lineSeparator());
		while (resultSet.next()) {
			tempResult = new StringJoiner(VERTICAL);
			if (sql.contains(SELECT + SPACE + EXISTS)) {
				ifQueryContainsColumnAddToStringJoinerFromResultSet(List.of(EXISTS));
			} else if (sql.contains(TABLE_MEALS)) {
				ifQueryContainsColumnAddToStringJoinerFromResultSet(
					List.of(COLUMN_MEAL_ID, COLUMN_MEAL_NAME, COLUMN_CATEGORY));
			} else if (sql.contains(TABLE_INGREDIENTS)) {
				ifQueryContainsColumnAddToStringJoinerFromResultSet(
					List.of(COLUMN_INGREDIENT_ID, COLUMN_INGREDIENT_NAME, COLUMN_AMOUNT_GRAMS));
			} else if (sql.contains(TABLE_MEAL_TO_INGREDIENT)) {
				ifQueryContainsColumnAddToStringJoinerFromResultSet(
					List.of(COLUMN_MEAL_ID, COLUMN_INGREDIENT_ID));
			} else if (sql.contains(TABLE_WEEKLY_PLAN)) {
				ifQueryContainsColumnAddToStringJoinerFromResultSet(List.of(COLUMN_DAY, COLUMN_MEAL_ID));
			}
			result.add(tempResult.toString());
		}
		return result.toString();
	}

	private void ifQueryContainsColumnAddToStringJoinerFromResultSet(List<String> columnNames)
		throws SQLException {
		//
		if (sql.contains(ALL)) {
			for (var a = 1; a <= columnNames.size(); a++) {
				tempResult.add(resultSet.getString(a));
			}
		} else {
			for (var columnName : columnNames) {
				if (sql.contains(SELECT + SPACE + columnName)) {
					tempResult.add(resultSet.getString(columnName));
				}
			}
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