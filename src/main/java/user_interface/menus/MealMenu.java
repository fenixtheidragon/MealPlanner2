package user_interface.menus;

import backend.Meal;
import backend.MealType;
import backend.crud.*;

import static backend.crud.DataBaseNameConstants.*;

import java.util.*;
import java.util.stream.Collectors;

public class MealMenu extends Menu implements IMenu {


    private final SQLExecutorForMealsDB sqlExecutor;
    private Meal meal;

    public MealMenu(String name, ArrayList<MenuOption> menuOptions, Scanner scanner) {
        super(name, menuOptions, scanner);
        this.sqlExecutor = new SQLExecutorForMealsDB();
    }

    public MealMenu(IMenu menu) {
        super(menu);
        this.sqlExecutor = new SQLExecutorForMealsDB();
    }

    public void open() {
        boolean isOpen = true;
        while (isOpen) {
            MenuOption option = printMenuScanAndReturnOption();
            switch (option) {
                case SHOW_MEALS -> showMeals();
                case ADD_MEAL -> addMeal();
                case EDIT_MEAL -> editMeal();
                case DELETE_MEAL -> deleteMeal();
                case MAIN_MENU -> isOpen = false;
                default -> System.out.println("Invalid meal menu option");
            }
        }
    }

    private void showMeals() {

        String mealsAsString = sqlExecutor.execute(SQLStatements.getSelectAllColumnsFromTableStatement(TABLE_MEALS));
        ArrayList<String> mealsAL = Arrays.stream(mealsAsString.split(" ")).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<ArrayList<String>> ingredientsForEachMealOrdered = new ArrayList<>();
        String mealIDsAsString = sqlExecutor.execute(SQLStatements.getSelectColumnFromTableStatement(TABLE_MEALS, COLUMN_MEAL_ID));
        String[] mealsArray = mealIDsAsString.split(" ");
        ArrayList<String> mealIDs = Arrays.stream(mealsArray).collect(Collectors.toCollection(ArrayList::new));
        mealIDs.forEach(mealID -> {
            String ingredientIDsAsString = sqlExecutor.execute(
                    SQLStatements.getSelectColumnFromTableWhereColumnEqualsValueStatement(TABLE_MEAL_TO_INGREDIENT, COLUMN_INGREDIENT_ID, COLUMN_MEAL_ID, mealID));
            ArrayList<String> ingredientIDs = Arrays.stream(ingredientIDsAsString.split(" ")).collect(Collectors.toCollection(ArrayList::new));
            ingredientIDs.forEach(ingredientID -> {
                String ingredientsAsString = sqlExecutor.execute(SQLStatements.getSelectColumnFromTableWhereColumnEqualsValueStatement(
                                        TABLE_INGREDIENTS, COLUMN_INGREDIENT_NAME, COLUMN_INGREDIENT_ID, ingredientID));
                ArrayList<String> ingredientsAL = Arrays.stream(ingredientsAsString.split(" ")).collect(Collectors.toCollection(ArrayList::new));
                ingredientsForEachMealOrdered.add(ingredientsAL);
            });
        });
        StringJoiner mealDescription = new StringJoiner (System.lineSeparator());
        for (int mealIndex = 0; mealIndex < mealsAL.size(); mealIndex++) {
            StringJoiner sj = new StringJoiner(", ");
            for (String ingredient: ingredientsForEachMealOrdered.get(mealIndex)) {
                sj.add(ingredient);
            }
            mealDescription.add(mealsAL.get(mealIndex) + "|" + sj.toString());
        }

        //if (meals.isBlank()) meals = "No meals yet";
        System.out.println(mealDescription.toString());
    }

    private void enterName() {
        System.out.print("Enter meal's name: ");
        String name = getScanner().nextLine();
        meal.setName(name);
    }

    private void enterCategory() {
        String type;
        System.out.println(MealType.getDescription());
        System.out.print("Enter meal's category or make it \"uncategorized\" by pressing enter: ");
        type = getScanner().nextLine();
        meal.setCategory(type);
    }

    private void enterIngredients() {
        String ingredients = "";
        while (!meal.setIngredients(ingredients)) {
            System.out.print("Enter meal's ingredients in csv format: ");
            ingredients = getScanner().nextLine();
        }
    }

    private void addMeal() {
        meal = new Meal();
        enterName();
        enterCategory();
        sqlExecutor.execute(SQLStatements.getInsertIntoStatement(TABLE_MEALS, List.of(COLUMN_MEAL_NAME, COLUMN_CATEGORY),
                List.of(meal.getName(), meal.getCategory().getName())));
        String meal_ID =
                sqlExecutor.execute(
                        SQLStatements.getSelectColumnFromTableWhereColumnEqualsValueStatement(TABLE_MEALS, COLUMN_MEAL_ID, COLUMN_MEAL_NAME, meal.getName()));
        //
        enterIngredients();
        List<String> ingredient_IDs = new ArrayList<>();
        String ingredientID;
        for (String ingredient : meal.getIngredients()) {
            sqlExecutor.execute((SQLStatements.getInsertIntoStatement(TABLE_INGREDIENTS, List.of(COLUMN_INGREDIENT_NAME), List.of(ingredient))));
            ingredientID = sqlExecutor.execute(SQLStatements
                    .getSelectColumnFromTableWhereColumnEqualsValueStatement(TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID, COLUMN_INGREDIENT_NAME, ingredient));
            ingredient_IDs.add(ingredientID);
            sqlExecutor.execute(
                    SQLStatements.getInsertIntoStatement(TABLE_MEAL_TO_INGREDIENT, List.of(COLUMN_MEAL_ID, COLUMN_INGREDIENT_ID), List.of(meal_ID, ingredientID)));
        }


    }

    private void editMeal() {
        meal = new Meal();
        showMeals();
        System.out.print("Enter id of the meal you want to edit: ");
        String mealIDToCompare = getScanner().nextLine();
        String mealID = sqlExecutor.execute(SQLStatements.getSelectAllColumnsFromTableWhereColumnEqualsValueStatement(TABLE_MEALS, COLUMN_MEAL_ID, mealIDToCompare));
        mealID = mealID.substring(0, mealIDToCompare.length());
        if (!mealID.isBlank() && mealID.equals(mealIDToCompare)) {
            boolean condition = true;
            while (condition) {
                System.out.println("""
                                   What do you want to edit(enter the number)?
                                   1)name;
                                   2)category;
                                   3)ingredients;
                                   4)save edit""");
                String input = getScanner().nextLine();
                switch (input) {
                    case "1" -> enterName();
                    case "2" -> enterCategory();
                    case "3" -> enterIngredients();
                    case "4" -> {
                        saveEdit(mealID);
                        condition = false;
                    }
                    default -> System.out.println("Invalid option");
                }
            }
        }
    }

    private void saveEdit(String mealID) {
        if (!meal.getName().isBlank()) executeUpdateOfMealsTable(COLUMN_MEAL_NAME, meal.getName(), mealID);
        if (!meal.getCategory().equals(MealType.UNCATEGORIZED)) {
            executeUpdateOfMealsTable(COLUMN_CATEGORY, meal.getCategory().getName(), mealID);
        }
        sqlExecutor.execute(SQLStatements.getDeleteFromTableWhereColumnEqualsValue(TABLE_MEAL_TO_INGREDIENT, COLUMN_MEAL_ID, mealID));
        for (String ingredient : meal.getIngredients()) {
            sqlExecutor.execute(SQLStatements.getInsertIntoStatement(TABLE_INGREDIENTS, List.of(COLUMN_INGREDIENT_NAME), List.of(ingredient)));
            String ingredientID = sqlExecutor.execute(
                    SQLStatements.getSelectColumnFromTableWhereColumnEqualsValueStatement(TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID, COLUMN_INGREDIENT_NAME, ingredient));
            sqlExecutor.execute(
                    SQLStatements.getInsertIntoStatement(TABLE_MEAL_TO_INGREDIENT, List.of(COLUMN_MEAL_ID, COLUMN_INGREDIENT_ID), List.of(mealID, ingredientID)));
        }
        System.out.println("Saved edit");
    }

    private void executeUpdateOfMealsTable(String column1, String value, String mealID) {
        System.out.println(
                sqlExecutor.execute(SQLStatements.getUpdateTableSetColumn1ToValueWhereColumn2EqualsValueStatement(DataBaseNameConstants.TABLE_MEALS, column1, value,
                        DataBaseNameConstants.COLUMN_MEAL_ID, mealID)));
    }

    private void deleteMeal() {
        showMeals();
        System.out.print("Enter id of the meal you want to delete: ");
        String mealId = getScanner().nextLine();
        String meal = sqlExecutor.execute(SQLStatements.getSelectAllColumnsFromTableWhereColumnEqualsValueStatement(TABLE_MEALS, "meal_id", mealId));
        meal = meal.substring(0, mealId.length());
        if (!meal.isBlank() && meal.equals(mealId)) {
            sqlExecutor.execute(SQLStatements.getDeleteFromTableWhereColumnEqualsValue(TABLE_MEALS, "id", meal));
            System.out.println("Meal with id = \"" + meal + "\" deleted");
        } else System.out.println("meal with id = \"" + meal + "\" doesn't exist");
    }
}
