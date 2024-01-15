package backend.crud;

import java.util.List;

public class CRUDStatements {

	public static String getSelectAllFromStatement(String table) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ").append(table).append(";");
		return sb.toString();
	}
	public static String getInsertIntoStatement(String table, List<String> columns, List<String> values) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(table).append(" (");
		columns.forEach(c->sb.append(c).append(", "));
		sb.delete(sb.length()-2,sb.length());
		sb.append(") values (");
		values.forEach(v->sb.append("'").append(v).append("',"));
		sb.replace(sb.length()-1,sb.length(), ");");
		System.out.println(sb);
		return sb.toString();
	}
}