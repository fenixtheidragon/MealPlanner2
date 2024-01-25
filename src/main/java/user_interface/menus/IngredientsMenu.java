package user_interface.menus;

import backend.crud.*;
import static backend.crud.DataBaseNameConstants.*;

import java.util.*;

public class IngredientsMenu extends Menu implements IMenu {
	private final SQLExecutorForMealsDB sqlExecutor;

	public IngredientsMenu(String name, ArrayList<MenuOption> menuOptions, Scanner scanner) {
		super(name, menuOptions, scanner);
		this.sqlExecutor = new SQLExecutorForMealsDB();
	}

	public IngredientsMenu(IMenu menu) {
		super(menu);
		this.sqlExecutor = new SQLExecutorForMealsDB();
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
				default -> System.out.println("Invalid ingredeints menu option");
			}
		}
	}

	private void showIngredients() {
		System.out.println(sqlExecutor.execute(SQLStatements.getSelectAllColumnsFromTableStatement(TABLE_INGREDIENTS)));
	}

	private void addIngredients() {
		System.out.print("Enter ingredients names and amounts in grams (in form: apple:1000,banana:500): ");
		String namesAndAmounts = getScanner().nextLine();
		ArrayList<String[]> namesAndAmountsAL = new ArrayList<>();
		Arrays.stream(namesAndAmounts.split(",")).forEach(nameAndAmount -> namesAndAmountsAL.add(nameAndAmount.split(":")));
		for (String[] nameAndAmount: namesAndAmountsAL) {
			System.out.println(sqlExecutor.execute(SQLStatements.getInsertIntoStatement(
				TABLE_INGREDIENTS, List.of(COLUMN_INGREDIENT_NAME, COLUMN_AMOUNT_GRAMS), List.of(nameAndAmount[0],nameAndAmount[1]))));
		}
	}

	private void editIngredients() {

	}

	private void deleteIngredients() {

	}

}
