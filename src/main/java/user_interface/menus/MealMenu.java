package user_interface.menus;

import backend.Meal;

import java.util.ArrayList;
import java.util.Scanner;

public class MealMenu extends Menu implements IMenu{
    private MenuOption option;
    private boolean isOpen;

    public MealMenu(String name, ArrayList<MenuOption> menuOptions, Scanner scanner) {
        super(name, menuOptions, scanner);
    }

    public MealMenu(IMenu menu) {
        super(menu);
    }

    public void open() {
        option = printMenuScanAndReturnOption();
        this.isOpen = true;
        while (isOpen) {
            switch (option) {
                case SHOW_MEALS -> showMeals();
                case ADD_MEAL -> addMeal();
                //case EDIT_MEAL -> editMeal();
                //case DELETE_MEAL -> deleteMeal();
                case MAIN_MENU -> this.isOpen = false;
                default -> System.out.println("Invalid meal menu option");
            }
        }
    }

    private void showMeals() {
        //SELECT * FROM meals;
    }

    private void addMeal() {
        Meal meal = new Meal();
        System.out.print("Enter meal's name: ");
        String name = getScanner().nextLine();
        meal.setName(name);
        //
        String type = null;
        while (!meal.setType(type)) {
            System.out.print("Enter meal's category or make it \"uncategorized\" by pressing enter: ");
            type = getScanner().nextLine();
        }
        String ingredients = null;
        while (!meal.setIngredients(ingredients)) {
            System.out.print("Enter meal's ingredients in csv format: ");
            ingredients = getScanner().nextLine();
        }
    }
}
