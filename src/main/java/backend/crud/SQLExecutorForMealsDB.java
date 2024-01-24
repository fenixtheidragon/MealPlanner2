package backend.crud;

public class SQLExecutorForMealsDB extends SQLExecutor {
    private final static String URL = "jdbc:postgresql:meals_db";
    private final static String user = "postgres";
    private final static String password = "postgres";

    public SQLExecutorForMealsDB() {
        super(URL, user, password);
    }
}
