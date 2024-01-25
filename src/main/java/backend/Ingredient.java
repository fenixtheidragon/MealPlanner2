package backend;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Ingredient {

	private String name;
	private int amount;

	public Ingredient(String name) {
		this.name = name;
		this.amount = 0;
	}

	public boolean setAmount(String amount) {
		if (amount.matches("\\d+")) {
			this.amount = Integer.parseInt(amount);
			return true;
		} else return false;
	}
}
