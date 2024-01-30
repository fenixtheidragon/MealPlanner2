package backend.crud;

import java.util.List;
import java.util.StringJoiner;

import static backend.crud.Constants.*;

public class SQLStatements {
	private static StringJoiner sj;

    /*public static String getSelectExistsSelectStatement(String selectStatement) {
        clearStringJoiner();
        sj.add(SELECT).add(EXISTS).add(LEFT_PAR).add(selectStatement.substring(0, selectStatement
        .length() - 1)).add(RIGHT_PAR).add(SEMICOL);
        return sj.toString();
    }*/

	public static String getSelectColumnStatement(String table, String column) {
		clearStringJoiner();
		sj.add(SELECT).add(column).add(FROM).add(table).add(SEMICOL);
		return sj.toString();
	}

	public static String getSelectAllColumnsStatement(String table) {
		return getSelectColumnStatement(table, ALL);
	}

	public static String getSelectFieldByValueStatement(String table, String column1,
			String column2, String value) {
		String statement = getSelectColumnStatement(table, column1);
		clearStringJoiner();
		sj.add(statement.substring(0, statement.length() - 2))
				.add(getWhereColumnEqualsValue(column2, value));
		return sj.toString();
	}

	public static String getSelectRowStatement(String table,
			String column, String value) {
		return getSelectFieldByValueStatement(table,ALL,column,value);
	}

	public static String getInsertIntoStatement(String table, List<String> columns,
			List<String> values) {
		StringBuilder sb = new StringBuilder().append("INSERT INTO ").append(table).append(" (");
		columns.forEach(c -> sb.append(c).append(", "));
		sb.delete(sb.length() - 2, sb.length());
		sb.append(") values (");
		values.forEach(v -> sb.append("'").append(v).append("',"));
		sb.replace(sb.length() - 1, sb.length(), ");");
		return sb.toString();
	}
//getUpdateForSelectRow
	public static String getUpdateForFieldStatement(String table,
			String column1, String value1, String column2, String value2) {
		return new StringJoiner(" ")
				.add(UPDATE).add(table).add(SET).add(column1).add(EQUALS).add(APSTRPH + value1 + APSTRPH)
				.add(getWhereColumnEqualsValue(column2, value2)).toString();
	}

	public static String getDeleteRowStatement(String table, String column,
			String value) {
		clearStringJoiner();
		sj.add(DELETE).add(FROM).add(table).add(getWhereColumnEqualsValue(column,value));
		return sj.toString();
	}

	public static String getClearTableStatement(String table) {
		clearStringJoiner();
		sj.add(DELETE).add(FROM).add(table).add(SEMICOL);
		return sj.toString();
	}

	private static void clearStringJoiner() {
		sj = new StringJoiner(SPACE);
	}

	private static String getWhereColumnEqualsValue(String column, String value) {
		return new StringJoiner((" ")).add(WHERE).add(column).add(EQUALS).add(APSTRPH).add(value)
				.add(APSTRPH).add(SEMICOL).toString();
	}
}