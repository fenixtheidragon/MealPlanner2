package user_interface.menus;


import backend.crud.*;

import static backend.crud.ConstantsForStringBuilding.COMMA;
import static backend.crud.DBNames.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class WeeklyPlanMenu extends Menu implements IMenu {
	private static final String[] daysOfWeek;
	private final QueryExecutorForMealsDB sqlExecutor;
	private final MealMenu mealMenu;

	static{
		daysOfWeek = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
	}

	public WeeklyPlanMenu(String name, List<MenuOption> menuOptions, Scanner scanner) {
		super(name, menuOptions, scanner);
		this.sqlExecutor = new QueryExecutorForMealsDB();
		mealMenu = new MealMenu(new MenuFactory(getScanner()).getMenu(MenuOption.MEAL_MENU));
	}

	public WeeklyPlanMenu(IMenu menu) {
		super(menu);
		sqlExecutor = new QueryExecutorForMealsDB();
		mealMenu = new MealMenu(new MenuFactory(getScanner()).getMenu(MenuOption.MEAL_MENU));
	}

	public void open() {
		boolean isOpen = true;
		while (isOpen) {
			var option = printMenuScanAndReturnOption();
			switch (option) {
				case SHOW_WEEKLY_PLAN -> showWeeklyPlan();
				case EDIT_DAY -> editDay();
				case COPY_DAY -> copyDay();
				case CLEAR_DAY -> clearDay();
				case CLEAR_WEEK -> clearWeek();
				case MAIN_MENU -> isOpen = false;
				default -> System.out.println("Invalid meal menu option");
			}
		}
	}

	private void showWeeklyPlan() {
		for (var dayNumber = 0; dayNumber < daysOfWeek.length; dayNumber++) {
			System.out.println((dayNumber+1)+". " + daysOfWeek[dayNumber]);
			printMeals(daysOfWeek[dayNumber]);
		}
	}

	private void editDay() {
		var day = getDayName("edit");
		if (day != null) {
			printDay(day);
			System.out.println("Choose meals for this day (IDs in csv format): ");
			mealMenu.showMeals();
			var meals = getScanner().nextLine().split(COMMA);
			insertIntoWeeklyPlan(day, meals);
		}
	}

	private void copyDay() {
		var dayToCopy = getDayName("copy");
		var dayToPasteTo = getDayName("paste to");
		if (areNotNull(dayToCopy, dayToPasteTo)) {
			delete(dayToPasteTo);
			pasteMealsFromTo(dayToCopy, dayToPasteTo);
		}
	}

	private void clearDay() {
		String day = getDayName("clear");
		if (day != null) {
			delete(day);
		}
	}

	private void clearWeek() {
		sqlExecutor.execute(Queries.getClearTableStatement(TABLE_WEEKLY_PLAN));
	}

	private void pasteMealsFromTo(String dayToCopy, String dayToPasteTo) {
		getMealIds(dayToCopy).forEach(
			mealID -> sqlExecutor.execute(
				Queries.getInsertIntoStatement(
					TABLE_WEEKLY_PLAN, List.of(COLUMN_DAY, COLUMN_MEAL_ID), List.of(dayToPasteTo, mealID)
				)
			)
		);
	}

	private void delete(String day) {
		sqlExecutor.execute(
			Queries.getDeleteRowStatement(TABLE_WEEKLY_PLAN, COLUMN_DAY, day)
		);
	}

	private boolean areNotNull(String dayToCopy, String dayToPasteTo) {
		return dayToCopy != null && dayToPasteTo != null;
	}

	private void insertIntoWeeklyPlan(String day, String[] meals) {
		for (var meal : meals) {
			sqlExecutor.execute(
				Queries.getInsertIntoStatement(
					TABLE_WEEKLY_PLAN, List.of(COLUMN_DAY, COLUMN_MEAL_ID), List.of(day, meal)
				)
			);
		}
	}

	private void printMeals(String day) {
		for (var mealId : getMealIds(day)) {
			if (!mealId.isBlank()) {
				System.out.println(sqlExecutor.execute(Queries.getSelectFieldByValueStatement(
					TABLE_MEALS, COLUMN_MEAL_NAME, COLUMN_MEAL_ID, mealId)));
			}
		}
	}

	private List<String> getMealIds(String day) {
		return Arrays.stream(
				sqlExecutor.execute(
					Queries.getSelectFieldByValueStatement(
						TABLE_WEEKLY_PLAN, COLUMN_MEAL_ID, COLUMN_DAY, day
					)
				).split(System.lineSeparator()))
			.filter(a -> !a.isBlank())
			.collect(Collectors.toCollection(ArrayList::new));
	}

	private void printDay(String day) {
		System.out.println(
			sqlExecutor.execute(
				Queries.getSelectRowStatement(
					TABLE_WEEKLY_PLAN, COLUMN_DAY, day
				)
			)
		);
	}

	private String getDayName(String keyword) {
		return getDayNameOrNull(showPlanPrintQuestionScanAnswer(keyword));
	}

	private String showPlanPrintQuestionScanAnswer(String keyWord) {
		showWeeklyPlan();
		System.out.print("Which day to " + keyWord + " (1-7 - Monday-Sunday): ");
		return getScanner().nextLine();
	}

	private String getDayNameOrNull(String dayNumber) {
		if (dayNumber.matches("\\d") && Integer.parseInt(dayNumber) >= 1
		    && Integer.parseInt(dayNumber) <= 7) {
			return daysOfWeek[Integer.parseInt(dayNumber) - 1];
		} else System.out.println("invalid day number. try 1-7");
		return null;
	}
}
