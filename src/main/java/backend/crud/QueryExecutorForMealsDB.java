package backend.crud;

public class QueryExecutorForMealsDB extends QueryExecutor {
	private final static String URL = "jdbc:postgresql:meals_db";
	// caps TODO
	private final static String user = "postgres";
	private final static String password = "postgres";

	public QueryExecutorForMealsDB() {
		super(URL, user, password);
	}
}
