package backend;

import lombok.Getter;

import java.util.ArrayList;

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

	public void setName(String input) {
		name = input.trim();
	}

	public void setCategory(String input) {
		category = MealType.getMealTypeByInput(input);
	}

	public boolean setIngredients(String input) {
		if (input.matches("[a-zA-Z\\d]+(\\s*,\\s*[a-zA-Z\\d]+)*")) {
			this.ingredients = new ArrayList<>();
			String[] ingredients = input.split(",");
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
