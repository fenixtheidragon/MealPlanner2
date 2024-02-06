package backend;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static backend.crud.ConstantsForStringBuilding.COMMA;

@Getter
public class Meal {
	private final Pattern correctIngredients = Pattern.compile("[a-zA-Z\\d]+(\\s*,\\s*[a-zA-Z\\d]+)*");
	private String name;
	private MealType category;
	private List<String> ingredients;

	public Meal() {
		this.name = "default meal name";
		this.category = MealType.UNCATEGORIZED;
	}

	public void setName(String input) {
		name = input.trim();
	}

	public void setCategory(String input) {
		category = MealType.getMealTypeByInput(input);
	}

	public boolean setIngredientsIfCorrect(String input) {
		if (correctIngredients.matcher(input).matches()) {
			ingredients = new ArrayList<>();
			var ingredients = input.split(",");
			for (var ingredient : ingredients) {
				ingredient = ingredient.trim();
				if (!ingredient.isBlank()) this.ingredients.add(ingredient);
			}
			return true;
		} else return false;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder()
			.append("Category: ").append(category).append(System.lineSeparator())
			.append("Name: ").append(name).append(System.lineSeparator())
			.append("Ingredients: ");
		ingredients.forEach(ingredient -> sb.append(COMMA).append(ingredient));
		sb.deleteCharAt(sb.indexOf(" ,") + 1);
		return sb.toString();
	}
}
