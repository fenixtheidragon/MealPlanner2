package user_interface.menus;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

@Getter
public class AllMenusSystem {
    private final static Logger logger = LoggerFactory.getLogger("MenuSystemLogger");
    private Scanner scanner;
    private MainMenuSystem mainMenuSystem;

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            this.scanner = scanner;
            this.mainMenuSystem = new MainMenuSystem(scanner);
                mainMenuSystem.open();
        } catch (Exception e) {
            logger.error("Catch-all: {}", e.getMessage());
        }
    }
}
