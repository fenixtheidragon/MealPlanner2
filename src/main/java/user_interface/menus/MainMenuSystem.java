package user_interface.menus;

import java.util.Scanner;

import static user_interface.menus.MenuOption.*;
import static user_interface.menus.MenuOption.INGREDIENTS_MENU;

public class MainMenuSystem {
    private final Menu mainMenu;
    private final MealMenu mealMenu;
    //private final WeeklyPlanMenu weeklyPlanMenu;
    //private final IngredientsMenu ingredientsMenu;
    private Scanner scanner;
    private MenuOption option;
    private MenuFactory menuFactory;
    private boolean isAlive;

    public MainMenuSystem(Scanner scanner) {
        this.scanner = scanner;
        this.menuFactory = new MenuFactory(scanner);
        this.mainMenu = menuFactory.getMenu(MAIN_MENU);
        this.mealMenu = new MealMenu(menuFactory.getMenu(MEAL_MENU));
        //this.weeklyPlanMenu = menuFactory.getMenu(WEEKLY_PLAN_MENU);
        //this.ingredientsMenu = menuFactory.getMenu(INGREDIENTS_MENU);
    }

    public void open() {
        this.option = mainMenu.printMenuScanAndReturnOption();
        this.isAlive = true;
        while (isAlive) {
            switch (option) {
                case MEAL_MENU -> mealMenu.open();
                //case WEEKLY_PLAN_MENU -> weeklyPlanMenu.open();
                //case INGREDIENTS_MENU -> ingredientsMenu.open();
                case APPLICATION_EXIT -> isAlive = false;
                default -> System.out.println("Invalid main menu option");
            }
        }
        System.out.println("Exiting");
    }
}