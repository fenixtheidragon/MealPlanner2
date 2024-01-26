package user_interface.menus;

import java.util.ArrayList;
import java.util.Scanner;

public class MenuFactory {
    private Scanner scanner;

    public MenuFactory(Scanner scanner) {
        this.scanner = scanner;
    }

    public Menu getMenu(MenuOption menu) {
        return switch (menu) {
            case MAIN_MENU -> new Menu(MenuOption.MAIN_MENU.getName(), new ArrayList<>(MenuOption.getMainMenuOptions()), scanner);
            case MEAL_MENU -> new MealMenu(MenuOption.MEAL_MENU.getName(), new ArrayList<>(MenuOption.getMealMenuOptions()), scanner);
            case WEEKLY_PLAN_MENU -> new Menu(MenuOption.WEEKLY_PLAN_MENU.getName(), new ArrayList<>(MenuOption.getWeeklyPlanMenuOptions()), scanner);
            case INGREDIENTS_MENU -> new IngredientsMenu(MenuOption.INGREDIENTS_MENU.getName(), new ArrayList<>(MenuOption.getIngredientsMenuOptions()), scanner);
            default -> throw new IllegalStateException("No such menu exists: " + menu);
        };
    }
}
