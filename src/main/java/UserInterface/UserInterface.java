package UserInterface;

public class UserInterface {
    private MenuSystem menuSystem;

    public UserInterface() {
        this.menuSystem = new MenuSystem();
    }

    public void start() {
        menuSystem.enter();
    }
}
