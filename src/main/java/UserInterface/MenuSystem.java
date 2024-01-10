package UserInterface;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

@Getter
public class MenuSystem {
    private final static Logger logger = LoggerFactory.getLogger("MenuSystemLogger");
    private Menu mainMenu;
    private Menu mealMenu;
    private Menu weeklyPlanMenu;
    private Menu listOfIngredientsMenu;
    private Scanner scanner;

    public MenuSystem() {
        this.mainMenu = new Menu();
        this.mealMenu = new Menu();
        this.weeklyPlanMenu = new Menu();
        this.listOfIngredientsMenu = new Menu();
    }

    public void enter() {
        try (Scanner scanner = new Scanner(System.in)) {
            showMainMenu();
        } catch (Exception e) {
            logger.error("Catch-all: {}", e.getMessage());
        }
    }

    private void showMainMenu() {

    }
}
