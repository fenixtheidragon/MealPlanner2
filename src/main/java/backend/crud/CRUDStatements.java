package backend.crud;

import java.util.List;
import java.util.StringJoiner;

public class CRUDStatements {
    private final static String SELECT = "SELECT";
    private final static String FROM = "FROM";
    private final static String WHERE = "WHERE";
    private final static String INSERT_INTO = "INSERT INTO";
    private final static String SEMICOLON = ";";
    private final static String APOSTROPHE = "'";

    public static String getSelectColumnFromTableStatement(String table, String column) {
        return new StringJoiner(" ").add(SELECT).add(column).add(FROM).add(table).add(";").toString();
    }

    public static String getSelectAllColumnsFromTableStatement(String table) {
        return getSelectColumnFromTableStatement(table, "*");
    }

    public static String getSelectAllColumnsFromTableWhereColumnEqualsValueStatement(String table, String column, String value) {
        String selectAll = getSelectAllColumnsFromTableStatement(table);
        selectAll = (selectAll.substring(0, selectAll.length() - 2) + " WHERE " + column + " = " + value + SEMICOLON);
        System.out.println(selectAll);
        return selectAll;
    }

    public static String getInsertIntoStatement(String table, List<String> columns, List<String> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(table).append(" (");
        columns.forEach(c -> sb.append(c).append(", "));
        sb.delete(sb.length() - 2, sb.length());
        sb.append(") values (");
        values.forEach(v -> sb.append("'").append(v).append("',"));
        sb.replace(sb.length() - 1, sb.length(), ");");
        System.out.println(sb);
        return sb.toString();
    }

    public static String getUpdateTableSetColumnToValueWhereIDEqualsValue(String table, String column, String value, String ID) {
        return "UPDATE " + table + " SET " + column + " = '" + value + "' WHERE id = " + ID + SEMICOLON;
    }

    public static String getDeleteFromTableWhereColumnEqualsValue(String table, String column, String value) {
        //delete from meals where name = '';
        return "DELETE FROM " + table + " WHERE " + column + " = " + value + SEMICOLON;
    }
}