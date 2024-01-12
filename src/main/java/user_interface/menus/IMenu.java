package user_interface.menus;

import java.util.ArrayList;
import java.util.Scanner;

public interface IMenu {
    String getNAME();
    ArrayList<MenuOption> getMenuOptions();
    Scanner getScanner();
    MenuOption printMenuScanAndReturnOption();
    MenuOption getMenuOptionBy(String input);
    String toString();
}
