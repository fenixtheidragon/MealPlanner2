package user_interface.menus;

import java.util.Scanner;

import static user_interface.menus.MenuOption.*;

public class MainMenuSystem {
	private final Menu mainMenu;
	private final MealMenu mealMenu;
	private final WeeklyPlanMenu weeklyPlanMenu;
	private final IngredientsMenu ingredientsMenu;

	public MainMenuSystem(Scanner scanner) {
		var menuFactory = new MenuFactory(scanner);
		mainMenu = menuFactory.getMenu(MAIN_MENU);
		mealMenu = new MealMenu(menuFactory.getMenu(MEAL_MENU));
		weeklyPlanMenu = new WeeklyPlanMenu(menuFactory.getMenu(WEEKLY_PLAN_MENU));
		ingredientsMenu = new IngredientsMenu(menuFactory.getMenu(INGREDIENTS_MENU));
	}

	public void open() {
		var isAlive = true;
		while (isAlive) {
			var option = mainMenu.printMenuScanAndReturnOption();
			switch (option) {
				case MEAL_MENU -> mealMenu.open();
				case WEEKLY_PLAN_MENU -> weeklyPlanMenu.open();
				case INGREDIENTS_MENU -> ingredientsMenu.open();
				case APPLICATION_EXIT -> isAlive = false;
				default -> System.out.println("Invalid main menu option");
			}
		}
		System.out.println("Exiting");
	}
}
