package user_interface.menus;

import java.util.ArrayList;
import java.util.Scanner;

public class MainMenu extends Menu{

    public MainMenu(String name, ArrayList<MenuOption> menuOptions, Scanner scanner) {
        super(name, menuOptions, scanner);
    }

    public void show() {
        MenuOption option = printMenuScanAndReturnOption();
        switch (option) {
            case MEAL_MENU -> getMenuOptionBy()
        }
    }
}
