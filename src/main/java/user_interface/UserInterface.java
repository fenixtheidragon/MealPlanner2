package user_interface;

import backend.ExceptionLogger;
import lombok.Getter;
import user_interface.menus.MainMenuSystem;

import java.util.Scanner;

@Getter
public class UserInterface {

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            MainMenuSystem mainMenuSystem = new MainMenuSystem(scanner);
            mainMenuSystem.open();
        } catch (Exception e) {
            ExceptionLogger.logAsError(e, "Catch-all exception:");
        }
    }
}
