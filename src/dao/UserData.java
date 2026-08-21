package dao;

import entity.User;
import entity.UserRole;

/* author: Ho Jia Ming */
public final class UserData {

    private UserData() {
    }

    public static User[] createUsers() {
        return new User[]{
            // 3 Housekeeping Staff
            new User("staff1", "staff123", UserRole.HOUSEKEEPING_STAFF),
            new User("staff2", "staff456", UserRole.HOUSEKEEPING_STAFF),
            new User("staff3", "staff789", UserRole.HOUSEKEEPING_STAFF),
            // 2 Supervisors
            new User("supervisor1", "sup123", UserRole.SUPERVISOR),
            new User("supervisor2", "sup456", UserRole.SUPERVISOR)
        };
    }

    public static User findUser(String username, String password) {
        User[] users = createUsers();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)
                    && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
