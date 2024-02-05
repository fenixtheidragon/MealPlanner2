package user_interface.menus;


import backend.crud.*;

import static backend.crud.DBNames.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class WeeklyPlanMenu extends Menu implements IMenu {
	private final List<String> daysOfWeek = new ArrayList<>(
		List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
	private final QueryExecutorForMealsDB sqlExecutor;
	private final MealMenu mealMenu;

	public WeeklyPlanMenu(String name, ArrayList<MenuOption> menuOptions, Scanner scanner) {
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
		daysOfWeek.forEach(day -> {
			System.out.println(day);
			ArrayList<String> mealIDs =
				Arrays.stream(sqlExecutor.execute(Queries.getSelectFieldByValueStatement(
						TABLE_WEEKLY_PLAN, COLUMN_MEAL_ID, COLUMN_DAY, day)
					).split(System.lineSeparator()))
					.collect(Collectors.toCollection(ArrayList::new));
			mealIDs.forEach(mealID -> {
				if (!mealID.isBlank()) System.out.println(
					sqlExecutor.execute(
						Queries.getSelectFieldByValueStatement(TABLE_MEALS, COLUMN_MEAL_NAME,
							COLUMN_MEAL_ID, mealID
						)));
			});
		});
	}

	private String showPlanPrintQuestionScanAnswer(String keyWord) {
		showWeeklyPlan();
		System.out.print("Which day to " + keyWord + " (1-7 - Monday-Sunday): ");
		return getScanner().nextLine();
	}

	private String getDayNameOrNull(String dayNumber) {
		if (dayNumber.matches("\\d") && Integer.parseInt(dayNumber) >= 1 &&
		    Integer.parseInt(dayNumber) <= 7) return daysOfWeek.get(Integer.parseInt(dayNumber) - 1);
		else System.out.println("invalid day number. try 1-7");
		return null;
	}

	private String getDayName(String keyword) {
		return getDayNameOrNull(showPlanPrintQuestionScanAnswer(keyword));
	}

	private void editDay() {
		String day = getDayName("edit");
		if (day != null) {
			System.out.println(sqlExecutor.execute(
				Queries.getSelectRowStatement(TABLE_WEEKLY_PLAN,
					COLUMN_DAY, day
				)));
			System.out.println("Choose meals for this day (IDs in csv format): ");
			mealMenu.showMealsOption();
			String meals = getScanner().nextLine();
			ArrayList<String> mealsAL =
				Arrays.stream(meals.split(",")).collect(Collectors.toCollection(ArrayList::new));
			for (String meal : mealsAL) {
				sqlExecutor.execute(
					Queries.getInsertIntoStatement(TABLE_WEEKLY_PLAN,
						List.of(COLUMN_DAY, COLUMN_MEAL_ID),
						List.of(day, meal)
					));
			}
		}
	}

	private void copyDay() {
		String dayToCopy = getDayName("copy");
		String dayToPasteTo = getDayName("paste to");
		if (dayToCopy != null && dayToPasteTo != null) {
			ArrayList<String> mealIDs =
				Arrays.stream(sqlExecutor.execute(Queries.getSelectFieldByValueStatement(
						TABLE_WEEKLY_PLAN, COLUMN_MEAL_ID, COLUMN_DAY, dayToCopy)).split(System.lineSeparator()))
					.filter(a -> !a.isBlank())
					.collect(Collectors.toCollection(ArrayList::new));
			sqlExecutor.execute(
				Queries.getDeleteRowStatement(TABLE_WEEKLY_PLAN, COLUMN_DAY,
					dayToPasteTo
				));
			mealIDs.forEach(
				mealID -> sqlExecutor.execute(
					Queries.getInsertIntoStatement(TABLE_WEEKLY_PLAN,
						List.of(COLUMN_DAY, COLUMN_MEAL_ID),
						List.of(dayToPasteTo, mealID)
					)));

		}
	}

	private void clearDay() {
		String day = getDayName("clear");
		if (day != null) {
			sqlExecutor.execute(
				Queries.getDeleteRowStatement(TABLE_WEEKLY_PLAN, COLUMN_DAY, day));
		}
	}

	private void clearWeek() {
		sqlExecutor.execute(Queries.getClearTableStatement(TABLE_WEEKLY_PLAN));
	}
}
