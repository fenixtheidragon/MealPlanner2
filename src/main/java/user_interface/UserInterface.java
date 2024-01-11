package user_interface;

import user_interface.menus.MenuSystem;

public class UserInterface {
    private MenuSystem menuSystem;

    public UserInterface() {
        this.menuSystem = new MenuSystem();
    }

    public void start() {
        menuSystem.enter();
    }
}
