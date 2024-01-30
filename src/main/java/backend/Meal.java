package backend;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public class Meal {
	private String name;
	private MealType category;
	private ArrayList<String> ingredients;

	public Meal() {
		this.name = "";
		this.category = MealType.UNCATEGORIZED;
		this.ingredients = new ArrayList<>();
	}

	/*public Meal(String name, String category, String ingredients) {
		this.name = name;
		setCategory(category);
		setIngredients(ingredients);
	}*/

	public boolean setName(String input) {
		name = input.trim();
		return !name.isBlank();
	}

	public boolean setCategory(String input) {
		category = MealType.getMealTypeByInput(input);
		return !category.equals(MealType.UNCATEGORIZED);
	}

	public boolean setIngredients(String input) {
		if (input.matches("[a-zA-Z\\d]+(\\s*,\\s*[a-zA-Z\\d]+)*")) {
			this.ingredients = new ArrayList<>();
			// TOdo arrraylist to list
			ArrayList<String> ingredients =
					Arrays.stream(input.split(",")).collect(Collectors.toCollection(ArrayList::new));
			for (String ingredient : ingredients) {
				ingredient = ingredient.trim();
				if (!ingredient.isBlank()) this.ingredients.add(ingredient);
			}
			return true;
		} else return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
				.append("Category: ").append(category).append("\nName: ").append(name).append("\nIngredients: ");
		ingredients.forEach(ingredient -> sb.append(",").append(ingredient));
		sb.deleteCharAt(sb.indexOf(" ,") + 1);
		sb.append("\n");
		return sb.toString();
	}
}
