package user_interface;

import backend.ExceptionLogger;
import lombok.Getter;
import user_interface.menus.MainMenuSystem;

import java.util.Scanner;

@Getter
public class UserInterface {
    private Scanner scanner;
    private MainMenuSystem mainMenuSystem;

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            this.scanner = scanner;
            this.mainMenuSystem = new MainMenuSystem(scanner);
            mainMenuSystem.open();
        } catch (Exception e) {
            ExceptionLogger.getExceptionStackAsString(e, "Catch-all exception:");
        }
    }
}
