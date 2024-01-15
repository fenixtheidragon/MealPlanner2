package user_interface.menus;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.Scanner;

@Getter@Slf4j
public class AllMenusSystem {
    private Scanner scanner;
    private MainMenuSystem mainMenuSystem;

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            this.scanner = scanner;
            this.mainMenuSystem = new MainMenuSystem(scanner);
                mainMenuSystem.open();
        } catch (Exception e) {
            log.error("Catch-all: {}", e.getMessage());
        }
    }
}
