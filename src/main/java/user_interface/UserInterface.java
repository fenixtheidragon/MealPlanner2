package user_interface;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import user_interface.menus.MainMenuSystem;

import java.util.Scanner;

@Getter
@Slf4j
public class UserInterface {

	public void start() {
		log.debug("Program started!");
		try (Scanner scanner = new Scanner(System.in)) {
			var mainMenuSystem = new MainMenuSystem(scanner);
			mainMenuSystem.open();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
