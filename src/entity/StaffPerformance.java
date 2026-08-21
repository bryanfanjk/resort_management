package entity;

public class StaffPerformance {

    private String staffName;
    private String staffRole;
    private int actionCount;
    private int[] statusChangeCounts; // Index 0=Dirty, 1=Cleaning, 2=Inspected, 3=Ready

    public StaffPerformance(String staffName, String staffRole) {
        this.staffName = staffName;
        this.staffRole = staffRole;
        this.actionCount = 0;
        this.statusChangeCounts = new int[4];
    }

    public void incrementAction(HousekeepingStatus newStatus) {
        actionCount++;
        statusChangeCounts[newStatus.getSequenceNumber() - 1]++;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getStaffRole() {
        return staffRole;
    }

    public int getActionCount() {
        return actionCount;
    }

    public int[] getStatusChangeCounts() {
        return statusChangeCounts;
    }
}
