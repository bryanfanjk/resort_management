package entity;

/* author: Ho Jia Ming */
public enum UserRole {
    HOUSEKEEPING_STAFF("Housekeeping Staff"),
    SUPERVISOR("Supervisor");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
