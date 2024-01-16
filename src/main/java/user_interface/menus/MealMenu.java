package user_interface.menus;

import backend.Meal;
import backend.MealType;
import backend.crud.*;

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
        System.out.println(sqlExecutor.execute(CRUDStatements.getSelectAllColumnsFromTableStatement("meals")));
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
        meal.setType(type);
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
        enterIngredients();
        sqlExecutor.execute(CRUDStatements.getInsertIntoStatement("meals", List.of("name", "category"), List.of(meal.getName(), meal.getType().getName())));
        for (String ingredient : meal.getIngredients()) {
            sqlExecutor.execute((CRUDStatements.getInsertIntoStatement("ingredients", List.of("name"), List.of(ingredient))));
        }
    }

    private void editMeal() {
        meal = new Meal();
        showMeals();
        System.out.print("Enter id of the meal you want to edit: ");
        String mealID = getScanner().nextLine();
        String mealDescription = sqlExecutor.execute(CRUDStatements.getSelectAllColumnsFromTableWhereColumnEqualsValueStatement("meals", "id", mealID));
        mealDescription = mealDescription.substring(0, mealID.length());
        if (!mealDescription.isBlank() && mealDescription.equals(mealID)) {
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
                    case "1" -> {
                        enterName();
                    }
                    case "2" -> {
                        enterCategory();
                    }
                    case "3" -> {
                        enterIngredients();
                    }
                    case "4" -> {
                        condition = false;
                        executeUpdateOfMeal("meals", "name", meal.getName(), mealID);
                        executeUpdateOfMeal("meals", "category", meal.getType().getName(), mealID);
                        for (String ingredient : meal.getIngredients()) {
                            executeUpdateOfMeal("ingredients", "name", ingredient, mealID);
                        }
                        System.out.println("Saved edit");
                    }
                    default -> System.out.println("Invalid option");
                }
            }
        }
    }

    private void executeUpdateOfMeal(String table, String column, String value, String mealID) {
        System.out.println(sqlExecutor.execute(CRUDStatements.getUpdateTableSetColumnToValueWhereIDEqualsValue(
                table, column, value, mealID)));
    }

    private void deleteMeal() {
        showMeals();
        System.out.print("Enter id of the meal you want to delete: ");
        String mealId = getScanner().nextLine();
        String meal = sqlExecutor.execute(CRUDStatements.getSelectAllColumnsFromTableWhereColumnEqualsValueStatement("meals", "id", mealId));
        meal = meal.substring(0, mealId.length());
        if (!meal.isBlank() && meal.equals(mealId)) {
            sqlExecutor.execute(CRUDStatements.getDeleteFromTableWhereColumnEqualsValue("meals", "id", meal));
            System.out.println("Meal with id = \"" + meal + "\" deleted");
        } else System.out.println("meal with id = \"" + meal + "\" doesn't exist");
    }
}
