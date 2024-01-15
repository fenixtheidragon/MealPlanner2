package user_interface.menus;

import backend.Meal;
import backend.crud.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MealMenu extends Menu implements IMenu {
    private final SQLExecutorForMealsDB sqlExecutor;

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
				//case EDIT_MEAL -> editMeal();
				case DELETE_MEAL -> deleteMeal();
				case MAIN_MENU -> isOpen = false;
				default -> System.out.println("Invalid meal menu option");
			}
		}
	}

	private void deleteMeal() {
		System.out.print("Choose to be deleted meal: ");
		String mealName = getScanner().nextLine();
		System.out.println(sqlExecutor.execute(CRUDStatements.getSelectColumnFromTableStatement("meals", mealName)));
	}

	private void showMeals() {
		System.out.println(sqlExecutor.execute(CRUDStatements.getSelectAllColumnsFromTableStatement("meals")));
	}

	private void addMeal() {
		Meal meal = new Meal();
		System.out.print("Enter meal's name: ");
		String name = getScanner().nextLine();
		meal.setName(name);
		//
		String type;
		System.out.print("Enter meal's category or make it \"uncategorized\" by pressing enter: ");
		type = getScanner().nextLine();
		meal.setType(type);
		//
		String ingredients = "";
		while (!meal.setIngredients(ingredients)) {
			System.out.print("Enter meal's ingredients in csv format: ");
			ingredients = getScanner().nextLine();
		}
		sqlExecutor.execute(CRUDStatements.getInsertIntoStatement("meals", List.of("name","category"),List.of(name,meal.getType().getName())));
		for (String ingredient: meal.getIngredients()) {
			sqlExecutor.execute((CRUDStatements.getInsertIntoStatement("ingredients", List.of("name"), List.of(ingredient))));
		}
	}
}
