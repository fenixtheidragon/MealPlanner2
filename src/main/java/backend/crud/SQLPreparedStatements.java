package backend.crud;

import java.util.List;
import java.util.StringJoiner;

import static backend.crud.DBNames.*;
import static backend.crud.ConstantsForStringBuilding.*;

public class SQLPreparedStatements {
 private static StringJoiner spaceJoiner = new StringJoiner(" ");

 private static void clearSpaceJoiner() {
  spaceJoiner = new StringJoiner(" ");
 }

 public String insertIntoMeals(List<String> values) {
  clearSpaceJoiner();
  spaceJoiner.add(INSERT).add(INTO).add(TABLE_MEALS).add(LEFT_PAR).add(COLUMN_MEAL_NAME).add(",").add(COLUMN_CATEGORY).add(RIGHT_PAR);
  spaceJoiner.add(VALUES).add(LEFT_PAR).add(APSTRPH).add(values.get(0)).add(APSTRPH).add(",").add(APSTRPH).add(values.get(1)).add(APSTRPH).add(SEMICOL);
  return spaceJoiner.toString();
 }

 public String insertIntoIngredients(List<String> values) {
  clearSpaceJoiner();
  spaceJoiner.add(INSERT).add(INTO).add(TABLE_INGREDIENTS).add(LEFT_PAR).add(COLUMN_INGREDIENT_NAME).add(",").add(COLUMN_AMOUNT_GRAMS).add(RIGHT_PAR);
  spaceJoiner.add(VALUES).add(LEFT_PAR).add(APSTRPH).add(values.get(0)).add(APSTRPH).add(",").add(APSTRPH).add(values.get(1)).add(APSTRPH).add(SEMICOL);
  return spaceJoiner.toString();
 }

 public String insertIntoMealToIngredientRelations(List<String> values) {
  clearSpaceJoiner();
  spaceJoiner.add(INSERT).add(INTO).add(TABLE_MEAL_TO_INGREDIENT).add(LEFT_PAR).add(COLUMN_MEAL_ID).add(",").add(COLUMN_INGREDIENT_ID).add(RIGHT_PAR);
  spaceJoiner.add(VALUES).add(LEFT_PAR).add(APSTRPH).add(values.get(0)).add(APSTRPH).add(",").add(APSTRPH).add(values.get(1)).add(APSTRPH).add(SEMICOL);
  return spaceJoiner.toString();
 }

 public String insertIntoWeeklyPlan(List<String> values) {
  clearSpaceJoiner();
  spaceJoiner.add(INSERT).add(INTO).add(TABLE_WEEKLY_PLAN).add(LEFT_PAR).add(COLUMN_DAY).add(",").add(COLUMN_MEAL_ID).add(RIGHT_PAR);
  spaceJoiner.add(VALUES).add(LEFT_PAR).add(APSTRPH).add(values.get(0)).add(APSTRPH).add(",").add(APSTRPH).add(values.get(1)).add(APSTRPH).add(SEMICOL);
  return spaceJoiner.toString();
 }
}
