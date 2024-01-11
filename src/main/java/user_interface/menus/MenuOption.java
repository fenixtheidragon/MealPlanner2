package user_interface.menus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum MenuOption {
    MAIN_MENU("Main menu"),
    DEFAULT("Default option"),
    //main menu options:
    APPLICATION_EXIT("Application exit"),
    MEAL_MENU("Meal menu"),
    WEEKLY_PLAN_MENU("Weekly plan menu"),
    INGREDIENTS_MENU("List of ingredients menu"),
    //meal menu options(+return):
    SHOW_MEALS("Show meals"),
    ADD_MEAL("Add meal"),
    EDIT_MEAL("Edit meal"),
    DELETE_MEAL("Delete meal"),
    //weekly plan menu options(+return):
    SHOW_WEEKLY_PLAN("Show weekly plan"),
    EDIT_DAY("Edit day"),
    COPY_DAY("Copy day"),
    CLEAR_DAY("Clear day"),
    CLEAR_WEEK("Clear week"),
    //ingredients menu options(+return):
    SHOW_INGREDIENTS("Show weekly plan"),
    ADD_INGREDIENTS("Add ingredients"),
    DELETE_INGREDIENTS("Delete ingredients");

    private final String name;

    public static List<MenuOption> getMainMenuOptions() {
        return List.of(APPLICATION_EXIT, MEAL_MENU, WEEKLY_PLAN_MENU, INGREDIENTS_MENU);
    }

    public static List<MenuOption> getMealMenuOptions() {
        return List.of(MAIN_MENU, SHOW_MEALS, ADD_MEAL, EDIT_MEAL, DELETE_MEAL);
    }

    public static List<MenuOption> getWeeklyPlanMenuOptions() {
        return List.of(MAIN_MENU, SHOW_WEEKLY_PLAN, EDIT_DAY, COPY_DAY, CLEAR_DAY, CLEAR_WEEK);
    }

    public static List<MenuOption> getIngredientsMenuOptions() {
        return List.of(MAIN_MENU, SHOW_INGREDIENTS, ADD_INGREDIENTS, DELETE_INGREDIENTS);
    }
}
