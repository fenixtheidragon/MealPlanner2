package user_interface.menus;


import backend.crud.*;

import static backend.crud.DataBaseNameConstants.*;

import java.util.ArrayList;
import java.util.Scanner;

public class WeeklyPlanMenu extends Menu implements IMenu {
 private final SQLExecutorForMealsDB sqlExecutor;

 public WeeklyPlanMenu(String name, ArrayList<MenuOption> menuOptions, Scanner scanner) {
  super(name, menuOptions, scanner);
  this.sqlExecutor = new SQLExecutorForMealsDB();
 }

 public WeeklyPlanMenu(IMenu menu) {
  super(menu);
  this.sqlExecutor = new SQLExecutorForMealsDB();
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

  }
 }

 private void copyDay() {

 }

 private void clearDay() {

 }

 private void clearWeek() {

 }
}
