package user_interface;

import user_interface.menus.AllMenusSystem;

public class UserInterface {
    private AllMenusSystem allMenusSystem;

    public UserInterface() {
        this.allMenusSystem = new AllMenusSystem();
    }

    public void start() {
        allMenusSystem.start();
    }
}
