package user_interface.menus;

import backend.Ingredient;
import backend.crud.*;

import static backend.crud.DBNames.*;

import java.util.*;

public class IngredientsMenu extends Menu implements IMenu {
	private final QueryExecutorForMealsDB sqlExecutor;
	private Ingredient ingredient;

	public IngredientsMenu(String name, ArrayList<MenuOption> menuOptions, Scanner scanner) {
		super(name, menuOptions, scanner);
		this.sqlExecutor = new QueryExecutorForMealsDB();
	}

	public IngredientsMenu(IMenu menu) {
		super(menu);
		this.sqlExecutor = new QueryExecutorForMealsDB();
	}

	public void open() {
		boolean isOpen = true;
		while (isOpen) {
			MenuOption option = printMenuScanAndReturnOption();
			switch (option) {
				case SHOW_INGREDIENTS -> showIngredients();
				case ADD_INGREDIENTS -> addIngredients();
				case EDIT_INGREDIENTS -> editIngredients();
				case DELETE_INGREDIENTS -> deleteIngredients();
				case MAIN_MENU -> isOpen = false;
				default -> System.out.println("Invalid ingredients menu option");
			}
		}
	}

	private void showIngredients() {
		System.out.println(
				sqlExecutor.execute(SQLStatements.getSelectAllColumnsStatement(TABLE_INGREDIENTS)));
	}

	private void addIngredients() {
		System.out.print(
				"Enter ingredients names and amounts in grams (in form: apple:1000,banana:500): ");
		String namesAndAmounts = getScanner().nextLine();
		ArrayList<String[]> namesAndAmountsAL = new ArrayList<>();
		Arrays.stream(namesAndAmounts.split(","))
				.forEach(nameAndAmount -> namesAndAmountsAL.add(nameAndAmount.split(":")));
		for (var nameAndAmount : namesAndAmountsAL) {
			System.out.println(sqlExecutor.execute(SQLStatements.getInsertIntoStatement(TABLE_INGREDIENTS,
					List.of(COLUMN_INGREDIENT_NAME, COLUMN_AMOUNT_GRAMS),
					List.of(nameAndAmount[0], nameAndAmount[1]))));
		}
	}

	private void editIngredients() {
		this.ingredient = new Ingredient("");
		showIngredients();
		System.out.print("Enter id of ingredient you want to edit: ");
		String ingredientID = getScanner().nextLine();
		if (!checkIfIngredientExistsBy(ingredientID)) {
			System.out.println("Ingredient with id = " + ingredientID + " doesn't exist.");
		} else {
			boolean condition = true;
			while (condition) {
				System.out.println("""
				                   What do you want to edit(enter the number)?
				                   	1)name;
				                   	2)amount;
				                   	3)save edit""");
				String input = getScanner().nextLine();
				switch (input) {
					case "1" -> enterName();
					case "2" -> enterAmount();
					case "3" -> {
						saveEdit(ingredientID);
						condition = false;
					}
				}
			}
		}
	}

	private boolean checkIfIngredientExistsBy(String ingredientID) {
		return !sqlExecutor.execute(
				SQLStatements.getSelectRowStatement(TABLE_INGREDIENTS,
						COLUMN_INGREDIENT_ID, ingredientID)
		).isBlank();
	}

	private void enterName() {
		System.out.print("Enter new name: ");
		ingredient.setName(getScanner().nextLine());
	}

	private void enterAmount() {
		do {
			System.out.print("Enter new amount: ");
		} while (!ingredient.setAmount(getScanner().nextLine()));
	}

	private void saveEdit(String ingredientId) {
		if (!ingredient.getName().isBlank()) sqlExecutor.execute(
				SQLStatements.getUpdateForFieldStatement(TABLE_INGREDIENTS,
						COLUMN_INGREDIENT_NAME, ingredient.getName(), COLUMN_INGREDIENT_ID, ingredientId));
		if (ingredient.getAmount() != 0) sqlExecutor.execute(
				SQLStatements.getUpdateForFieldStatement(TABLE_INGREDIENTS,
						COLUMN_AMOUNT_GRAMS, String.valueOf(ingredient.getAmount()), COLUMN_INGREDIENT_ID,
						ingredientId));
	}

	private void deleteIngredients() {
		showIngredients();
		System.out.print("Enter id of ingredient you want to delete: ");
		String ingredientID = getScanner().nextLine();
		if (sqlExecutor.execute(
				SQLStatements.getDeleteRowStatement(TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID,
						ingredientID)).matches(("\\d+")))
			System.out.println("Ingredient with id = " + ingredientID + " was deleted");
		else System.out.println("Ingredient is in the recipe, so can not be deleted");
	}
}