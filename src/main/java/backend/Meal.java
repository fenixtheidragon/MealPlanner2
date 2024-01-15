package backend;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Meal {
    private String name;
    private MealType type;
    private ArrayList<String> ingredients;

    public Meal() {
        this.name = "";
        this.type = MealType.UNCATEGORIZED;
        this.ingredients = new ArrayList<>(List.of(""));
    }

    public Meal(String name, String type, String ingredients) {
        this.name = name;
        setType(type);
        setIngredients(ingredients);
    }

    public boolean setName(String input) {
        name = input.trim();
        return !name.isBlank();
    }

    public boolean setType(String input) {
        type = MealType.getMealTypeByInput(input);
        return !type.equals(MealType.UNCATEGORIZED);
    }

    public boolean setIngredients(String input) {
        if (input.matches("[a-zA-Z\\d ]+(\\s*,\\s*[a-zA-Z\\d ]+)*")) {
            ArrayList<String> ingredients = Arrays.stream(input.split(",")).collect(Collectors.toCollection(ArrayList::new));
            for (String ingredient : ingredients) {
                if (!ingredient.isBlank()) this.ingredients.add(ingredient.trim());
            }
            return true;
        } else return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Category: " + type + "\nName: " + name + "\nIngredients: ");
        ingredients.forEach(ingredient -> sb.append(",").append(ingredient));
        sb.deleteCharAt(sb.indexOf(" ,") + 1);
        sb.append("\n");
        return sb.toString();
    }
}
