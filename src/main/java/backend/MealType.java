package backend;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
		for (MealType type : MealType.values()) {
			if (type.getNumber().equals(input)) {
				return type;
			}
		}
		return MealType.UNCATEGORIZED;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (MealType type : MealType.values()) {
			sb.append(type.getNumber()).append(". ").append(type.getName()).append(";");
		}
		return sb.toString();
	}
}