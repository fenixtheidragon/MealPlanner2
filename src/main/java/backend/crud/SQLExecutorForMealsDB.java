package backend.crud;

public class SQLExecutorForMealsDB extends SQLExecutor {
    private final static String URL = "jdbc:postgresql:meals_db";
    private final static String userOrPassword = "postgres";

    public SQLExecutorForMealsDB() {
        super(URL, userOrPassword, userOrPassword);
    }
}
