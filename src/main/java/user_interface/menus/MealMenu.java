package user_interface.menus;

import java.util.*;
import java.util.stream.Collectors;

import backend.Meal;
import backend.MealType;
import backend.crud.*;

import static backend.crud.DataBaseNameConstants.*;

public class MealMenu extends Menu implements IMenu {
 private final SQLExecutorForMealsDB sqlExecutor;
 private Meal meal;

 public MealMenu(String name, ArrayList<MenuOption> menuOptions, Scanner scanner) {
  super(name, menuOptions, scanner);
  this.sqlExecutor = new SQLExecutorForMealsDB();
 }

 public MealMenu(IMenu menu) {
  super(menu);
  this.sqlExecutor = new SQLExecutorForMealsDB();
 }

 public void open() {
  boolean isOpen = true;
  while (isOpen) {
   MenuOption option = printMenuScanAndReturnOption();
   switch (option) {
    case SHOW_MEALS -> showMeals();
    case ADD_MEAL -> addMeal();
    case EDIT_MEAL -> editMeal();
    case DELETE_MEAL -> deleteMeal();
    case MAIN_MENU -> isOpen = false;
    default -> System.out.println("Invalid meal menu option");
   }
  }
 }

 private ArrayList<String> getArrayListFrom(String string) {
  ArrayList<String> al = Arrays.stream(string.split(System.lineSeparator())).collect(Collectors.toCollection(ArrayList::new));
  return al.stream().filter(a -> !a.isBlank()).collect(Collectors.toCollection(ArrayList::new));
 }

 public void showMeals() {
  String mealsAsString = sqlExecutor.execute(SQLStatements.getSelectAllColumnsFromTableStatement(TABLE_MEALS));
  ArrayList<String> mealsAL = getArrayListFrom(mealsAsString);
  ArrayList<ArrayList<String>> ingredientsForEachMealOrdered = new ArrayList<>();
  //
  String mealIDsAsString = sqlExecutor.execute(SQLStatements.getSelectColumnFromTableStatement(TABLE_MEALS, COLUMN_MEAL_ID));
  ArrayList<String> mealIDsAL = getArrayListFrom(mealIDsAsString);
  //
  mealIDsAL.forEach(mealID -> {
   String ingredientIDsAsString = sqlExecutor.execute(
           SQLStatements.getSelectColumnFromTableWhereColumnEqualsValueStatement(TABLE_MEAL_TO_INGREDIENT, COLUMN_INGREDIENT_ID, COLUMN_MEAL_ID, mealID));
   ArrayList<String> ingredientIDsAL = getArrayListFrom(ingredientIDsAsString);
   //
   StringJoiner ingredientsAsString = new StringJoiner(System.lineSeparator());
   ingredientIDsAL.forEach(ingredientID -> ingredientsAsString.add(
           sqlExecutor.execute(SQLStatements.getSelectColumnFromTableWhereColumnEqualsValueStatement(
                   TABLE_INGREDIENTS, COLUMN_INGREDIENT_NAME, COLUMN_INGREDIENT_ID, ingredientID))));

   ArrayList<String> ingredientsAL = getArrayListFrom(ingredientsAsString.toString());
   //
   ingredientsForEachMealOrdered.add(ingredientsAL);
  });
  buildAndPrintMealDescription(mealsAL, ingredientsForEachMealOrdered);
 }

 private void buildAndPrintMealDescription(ArrayList<String> mealsAL, ArrayList<ArrayList<String>> ingredientsForEachMealOrdered) {
  StringJoiner mealDescription = new StringJoiner(System.lineSeparator());
  for (int mealIndex = 0; mealIndex < mealsAL.size(); mealIndex++) {
   StringJoiner sj = new StringJoiner(", ");
   for (String ingredient : ingredientsForEachMealOrdered.get(mealIndex)) {
    sj.add(ingredient);
   }
   mealDescription.add(mealsAL.get(mealIndex) + "|" + sj);
  }
  if (mealDescription.toString().isBlank()) System.out.println("No meals yet");
  else System.out.println(mealDescription);
 }

 private void addMeal() {
  meal = new Meal();
  enterName();
  enterCategory();
  enterIngredients();
  sqlExecutor.execute(SQLStatements.getInsertIntoStatement(TABLE_MEALS, List.of(COLUMN_MEAL_NAME, COLUMN_CATEGORY),
          List.of(meal.getName(), meal.getCategory().getName())));
  //
  String meal_ID = sqlExecutor.execute(
          SQLStatements.getSelectColumnFromTableWhereColumnEqualsValueStatement(TABLE_MEALS, COLUMN_MEAL_ID, COLUMN_MEAL_NAME, meal.getName()));
  String ingredientID;
  for (String ingredient : meal.getIngredients()) {
   sqlExecutor.execute((SQLStatements.getInsertIntoStatement(TABLE_INGREDIENTS, List.of(COLUMN_INGREDIENT_NAME), List.of(ingredient))));
   ingredientID = sqlExecutor.execute(
           SQLStatements.getSelectColumnFromTableWhereColumnEqualsValueStatement(TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID, COLUMN_INGREDIENT_NAME,
                   ingredient));
   sqlExecutor.execute(
           SQLStatements.getInsertIntoStatement(TABLE_MEAL_TO_INGREDIENT, List.of(COLUMN_MEAL_ID, COLUMN_INGREDIENT_ID), List.of(meal_ID, ingredientID)));
  }
 }

 private void enterName() {
  System.out.print("Enter meal's name: ");
  meal.setName(getScanner().nextLine());
 }

 private void enterCategory() {
  System.out.println(MealType.getDescription());
  System.out.print("Enter meal's category or make it \"uncategorized\" by pressing enter: ");
  meal.setCategory(getScanner().nextLine());
 }

 private void enterIngredients() {
  String ingredients = "";
  while (!meal.setIngredients(ingredients)) {
   System.out.print("Enter meal's ingredients in csv format: ");
   ingredients = getScanner().nextLine();
  }
 }

 private void editMeal() {
  meal = new Meal();
  showMeals();
  System.out.print("Enter id of the meal you want to edit: ");
  String mealIDToCompare = getScanner().nextLine();
  String mealID =
          sqlExecutor.execute(SQLStatements.getSelectAllColumnsFromTableWhereColumnEqualsValueStatement(TABLE_MEALS, COLUMN_MEAL_ID, mealIDToCompare));
  mealID = mealID.substring(0, mealIDToCompare.length());
  if (!mealID.isBlank() && mealID.equals(mealIDToCompare)) {
   boolean condition = true;
   while (condition) {
    System.out.println("""
                       What do you want to edit(enter the number)?
                       	1)name;
                       	2)category;
                       	3)ingredients;
                       	4)save edit""");
    String input = getScanner().nextLine();
    switch (input) {
     case "1" -> enterName();
     case "2" -> enterCategory();
     case "3" -> enterIngredients();
     case "4" -> {
      saveEdit(mealID);
      condition = false;
     }
     default -> System.out.println("Invalid option");
    }
   }
  }
 }

 private void saveEdit(String mealID) {
  if (!meal.getName().isBlank()) executeUpdateOfMealsTable(COLUMN_MEAL_NAME, meal.getName(), mealID);
  if (!meal.getCategory().equals(MealType.UNCATEGORIZED)) {
   executeUpdateOfMealsTable(COLUMN_CATEGORY, meal.getCategory().getName(), mealID);
  }
  sqlExecutor.execute(SQLStatements.getDeleteFromTableWhereColumnEqualsValue(TABLE_MEAL_TO_INGREDIENT, COLUMN_MEAL_ID, mealID));
  for (String ingredient : meal.getIngredients()) {
   sqlExecutor.execute(SQLStatements.getInsertIntoStatement(TABLE_INGREDIENTS, List.of(COLUMN_INGREDIENT_NAME), List.of(ingredient)));
   String ingredientID = sqlExecutor.execute(
           SQLStatements.getSelectColumnFromTableWhereColumnEqualsValueStatement(TABLE_INGREDIENTS, COLUMN_INGREDIENT_ID, COLUMN_INGREDIENT_NAME,
                   ingredient));
   sqlExecutor.execute(
           SQLStatements.getInsertIntoStatement(TABLE_MEAL_TO_INGREDIENT, List.of(COLUMN_MEAL_ID, COLUMN_INGREDIENT_ID), List.of(mealID, ingredientID)));
  }
  System.out.println("Saved edit");
 }

 private void executeUpdateOfMealsTable(String column1, String value, String mealID) {
  System.out.println(sqlExecutor.execute(
          SQLStatements.getUpdateTableSetColumn1ToValueWhereColumn2EqualsValueStatement(DataBaseNameConstants.TABLE_MEALS, column1, value,
                  DataBaseNameConstants.COLUMN_MEAL_ID, mealID)));
 }

 private void deleteMeal() {
  showMeals();
  System.out.print("Enter id of the meal you want to delete: ");
  String mealID = getScanner().nextLine();
  String meal = sqlExecutor.execute(SQLStatements.getSelectAllColumnsFromTableWhereColumnEqualsValueStatement(TABLE_MEALS, "meal_id", mealID));
  String mealIDToCompare = meal.substring(0, mealID.length());
  if (!mealIDToCompare.isBlank() && mealIDToCompare.equals(mealID)) {
   sqlExecutor.execute(SQLStatements.getDeleteFromTableWhereColumnEqualsValue(TABLE_MEAL_TO_INGREDIENT, COLUMN_MEAL_ID, mealID));
   sqlExecutor.execute(SQLStatements.getDeleteFromTableWhereColumnEqualsValue(TABLE_MEALS, COLUMN_MEAL_ID, mealID));
   System.out.println("Meal with id = \"" + mealID + "\" deleted");
  } else System.out.println("meal with id = \"" + mealID + "\" doesn't exist");
 }
}
