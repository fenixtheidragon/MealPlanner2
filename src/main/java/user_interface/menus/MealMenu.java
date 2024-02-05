package user_interface.menus;

import java.util.*;
import java.util.stream.Collectors;

import backend.Meal;
import backend.MealType;
import backend.crud.*;

import static backend.crud.ConstantsForStringBuilding.*;
import static backend.crud.DBNames.*;

public class MealMenu extends Menu implements IMenu {
	private final QueryExecutorForMealsDB sqlExecutor;
	private Meal meal;

	public MealMenu(String name, List<MenuOption> menuOptions, Scanner scanner) {
		super(name, menuOptions, scanner);
		this.sqlExecutor = new QueryExecutorForMealsDB();
	}

	public MealMenu(IMenu menu) {
		super(menu);
		this.sqlExecutor = new QueryExecutorForMealsDB();
	}

	public void open() {
		var isOpen = true;
		while (isOpen) {
			var option = printMenuScanAndReturnOption();
			switch (option) {
				case SHOW_MEALS -> showMealsOption();
				case ADD_MEAL -> addMealOption();
				case EDIT_MEAL -> editMealOption();
				case DELETE_MEAL -> deleteMealOption();
				case MAIN_MENU -> isOpen = false;
				default -> System.out.println("Invalid meal menu option");
			}
		}
	}//building meal descriptions from two tables(meals and ingredients) using third table

	// (meal_to_ingredient_relations)
	public void showMealsOption() {
		buildAndPrintMealsTable(getAllMeals(), getEachMealIngredientsList());
	}

	private void insertNameAndCategory() {
		sqlExecutor.execute(
			Queries.getInsertIntoStatement(TABLE_MEALS, List.of(COLUMN_MEAL_NAME, COLUMN_CATEGORY),
				List.of(meal.getName(), meal.getCategory().getName())
			)
		);
	}

	private void addMealOption() {
		meal = new Meal();
		enterName();
		if (mealExists()) {
			System.out.println("This meal already exists.");
			return;
		}
		enterCategory();
		enterIngredients();
		insertNameAndCategory();
		insertIngredients(getMealIdBy(COLUMN_MEAL_NAME, meal.getName()));
	}

	private void editMealOption() {
		meal = new Meal();
		showMealsOption();
		System.out.print("Enter id of the meal you want to edit: ");
		String mealIdToCompare = getScanner().nextLine();
		String mealId = getMealIdBy(COLUMN_MEAL_ID, mealIdToCompare);
		if (!mealId.isBlank() && !mealIdToCompare.isBlank() && mealId.equals(mealIdToCompare)) {
			edit(mealId);
		}
	}

	private void edit(String mealId) {
		boolean condition = true;
		while (condition) {
			printMenu();
			String input = getScanner().nextLine();
			switch (input) {
				case "1" -> enterName();
				case "2" -> enterCategory();
				case "3" -> enterIngredients();
				case "4" -> condition = saveEditAndReturnFalse(mealId);
				default -> System.out.println("Invalid option");
			}
		}
	}

	private void printMenu() {
		System.out.println("""
		                   What do you want to edit(enter the number)?
		                   1)name;
		                   2)category;
		                   3)ingredients;
		                   4)save edit""");
	}

	private boolean saveEditAndReturnFalse(String mealId) {
		//check if fields were changed and update table if they were
		handleNameAndCategoryChanges(mealId);
		delete(mealId);
		for (String ingredient : meal.getIngredients()) {
			sqlExecutor.execute(
				Queries.getInsertIntoStatement(TABLE_INGREDIENTS, COLUMN_INGREDIENT_NAME, ingredient));
			String ingredientID = sqlExecutor.execute(
				Queries.getSelectFieldByValueStatement(TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID,
					COLUMN_INGREDIENT_NAME, ingredient));
			sqlExecutor.execute(Queries.getInsertIntoStatement(TABLE_MEAL_TO_INGREDIENT,
				List.of(COLUMN_MEAL_ID, COLUMN_INGREDIENT_ID), List.of(mealId, ingredientID)));
		}
		System.out.println("Saved edit");
		return false;
	}

	private void delete(String mealId) {
		sqlExecutor.execute(
			Queries.getDeleteRowStatement(TABLE_MEAL_TO_INGREDIENT, COLUMN_MEAL_ID, mealId));
	}

	private void handleNameAndCategoryChanges(String mealId) {
		if (!meal.getName().isBlank()) {
			executeUpdateOfMealsTable(COLUMN_MEAL_NAME, meal.getName(), mealId);
		}
		if (!meal.getCategory().equals(MealType.UNCATEGORIZED)) {
			executeUpdateOfMealsTable(COLUMN_CATEGORY, meal.getCategory().getName(), mealId);
		}
	}

	private void executeUpdateOfMealsTable(String column1, String value, String mealId) {
		System.out.println(sqlExecutor.execute(
			Queries.getUpdateForFieldStatement(DBNames.TABLE_MEALS, column1, value,
				DBNames.COLUMN_MEAL_ID, mealId)));
	}

	private void deleteMealOption() {
		showMealsOption();
		System.out.print("Enter id of the meal you want to delete: ");
		String mealId = getScanner().nextLine();
		String meal =
			sqlExecutor.execute(Queries.getSelectRowStatement(TABLE_MEALS, "meal_id", mealId));
		String mealIdToCompare = meal.substring(0, mealId.length());
		if (!mealIdToCompare.isBlank() && mealIdToCompare.equals(mealId)) {
			delete(mealId);
			sqlExecutor.execute(Queries.getDeleteRowStatement(TABLE_WEEKLY_PLAN, COLUMN_MEAL_ID,
				mealId));
			sqlExecutor.execute(Queries.getDeleteRowStatement(TABLE_MEALS, COLUMN_MEAL_ID, mealId));
			System.out.println("Meal with id = \"" + mealId + "\" deleted");
		} else System.out.println("meal with id = \"" + mealId + "\" doesn't exist");
	}

	private boolean mealExists() {
		return sqlExecutor.execute((Queries.getSelectExistsStatement(
			Queries.getSelectRowStatement(TABLE_MEALS, COLUMN_MEAL_NAME, meal.getName())))).equals("t");
	}

	private List<String> convertStringToList(String string) {
		var al = Arrays.asList(string.split(System.lineSeparator()));
		return al.stream().filter(a -> !a.isBlank()).collect(Collectors.toList());
	}

	private void buildAndPrintMealsTable(List<String> meals, List<String> ingredientsForEachMeal) {
		var mealsTable = new StringJoiner(System.lineSeparator());
		for (var mealIndex = 0; mealIndex < meals.size(); mealIndex++) {
			mealsTable.add(meals.get(mealIndex) + VERTICAL + ingredientsForEachMeal.get(mealIndex));
		}
		if (mealsTable.toString().isBlank()) System.out.println("No meals yet");
		else System.out.println(mealsTable);
	}

	private List<String> getAllMeals() {
		return convertStringToList(
			sqlExecutor.execute(Queries.getSelectAllColumnsStatement(TABLE_MEALS)));
	}

	private List<String> getEachMealIngredientsList() {
		var ingredientsForEachMeal = new ArrayList<String>();
		//
		getAllMealIDs().forEach(mealId -> {
			var ingredients = new StringJoiner(COMMA);
			getAllIngredientIDs(mealId).forEach(ingredientID -> {
				ingredients.add(sqlExecutor.execute(Queries.getSelectFieldByValueStatement(
					TABLE_INGREDIENTS, COLUMN_INGREDIENT_NAME, COLUMN_INGREDIENT_ID, ingredientID)));
			});
			ingredientsForEachMeal.add(ingredients.toString());
		});
		return ingredientsForEachMeal;
	}

	private List<String> getAllMealIDs() {
		return convertStringToList(
			sqlExecutor.execute(Queries.getSelectColumnStatement(TABLE_MEALS, COLUMN_MEAL_ID)));
	}

	private List<String> getAllIngredientIDs(String mealId) {
		return convertStringToList(sqlExecutor.execute(
			Queries.getSelectFieldByValueStatement(TABLE_MEAL_TO_INGREDIENT, COLUMN_INGREDIENT_ID,
				COLUMN_MEAL_ID, mealId)));
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
		String ingredients = EMPTY;
		while (!meal.setIngredients(ingredients)) {
			System.out.print("Enter meal's ingredients in csv format: ");
			ingredients = getScanner().nextLine();
		}
	}

	private void insertIngredients(String mealId) {
		for (String ingredient : meal.getIngredients()) {
			sqlExecutor.execute(Queries.getInsertIntoStatement(
				TABLE_INGREDIENTS, COLUMN_INGREDIENT_NAME, ingredient)
			);
			sqlExecutor.execute(Queries.getInsertIntoStatement(TABLE_MEAL_TO_INGREDIENT,
				List.of(COLUMN_MEAL_ID, COLUMN_INGREDIENT_ID),
				List.of(mealId, getIngredientId(ingredient)))
			);
		}
	}

	private String getIngredientId(String ingredient) {
		return sqlExecutor.execute(Queries.getSelectFieldByValueStatement(
			TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID, COLUMN_INGREDIENT_NAME, ingredient));
	}

	private String getMealIdBy(String column, String columnValue) {
		return sqlExecutor.execute(Queries.getSelectFieldByValueStatement(
			TABLE_MEALS, COLUMN_MEAL_ID, column, columnValue)
		);
	}
}
