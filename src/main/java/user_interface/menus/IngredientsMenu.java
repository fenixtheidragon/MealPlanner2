package user_interface.menus;

import backend.Ingredient;
import backend.crud.*;

import static backend.crud.DBNames.*;

import java.util.*;

public class IngredientsMenu extends Menu implements IMenu {
	private final QueryExecutorForMealsDB sqlExecutor = new QueryExecutorForMealsDB();
	private Ingredient ingredient;

	public IngredientsMenu(String name, List<MenuOption> menuOptions, Scanner scanner) {
		super(name, menuOptions, scanner);
	}

	public IngredientsMenu(IMenu menu) {
		super(menu);
	}

	public void open() {
		var isOpen = true;
		while (isOpen) {
			var option = printMenuScanAndReturnOption();
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
			sqlExecutor.execute(Queries.getSelectAllColumnsStatement(TABLE_INGREDIENTS)));
	}

	private void addIngredients() {
		System.out.print(
			"Enter ingredients names and amounts in grams (in form: apple:1000,banana:500): ");
		var namesAndAmountsAsString = getScanner().nextLine();
		var namesAndAmounts = new ArrayList<String[]>();
		Arrays.stream(namesAndAmountsAsString.split(","))
			.forEach(nameAndAmount -> namesAndAmounts.add(nameAndAmount.split(":")));
		for (var nameAndAmount : namesAndAmounts) {
			System.out.println(sqlExecutor.execute(Queries.getInsertIntoStatement(
				TABLE_INGREDIENTS,
				List.of(COLUMN_INGREDIENT_NAME, COLUMN_AMOUNT_GRAMS),
				List.of(nameAndAmount[0], nameAndAmount[1])))
			);
		}
	}

	private void editIngredients() {
		this.ingredient = new Ingredient("");
		showIngredients();
		System.out.print("Enter id of ingredient you want to edit: ");
		var ingredientId = getScanner().nextLine();
		if (!doesIngredientExist(ingredientId)) {
			System.out.println("Ingredient with id = " + ingredientId + " doesn't exist.");
		} else {
			edit(ingredientId);
		}
	}

	private void edit(String ingredientId) {
		boolean condition = true;
		while (condition) {
			printEditMenu();
			String input = getScanner().nextLine();
			switch (input) {
				case "1" -> enterName();
				case "2" -> enterAmount();
				case "3" -> condition = saveEditAndReturnFalse(ingredientId);
				default -> System.out.println("Invalid menu option");
			}
		}
	}

	private void deleteIngredients() {
		showIngredients();
		System.out.print("Enter id of ingredient you want to delete: ");
		String ingredientID = getScanner().nextLine();
		if (doesIngredientExist(ingredientID)) {
			if (sqlExecutor.execute(
				Queries.getDeleteRowStatement(
					TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID, ingredientID)
			).matches(("\\d+"))) {
				System.out.println("Ingredient with id = " + ingredientID + " was deleted");
			} else {
				System.out.println("Ingredient is in the recipe, so can not be deleted");
			}
		} else {
			System.out.println("No ingredient with id = \"" + ingredientID + "\"");
		}
	}

	private void printEditMenu() {
		System.out.println("""
                      What do you want to edit(enter the number)?
                      1)name;
                      2)amount;
                      3)save edit""");
	}

	private boolean doesIngredientExist(String ingredientID) {
		return sqlExecutor.execute(
			Queries.getSelectExistsStatement(
				Queries.getSelectRowStatement(
					TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID, ingredientID)
			)).equals("t");
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

	private boolean saveEditAndReturnFalse(String ingredientId) {
		if (!ingredient.getName().isBlank()) {
			sqlExecutor.execute(
				Queries.getUpdateForFieldStatement(TABLE_INGREDIENTS, COLUMN_INGREDIENT_NAME,
					ingredient.getName(), COLUMN_INGREDIENT_ID, ingredientId));
		}
		if (ingredient.getAmount() != 0) {
			sqlExecutor.execute(Queries.getUpdateForFieldStatement(TABLE_INGREDIENTS,
				COLUMN_AMOUNT_GRAMS,
				String.valueOf(ingredient.getAmount()), COLUMN_INGREDIENT_ID, ingredientId));
		}
		return false;
	}
}