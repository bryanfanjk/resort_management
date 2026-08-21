package control;

import dao.UserData;
import entity.User;

public class AuthController {

    private User currentUser;

    public boolean login(String username, String password) {
        User user = UserData.findUser(username, password);
        if (user != null) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isSupervisor() {
        return isLoggedIn() && currentUser.getRole().toString().equals("SUPERVISOR");
    }

    public boolean isHousekeepingStaff() {
        return isLoggedIn() && currentUser.getRole().toString().equals("HOUSEKEEPING_STAFF");
    }
}
