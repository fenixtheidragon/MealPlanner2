package backend;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static backend.crud.ConstantsForStringBuilding.SEMICOL;

@Getter
@RequiredArgsConstructor
public enum MealType {
	UNCATEGORIZED("uncategorized", "0"),
	BREAKFAST("breakfast", "1"),
	SECOND_BREAKFAST("second breakfast", "2"),
	LUNCH("lunch", "3"),
	TEA("tea", "4"),
	DINNER("dinner", "5"),
	SUPPER("supper", "6"),
	SNACK("snack", "7");

	private final String name;
	private final String number;

	public static MealType getMealTypeByInput(String input) {
		for (var type : MealType.values()) {
			if (type.getNumber().equals(input)) {
				return type;
			}
		}
		return MealType.UNCATEGORIZED;
	}

	public static String getDescription() {
		var sb = new StringBuilder();
		for (var mealType : MealType.values()) {
			sb.append(mealType.getNumber())
				.append(". ").
				append(mealType.getName()).
				append(SEMICOL)
				.append(System.lineSeparator());
		}
		return sb.delete(sb.length() - 1, sb.length()).toString();
	}
}