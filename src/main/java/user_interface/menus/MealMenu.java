package user_interface.menus;

import backend.Meal;
import backend.MealType;
import backend.crud.*;

import static backend.crud.DataBaseNameConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        System.out.println(sqlExecutor.execute(SQLStatements.getSelectAllColumnsFromTableStatement(TABLE_MEALS)));
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
        sqlExecutor.execute(SQLStatements.getInsertIntoStatement(TABLE_MEALS, List.of(COLUMN_NAME, COLUMN_CATEGORY),
                List.of(meal.getName(), meal.getCategory().getName())));
        String meal_ID = sqlExecutor.execute(SQLStatements.getSelectColumnFromTableWhereColumnEqualsValue(TABLE_MEALS, COLUMN_MEAL_ID, COLUMN_NAME, meal.getName()));
        sqlExecutor.execute(SQLStatements.getInsertIntoStatement(TABLE_MEAL_TO_INGREDIENT, List.of(COLUMN_MEAL_ID), List.of(meal_ID)));
        //
        enterIngredients();
        List<String> ingredient_IDs = new ArrayList<>();
        String ingredientID;
        for (String ingredient : meal.getIngredients()) {
            sqlExecutor.execute((SQLStatements.getInsertIntoStatement(TABLE_INGREDIENTS, List.of(COLUMN_NAME), List.of(ingredient))));
            ingredientID = sqlExecutor.execute(SQLStatements
                    .getSelectColumnFromTableWhereColumnEqualsValue(TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID, COLUMN_NAME, ingredient));
            ingredient_IDs.add(ingredientID);
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
                        saveEdit(mealIDToCompare, mealID);
                        condition = false;
                    }
                    default -> System.out.println("Invalid option");
                }
            }
        }
    }

    private void saveEdit(String mealIDToCompare, String mealID) {
        if (!meal.getName().isBlank()) executeUpdateOfMeal(TABLE_MEALS, COLUMN_NAME, meal.getName(), COLUMN_MEAL_ID, mealIDToCompare);
        if (!meal.getCategory().equals(MealType.UNCATEGORIZED)) {
            executeUpdateOfMeal(TABLE_MEALS, COLUMN_CATEGORY, meal.getCategory().getName(), COLUMN_MEAL_ID, mealIDToCompare);
        }
        for (String ingredient : meal.getIngredients()) {
            String statement = SQLStatements.getSelectColumnFromTableWhereColumnEqualsValue(TABLE_INGREDIENTS, SQLConstants.ALL, COLUMN_NAME, ingredient);
            System.out.println(sqlExecutor.execute(SQLStatements.getSelectExistsSelectStatement(statement)));
            executeUpdateOfMeal(TABLE_MEAL_TO_INGREDIENT, COLUMN_MEAL_ID, mealID,COLUMN_INGREDIENT_ID, ingredient );
        }
        System.out.println("Saved edit");
    }

    private void executeUpdateOfMeal(String table, String column1, String value, String column2, String mealID) {
        System.out.println(
                sqlExecutor.execute(SQLStatements.getUpdateTableSetColumn1ToValueWhereColumn2EqualsValueStatement(table, column1, value, column2, mealID)));
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
