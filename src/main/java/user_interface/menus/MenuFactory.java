package user_interface.menus;

import java.util.Scanner;

public class MenuFactory {
	private final Scanner scanner;

	public MenuFactory(Scanner scanner) {
		this.scanner = scanner;
	}

	public Menu getMenu(MenuOption menu) {
		return switch (menu) {
			case MAIN_MENU -> new Menu(
				MenuOption.MAIN_MENU.getName(), MenuOption.getMainMenuOptions(), scanner
			);
			case MEAL_MENU -> new MealMenu(
				MenuOption.MEAL_MENU.getName(), MenuOption.getMealMenuOptions(), scanner
			);
			case WEEKLY_PLAN_MENU -> new WeeklyPlanMenu(
				MenuOption.WEEKLY_PLAN_MENU.getName(), MenuOption.getWeeklyPlanMenuOptions(), scanner
			);
			case INGREDIENTS_MENU -> new IngredientsMenu(
				MenuOption.INGREDIENTS_MENU.getName(), MenuOption.getIngredientsMenuOptions(), scanner
			);
			default -> throw new IllegalStateException("Illegal state exception. No such menu exists: " + menu);
		};
	}
}
