package backend.crud;

import java.util.List;
import java.util.StringJoiner;

import static backend.crud.ConstantsForStringBuilding.*;

public class Queries {
	private static StringJoiner sj;

	public static String getSelectExistsStatement(String selectStatement) {
		clearStringJoiner();
		sj.add(SELECT).add(EXISTS).add(LEFT_PAR)
			.add(selectStatement.substring(0, selectStatement.length() - 1)).add(RIGHT_PAR).add(SEMICOL);
		return sj.toString();
	}

	public static String getSelectColumnStatement(String table, String column) {
		clearStringJoiner();
		sj.add(SELECT).add(column).add(FROM).add(table).add(SEMICOL);
		return sj.toString();
	}

	public static String getSelectAllColumnsStatement(String table) {
		return getSelectColumnStatement(table, ALL);
	}

	public static String getSelectFieldByValueStatement(String table, String column1, String column2,
		String value) {
		var statement = getSelectColumnStatement(table, column1);
		clearStringJoiner();
		sj.add(statement.substring(0, statement.length() - 2))
			.add(getWhereColumnEqualsValue(column2, value));
		System.out.println(sj);
		return sj.toString();
	}

	public static String getSelectRowStatement(String table, String column, String value) {
		return getSelectFieldByValueStatement(table, ALL, column, value);
	}

	public static String getInsertIntoStatement(String table, List<String> columns,
		List<String> values) {
		clearStringJoiner();
		sj.add(INSERT).add(INTO).add(table).add(LEFT_PAR);
		var sizeHelper = columns.size();
		for (var a = 0; a < sizeHelper; a++) {
			sj.add(columns.get(a));
			addComma(sizeHelper, a);
		}
		sj.add(RIGHT_PAR).add(VALUES).add(LEFT_PAR);
		sizeHelper = values.size();
		for (var a = 0; a < sizeHelper; a++) {
			sj.add(addApostrophes(values.get(a)));
			addComma(sizeHelper, a);
		}
		sj.add(RIGHT_PAR).add(SEMICOL);
		return sj.toString();
	}

	private static String addApostrophes(String initial) {
		return APSTRPH + initial + APSTRPH;
	}

	//getUpdateForSelectRow
	public static String getUpdateForFieldStatement(String table, String column1, String value1,
		String column2, String value2) {
		return new StringJoiner(" ").add(UPDATE).add(table).add(SET).add(column1).add(EQUALS)
			.add(APSTRPH + value1 + APSTRPH).add(getWhereColumnEqualsValue(column2, value2)).toString();
	}

	public static String getDeleteRowStatement(String table, String column, String value) {
		clearStringJoiner();
		sj.add(DELETE).add(FROM).add(table).add(getWhereColumnEqualsValue(column, value));
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
		return new StringJoiner((" ")).add(WHERE).add(column).add(EQUALS).add(APSTRPH + value + APSTRPH)
			.add(SEMICOL).toString();
	}

	private static void addComma(int sizeHelper, int a) {
		if (sizeHelper > 1 && a < sizeHelper - 1) sj.add(COMMA);
	}
}