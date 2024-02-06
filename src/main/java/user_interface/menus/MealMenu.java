package user_interface.menus;

import java.util.*;
import java.util.stream.Collectors;

import backend.Meal;
import backend.MealType;
import backend.crud.*;

import static backend.crud.ConstantsForStringBuilding.*;
import static backend.crud.DBNames.*;

public class MealMenu extends Menu implements IMenu {
	private final QueryExecutorForMealsDB queryExecutor = new QueryExecutorForMealsDB();
	private Meal meal;

	public MealMenu(String name, List<MenuOption> menuOptions, Scanner scanner) {
		super(name, menuOptions, scanner);
	}

	public MealMenu(IMenu menu) {
		super(menu);
	}

	public void open() {
		var isOpen = true;
		while (isOpen) {
			var option = printMenuScanAndReturnOption();
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

	public void showMeals() {
		buildAndPrintMealsTable(getAllMeals(), getEachMealIngredientsList());
	}

	private void addMeal() {
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

	private void editMeal() {
		meal = new Meal();
		showMeals();
		System.out.print("Enter id of the meal you want to edit: ");
		String mealId = getScanner().nextLine();
		if (isCorrect(mealId)) {
			edit(mealId);
			System.out.println("Saved edit");
		} else {
			printMealDoesNotExist(mealId);
		}
	}

	private void deleteMeal() {
		showMeals();
		System.out.print("Enter id of the meal you want to delete: ");
		String mealId = getScanner().nextLine();
		if (isCorrect(mealId)) {
			delete(mealId);
			System.out.println("Meal with id = \"" + mealId + "\" deleted");
		} else {
			printMealDoesNotExist(mealId);
		}
	}

	private void printMealDoesNotExist(String mealId) {
		System.out.println("meal with id = \"" + mealId + "\" doesn't exist");
	}

	private boolean isCorrect(String mealId) {
		String mealIdToCompare = getMealIdBy(COLUMN_MEAL_ID, mealId);
		return !mealIdToCompare.isBlank() && !mealId.isBlank() && mealIdToCompare.equals(mealId);
	}

	private void edit(String mealId) {
		boolean condition = true;
		while (condition) {
			printEditMenu();
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

	private void printEditMenu() {
		System.out.println("""
                     What do you want to edit(enter the number)?
                     1)name;
                     2)category;
                     3)ingredients;
                     4)save edit""");
	}

	private boolean saveEditAndReturnFalse(String mealId) {
		updateNameAndCategory(mealId);
		deleteMealToIngredientRelation(mealId);
		insertIngredients(mealId);
		return false;
	}

	private void delete(String mealId) {
		deleteMealConnections(mealId);
		queryExecutor.execute(Queries.getDeleteRowStatement(TABLE_MEALS, COLUMN_MEAL_ID, mealId));
	}

	private void deleteMealConnections(String mealId) {
		deleteMealToIngredientRelation(mealId);
		deleteMealFromWeeklyPlan(mealId);
	}

	private void deleteMealToIngredientRelation(String mealId) {
		queryExecutor.execute(Queries.getDeleteRowStatement(
			TABLE_MEAL_TO_INGREDIENT, COLUMN_MEAL_ID, mealId));
	}

	private void deleteMealFromWeeklyPlan(String mealId) {
		queryExecutor.execute(Queries.getDeleteRowStatement(
			TABLE_WEEKLY_PLAN, COLUMN_MEAL_ID, mealId));
	}

	private void updateNameAndCategory(String mealId) {
		if (!meal.getName().isBlank()) {
			executeUpdateOfMealsTable(COLUMN_MEAL_NAME, meal.getName(), mealId);
		}
		if (!meal.getCategory().equals(MealType.UNCATEGORIZED)) {
			executeUpdateOfMealsTable(COLUMN_CATEGORY, meal.getCategory().getName(), mealId);
		}
	}

	private void executeUpdateOfMealsTable(String column1, String value, String mealId) {
		System.out.println(queryExecutor.execute(
			Queries.getUpdateForFieldStatement(TABLE_MEALS, column1, value, COLUMN_MEAL_ID, mealId))
		);
	}

	private void insertNameAndCategory() {
		queryExecutor.execute(
			Queries.getInsertIntoStatement(
				TABLE_MEALS, List.of(COLUMN_MEAL_NAME, COLUMN_CATEGORY),
				List.of(meal.getName(), meal.getCategory().getName())
			));
	}

	private boolean mealExists() {
		return queryExecutor.execute((Queries.getSelectExistsStatement(
			Queries.getSelectRowStatement(TABLE_MEALS, COLUMN_MEAL_NAME, meal.getName())))).equals("t");
	}

	private List<String> convertStringToList(String string) {
		return Arrays.stream(string.split(System.lineSeparator()))
			.filter(a -> !a.isBlank())
			.collect(Collectors.toList());
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
			queryExecutor.execute(Queries.getSelectAllColumnsStatement(TABLE_MEALS))
		);
	}

	private List<String> getEachMealIngredientsList() {
		var ingredientsForEachMeal = new ArrayList<String>();
		//
		getAllMealIDs().forEach(mealId -> {
			var ingredients = new StringJoiner(COMMA);
			getAllIngredientIDs(mealId).forEach(ingredientID ->
				ingredients.add(queryExecutor.execute(Queries.getSelectFieldByValueStatement(
					TABLE_INGREDIENTS, COLUMN_INGREDIENT_NAME, COLUMN_INGREDIENT_ID, ingredientID))
				));
			ingredientsForEachMeal.add(ingredients.toString());
		});
		return ingredientsForEachMeal;
	}

	private List<String> getAllMealIDs() {
		return convertStringToList(
			queryExecutor.execute(Queries.getSelectColumnStatement(TABLE_MEALS, COLUMN_MEAL_ID))
		);
	}

	private List<String> getAllIngredientIDs(String mealId) {
		return convertStringToList(queryExecutor.execute(
			Queries.getSelectFieldByValueStatement(TABLE_MEAL_TO_INGREDIENT, COLUMN_INGREDIENT_ID,
				COLUMN_MEAL_ID, mealId))
		);
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
		while (!meal.setIngredientsIfCorrect(ingredients)) {
			System.out.print("Enter meal's ingredients in csv format: ");
			ingredients = getScanner().nextLine();
		}
	}

	private void insertIngredients(String mealId) {
		for (var ingredient : meal.getIngredients()) {
			queryExecutor.execute(Queries.getInsertIntoStatement(
				TABLE_INGREDIENTS, COLUMN_INGREDIENT_NAME, ingredient)
			);
			queryExecutor.execute(Queries.getInsertIntoStatement(TABLE_MEAL_TO_INGREDIENT,
				List.of(COLUMN_MEAL_ID, COLUMN_INGREDIENT_ID),
				List.of(mealId, getIngredientId(ingredient)))
			);
		}
	}

	private String getIngredientId(String ingredient) {
		return queryExecutor.execute(Queries.getSelectFieldByValueStatement(
			TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID, COLUMN_INGREDIENT_NAME, ingredient)
		);
	}

	private String getMealIdBy(String column, String columnValue) {
		return queryExecutor.execute(Queries.getSelectFieldByValueStatement(
			TABLE_MEALS, COLUMN_MEAL_ID, column, columnValue)
		);
	}
}
