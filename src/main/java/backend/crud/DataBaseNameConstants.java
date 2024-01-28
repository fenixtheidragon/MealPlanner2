package backend.crud;

import lombok.Getter;

@Getter
public class DataBaseNameConstants {
    public final static String TABLE_MEALS = "meals";
    public final static String COLUMN_MEAL_ID = "meal_id";
    public final static String COLUMN_MEAL_NAME = "meal_name";
    public final static String COLUMN_CATEGORY = "category";
    //
    public final static String TABLE_INGREDIENTS = "ingredients";
    public final static String COLUMN_INGREDIENT_ID = "ingredient_id";
    public final static String COLUMN_INGREDIENT_NAME = "ingredient_name";
    public final static String COLUMN_AMOUNT_GRAMS = "amount_grams";
    //
    public final static String TABLE_MEAL_TO_INGREDIENT = "meal_to_ingredient_relations";
    public final static String TABLE_M_TO_I_COLUMN_ID = "meal_to_ingredient_id";
    //
    public final static String TABLE_WEEKLY_PLAN = "weekly_plan";
    public final static String COLUMN_WEEKLY_PLAN_ID = "weekly_plan_id";
    public final static String COLUMN_DAY = "day";
}
