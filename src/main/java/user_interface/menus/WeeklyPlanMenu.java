package user_interface.menus;


import backend.crud.*;

import static backend.crud.DataBaseNameConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class WeeklyPlanMenu extends Menu implements IMenu {
 private final SQLExecutorForMealsDB sqlExecutor;
 private final MealMenu mealMenu;

 public WeeklyPlanMenu(String name, ArrayList<MenuOption> menuOptions, Scanner scanner) {
  super(name, menuOptions, scanner);
	 this.sqlExecutor = new SQLExecutorForMealsDB();
	 mealMenu = new MealMenu(new MenuFactory(getScanner()).getMenu(MenuOption.MEAL_MENU));
 }

 public WeeklyPlanMenu(IMenu menu) {
  super(menu);
	 this.sqlExecutor = new SQLExecutorForMealsDB();
	 mealMenu = new MealMenu(new MenuFactory(getScanner()).getMenu(MenuOption.MEAL_MENU));
 }

 public void open() {
  boolean isOpen = true;
  while (isOpen) {
   MenuOption option = printMenuScanAndReturnOption();
   switch (option) {
    case SHOW_WEEKLY_PLAN -> showWeeklyPlan();
    case EDIT_DAY -> editDay();
    case COPY_DAY -> copyDay();
    case CLEAR_WEEK -> clearWeek();
    case MAIN_MENU -> isOpen = false;
    default -> System.out.println("Invalid meal menu option");
   }
  }
 }

 private void showWeeklyPlan() {
  System.out.println(sqlExecutor.execute(SQLStatements.getSelectAllColumnsFromTableStatement(TABLE_WEEKLY_PLAN)));
 }

 private void editDay() {
  showWeeklyPlan();
  System.out.print("Which day to edit (1-7 - Monday-Sunday): ");
  String day = getScanner().nextLine();
  if (day.matches("\\d") && Integer.parseInt(day) >= 1 && Integer.parseInt(day) <= 7) {
   day = switch (day) {
    case "1" -> "Monday";
    case "2" -> "Tuesday";
    case "3" -> "Wednesday";
    case "4" -> "Thursday";
    case "5" -> "Friday";
    case "6" -> "Saturday";
    case "7" -> "Sunday";
    default -> "Invalid state of application.";
   };
   System.out.println(sqlExecutor.execute(SQLStatements.getSelectAllColumnsFromTableWhereColumnEqualsValueStatement(TABLE_WEEKLY_PLAN, COLUMN_DAY, day)));
   System.out.println("Choose meals for this day (IDs in csv format): ");
   mealMenu.showMeals();
   String meals = getScanner().nextLine();
   ArrayList<String> mealsAL = Arrays.stream(meals.split(",")).collect(Collectors.toCollection(ArrayList::new));
   for (String meal: mealsAL) {
    sqlExecutor.execute(SQLStatements.getInsertIntoStatement(TABLE_WEEKLY_PLAN, List.of(COLUMN_DAY,COLUMN_MEAL_ID), List.of(day, meal)));
   }
  }
 }

 private void copyDay() {

 }

 private void clearDay() {

 }

 private void clearWeek() {

 }
}
