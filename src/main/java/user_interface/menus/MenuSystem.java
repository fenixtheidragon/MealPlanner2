package user_interface.menus;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

@Getter
public class MenuSystem {
    private final static Logger logger = LoggerFactory.getLogger("MenuSystemLogger");
    private final Menu mainMenu;
    private final Menu mealMenu;
    private final Menu weeklyPlanMenu;
    private final Menu ingredientsMenu;
    private Scanner scanner;
    private MenuOption option;
    private MenuFactory menuFactory;

    public MenuSystem() {
        this.menuFactory = new MenuFactory(scanner);
        this.mainMenu = menuFactory.getMenu(MenuOption.MAIN_MENU);
        this.mealMenu = menuFactory.getMenu(MenuOption.MEAL_MENU);
        this.weeklyPlanMenu = menuFactory.getMenu(MenuOption.WEEKLY_PLAN_MENU);
        this.ingredientsMenu = menuFactory.getMenu(MenuOption.INGREDIENTS_MENU);
    }

    public void enter() {
        try (Scanner scanner = new Scanner(System.in)) {
            this.scanner = scanner;
            showMainMenu();
        } catch (Exception e) {
            logger.error("Catch-all: {}", e.getMessage());
        }
    }

    private void showMainMenu() {
        option = mainMenu.
        switch (option) {
            case MEAL_MENU -> showMealmenu();
        }
    }

    private void showMealmenu() {
        printMenuAndScanNextLineForOption(mealMenu);
        switch (option) {
            case SHOW_MEALS -> showMeals();
        }
    }
}
