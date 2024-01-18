package backend.crud;

import lombok.Getter;

@Getter
public class DataBaseNameConstants {
    public final static String TABLE_MEALS = "meals";
    public final static String COLUMN_MEAL_ID = "meal_id";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_CATEGORY = "category";
    public final static String TABLE_INGREDIENTS = "ingredients";
    public final static String COLUMN_INGREDIENT_ID = "ingredient_id";
    public final static String TABLE_MEAL_TO_INGREDIENT = "meal_to_ingredient_relation";
    public final static String TABLE_M_TO_I_COLUMN_ID = "meal_to_ingredient_id";
}
