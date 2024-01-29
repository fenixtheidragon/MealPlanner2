package backend.crud;

import java.util.List;
import java.util.StringJoiner;

import static backend.crud.SQLConstants.*;

public class SQLStatements {
    private static StringJoiner sj;

    private static void clearStringJoiner() {
        sj = new StringJoiner(SPACE);
    }

    public static String getSelectExistsSelectStatement(String selectStatement) {
        clearStringJoiner();
        sj.add(SELECT).add(EXISTS).add(LEFT_PAR).add(selectStatement.substring(0, selectStatement.length() - 1)).add(RIGHT_PAR).add(SEMICOL);
        return sj.toString();
    }

    public static String getSelectColumnFromTableStatement(String table, String column) {
        clearStringJoiner();
        sj.add(SELECT).add(column).add(FROM).add(table).add(SEMICOL);
        return sj.toString();
    }

    public static String getSelectColumnFromTableWhereColumnEqualsValueStatement(String table, String column1, String column2, String value) {
        String statement = getSelectColumnFromTableStatement(table, column1);
        clearStringJoiner();
        sj.add(statement.substring(0, statement.length() - 2)).add(WHERE).add(column2).add(EQUALS).add(APSTRPH + value + APSTRPH).add(SEMICOL);
        return sj.toString();
    }

    public static String getSelectAllColumnsFromTableStatement(String table) {
        return getSelectColumnFromTableStatement(table, ALL);
    }

    public static String getSelectAllColumnsFromTableWhereColumnEqualsValueStatement(String table, String column, String value) {
        String selectAll = getSelectAllColumnsFromTableStatement(table);
        clearStringJoiner();
        sj.add(selectAll.substring(0, selectAll.length() - 2)).add(WHERE).add(column).add(EQUALS).add(APSTRPH + value + APSTRPH).add(SEMICOL);
        return sj.toString();
    }

    public static String getInsertIntoStatement(String table, List<String> columns, List<String> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(table).append(" (");
        columns.forEach(c -> sb.append(c).append(", "));
        sb.delete(sb.length() - 2, sb.length());
        sb.append(") values (");
        values.forEach(v -> sb.append("'").append(v).append("',"));
        sb.replace(sb.length() - 1, sb.length(), ");");
        return sb.toString();
    }

    public static String getUpdateTableSetColumn1ToValueWhereColumn2EqualsValueStatement(String table, String column1, String value1, String column2, String value2) {
        sj = new StringJoiner(" ");
        sj.add(UPDATE).add(table).add(SET).add(column1).add(EQUALS).add(APSTRPH + value1 + APSTRPH);
        sj.add(WHERE).add(column2).add(EQUALS).add(APSTRPH + value2 + APSTRPH).add(SEMICOL);
        return sj.toString();
    }

    public static String getDeleteFromTableWhereColumnEqualsValue(String table, String column, String value) {
        clearStringJoiner();
        sj.add(DELETE).add(FROM).add(table).add(WHERE).add(column).add(EQUALS).add(APSTRPH + value + APSTRPH).add(SEMICOL);
        return sj.toString();
    }

    public static String getDeleteFromTableAll(String table) {
        clearStringJoiner();
        sj.add(DELETE).add(FROM).add(table).add(SEMICOL);
        return sj.toString();
    }
}