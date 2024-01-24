package user_interface.menus;

import java.util.Scanner;

import static user_interface.menus.MenuOption.*;

public class MainMenuSystem {
    private final Menu mainMenu;
    private final MealMenu mealMenu;
    //private final WeeklyPlanMenu weeklyPlanMenu;
    //private final IngredientsMenu ingredientsMenu;
    private final Scanner scanner;
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
        this.isAlive = true;
        while (isAlive) {
            this.option = mainMenu.printMenuScanAndReturnOption();
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
