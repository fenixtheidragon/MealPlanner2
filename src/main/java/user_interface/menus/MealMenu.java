package user_interface.menus;

import java.util.*;
import java.util.stream.Collectors;

import backend.Meal;
import backend.MealType;
import backend.crud.*;

import static backend.crud.DBNames.*;

public class MealMenu extends Menu implements IMenu {
	private final QueryExecutorForMealsDB sqlExecutor;
	private Meal meal;

	public MealMenu(String name, ArrayList<MenuOption> menuOptions, Scanner scanner) {
		super(name, menuOptions, scanner);
		this.sqlExecutor = new QueryExecutorForMealsDB();
	}

	public MealMenu(IMenu menu) {
		super(menu);
		this.sqlExecutor = new QueryExecutorForMealsDB();
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

	private List<String> convertStringToList(String string) {
		List<String> al = Arrays.asList(string.split(System.lineSeparator()));
		return al.stream().filter(a -> !a.isBlank()).collect(Collectors.toCollection(ArrayList::new));
	}

	//building meal descriptions from two tables(meals and ingredients) using third table
	// (meal_to_ingredient_relations)
	public void showMeals() {
		// TODO set map-а
		List<List<String>> ingredientsForEachMealOrdered = new ArrayList<>();
		//
		String mealsAsString = sqlExecutor.execute(
				Queries.getSelectAllColumnsStatement(TABLE_MEALS));
		List<String> meals = convertStringToList(mealsAsString);
		//
		String mealIDsAsString = sqlExecutor.execute(
				Queries.getSelectColumnStatement(TABLE_MEALS, COLUMN_MEAL_ID));
		List<String> mealIDs = convertStringToList(mealIDsAsString);
		//
		mealIDs.forEach(mealID -> {
			String ingredientIDsAsString = sqlExecutor.execute(Queries.getSelectFieldByValueStatement(
					TABLE_MEAL_TO_INGREDIENT, COLUMN_INGREDIENT_ID, COLUMN_MEAL_ID, mealID));
			List<String> ingredientIDs = convertStringToList(ingredientIDsAsString);
			//
			StringJoiner ingredientsAsString = new StringJoiner(System.lineSeparator());
			ingredientIDs.forEach(ingredientID -> ingredientsAsString.add(
					sqlExecutor.execute(Queries.getSelectFieldByValueStatement(
							TABLE_INGREDIENTS, COLUMN_INGREDIENT_NAME, COLUMN_INGREDIENT_ID, ingredientID))));

			List<String> ingredients = convertStringToList(ingredientsAsString.toString());
			//
			ingredientsForEachMealOrdered.add(ingredients);
		});
		buildAndPrintMealDescription(meals, ingredientsForEachMealOrdered);
	}

	private void buildAndPrintMealDescription(List<String> meals,
			List<List<String>> ingredientsForEachMealOrdered) {
		StringJoiner mealDescription = new StringJoiner(System.lineSeparator());
		for (int mealIndex = 0; mealIndex < meals.size(); mealIndex++) {
			StringJoiner sj = new StringJoiner(", ");
			ingredientsForEachMealOrdered.get(mealIndex).forEach(sj::add);
			mealDescription.add(meals.get(mealIndex) + "|" + sj);
		}
		if (mealDescription.toString().isBlank()) System.out.println("No meals yet");
		else System.out.println(mealDescription);
	}

	private void addMeal() {
		meal = new Meal();
		enterName();
		enterCategory();
		enterIngredients();
		sqlExecutor.execute(Queries.getInsertIntoStatement(
				TABLE_MEALS, List.of(COLUMN_MEAL_NAME, COLUMN_CATEGORY),
				List.of(meal.getName(), meal.getCategory().getName())));
		//
		String meal_ID = sqlExecutor.execute(Queries.getSelectFieldByValueStatement(
				TABLE_MEALS, COLUMN_MEAL_ID, COLUMN_MEAL_NAME, meal.getName()));
		String ingredientID;
		for (String ingredient : meal.getIngredients()) {
			sqlExecutor.execute(
					(Queries.getInsertIntoStatement(TABLE_INGREDIENTS, List.of(COLUMN_INGREDIENT_NAME),
							List.of(ingredient))));
			ingredientID = sqlExecutor.execute(Queries.getSelectFieldByValueStatement(
					TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID, COLUMN_INGREDIENT_NAME, ingredient));
			sqlExecutor.execute(Queries.getInsertIntoStatement(TABLE_MEAL_TO_INGREDIENT,
					List.of(COLUMN_MEAL_ID, COLUMN_INGREDIENT_ID), List.of(meal_ID, ingredientID)));
		}
	}

	private void enterName() {
		System.out.print("Enter meal's name: ");
		meal.setName(getScanner().nextLine());
	}

	private void enterCategory() {
		System.out.println(MealType.getDescription());
		System.out.print("Enter meal's category or make it \"uncategorized\" by pressing enter: ");
		meal.setCategory(getScanner().nextLine());
	}

	private void enterIngredients() {
		String ingredients = "";
		while (!meal.setIngredients(ingredients)) {
			System.out.print("Enter meal's ingredients in csv format: ");
			ingredients = getScanner().nextLine();
		}
	}

	private void editMeal() {
		meal = new Meal();
		showMeals();
		System.out.print("Enter id of the meal you want to edit: ");
		String mealIDToCompare = getScanner().nextLine();
		String mealID =
				sqlExecutor.execute(
						Queries.getSelectRowStatement(TABLE_MEALS, COLUMN_MEAL_ID, mealIDToCompare));
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
		//check if fields were changed and update table if they were
		if (!meal.getName().isBlank())
			executeUpdateOfMealsTable(COLUMN_MEAL_NAME, meal.getName(), mealID);
		if (!meal.getCategory().equals(MealType.UNCATEGORIZED))
			executeUpdateOfMealsTable(COLUMN_CATEGORY, meal.getCategory().getName(), mealID);

		sqlExecutor.execute(
				Queries.getDeleteRowStatement(TABLE_MEAL_TO_INGREDIENT, COLUMN_MEAL_ID, mealID));
		for (String ingredient : meal.getIngredients()) {
			sqlExecutor.execute(
					Queries.getInsertIntoStatement(TABLE_INGREDIENTS, List.of(COLUMN_INGREDIENT_NAME),
							List.of(ingredient)));
			String ingredientID = sqlExecutor.execute(
					Queries.getSelectFieldByValueStatement(TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID,
							COLUMN_INGREDIENT_NAME,
							ingredient));
			sqlExecutor.execute(
					Queries.getInsertIntoStatement(TABLE_MEAL_TO_INGREDIENT,
							List.of(COLUMN_MEAL_ID, COLUMN_INGREDIENT_ID), List.of(mealID, ingredientID)));
		}
		System.out.println("Saved edit");
	}

	private void executeUpdateOfMealsTable(String column1, String value, String mealID) {
		System.out.println(sqlExecutor.execute(
				Queries.getUpdateForFieldStatement(DBNames.TABLE_MEALS, column1, value,
						DBNames.COLUMN_MEAL_ID, mealID)));
	}

	private void deleteMeal() {
		showMeals();
		System.out.print("Enter id of the meal you want to delete: ");
		String mealID = getScanner().nextLine();
		String meal =
				sqlExecutor.execute(Queries.getSelectRowStatement(TABLE_MEALS, "meal_id", mealID));
		String mealIDToCompare = meal.substring(0, mealID.length());
		if (!mealIDToCompare.isBlank() && mealIDToCompare.equals(mealID)) {
			sqlExecutor.execute(
					Queries.getDeleteRowStatement(TABLE_MEAL_TO_INGREDIENT, COLUMN_MEAL_ID, mealID));
			sqlExecutor.execute(
					Queries.getDeleteRowStatement(TABLE_WEEKLY_PLAN, COLUMN_MEAL_ID, mealID));
			sqlExecutor.execute(Queries.getDeleteRowStatement(TABLE_MEALS, COLUMN_MEAL_ID, mealID));
			System.out.println("Meal with id = \"" + mealID + "\" deleted");
		} else System.out.println("meal with id = \"" + mealID + "\" doesn't exist");
	}
}
