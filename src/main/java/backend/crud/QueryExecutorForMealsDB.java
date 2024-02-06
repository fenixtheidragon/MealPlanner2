package backend.crud;

public class QueryExecutorForMealsDB extends QueryExecutor {
	private final static String URL = "jdbc:postgresql:meals_db";
	// caps TODO
	private final static String USER = "postgres";
	private final static String PASSWORD = "postgres";

	public QueryExecutorForMealsDB() {
		super(URL, USER, PASSWORD);
	}
}
