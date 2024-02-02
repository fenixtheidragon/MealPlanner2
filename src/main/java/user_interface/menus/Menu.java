package user_interface.menus;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Getter
public class Menu implements IMenu{
    private final String NAME;
    private final List<MenuOption> menuOptions;
    private final Scanner scanner;

    public Menu(String name, List<MenuOption> menuOptions, Scanner scanner) {
        NAME = name;
        this.menuOptions = menuOptions;
        this.scanner = scanner;
    }

    public Menu(IMenu menu) {
        NAME = menu.getNAME();
        menuOptions = menu.getMenuOptions();
        scanner = menu.getScanner();
    }

    public MenuOption printMenuScanAndReturnOption() {
        System.out.println();
        System.out.println(this);
        var input = scanner.nextLine();
        return getMenuOptionBy(input);
    }

    public MenuOption getMenuOptionBy(String input) {
        if (input.matches("\\d")) {
            for (int a = 1; a <= menuOptions.size(); a++) {
                if (input.equals(String.valueOf(a))) {
                    return menuOptions.get(a - 1);
                }
            }
        }
        return MenuOption.DEFAULT;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(NAME);
        //line format: 1. menuOption;
        for (var a = 1; a <= menuOptions.size(); a++) {
            sb.append(System.lineSeparator()).append(a).append(". ").append(menuOptions.get(a - 1)).append(";");
        }
        return sb.toString();
    }
}
