package user_interface.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public interface IMenu {
    String getNAME();
    List<MenuOption> getMenuOptions();
    Scanner getScanner();
    MenuOption printMenuScanAndReturnOption();
    MenuOption getMenuOptionBy(String input);
    String toString();
}
