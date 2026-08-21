package entity;

import adt.List;
import adt.ListInterface;

public class HousekeepingPerformanceReport {

    private ListInterface<StaffPerformance> staffPerformances;
    private int totalActions;
    private int staffCount;
    private double averageActionsPerStaff;
    private String mostActiveStaff;
    private int maxActions;
    private String leastActiveStaff;
    private int minActions;
    private int[] statusChangeCounts; // Index corresponds to status sequence
    private String generatedDate;

    public HousekeepingPerformanceReport() {
        this.staffPerformances = new List<>(20);
        this.statusChangeCounts = new int[4]; // 4 statuses
        this.totalActions = 0;
        this.staffCount = 0;
        this.generatedDate = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public void addStaffPerformance(StaffPerformance staff) {
        staffPerformances.add(staff);
        totalActions += staff.getActionCount();
        staffCount++;

        // Update status change counts
        for (int i = 0; i < staff.getStatusChangeCounts().length; i++) {
            statusChangeCounts[i] += staff.getStatusChangeCounts()[i];
        }
    }

    public void calculateMetrics() {
        if (staffPerformances.size() == 0) {
            return;
        }

        averageActionsPerStaff = (double) totalActions / staffCount;

        // Find most and least active
        StaffPerformance mostActive = staffPerformances.get(0);
        StaffPerformance leastActive = staffPerformances.get(0);

        for (int i = 0; i < staffPerformances.size(); i++) {
            StaffPerformance staff = staffPerformances.get(i);
            if (staff.getActionCount() > mostActive.getActionCount()) {
                mostActive = staff;
            }
            if (staff.getActionCount() < leastActive.getActionCount()) {
                leastActive = staff;
            }
        }

        mostActiveStaff = mostActive.getStaffName();
        maxActions = mostActive.getActionCount();
        leastActiveStaff = leastActive.getStaffName();
        minActions = leastActive.getActionCount();
    }

    public ListInterface<StaffPerformance> getSortedStaffList() {
        // Sort by action count (highest first) by default
        ListInterface<StaffPerformance> sorted = new List<>(staffPerformances.size());
        for (int i = 0; i < staffPerformances.size(); i++) {
            sorted.add(staffPerformances.get(i));
        }

        // Selection sort by action count (descending)
        for (int i = 0; i < sorted.size() - 1; i++) {
            int maxIdx = i;
            for (int j = i + 1; j < sorted.size(); j++) {
                if (sorted.get(j).getActionCount() > sorted.get(maxIdx).getActionCount()) {
                    maxIdx = j;
                }
            }
            if (maxIdx != i) {
                StaffPerformance temp = sorted.get(i);
                sorted.set(i, sorted.get(maxIdx));
                sorted.set(maxIdx, temp);
            }
        }
        return sorted;
    }

    public int getTotalActions() {
        return totalActions;
    }

    public int getStaffCount() {
        return staffCount;
    }

    public double getAverageActionsPerStaff() {
        return averageActionsPerStaff;
    }

    public String getMostActiveStaff() {
        return mostActiveStaff;
    }

    public int getMaxActions() {
        return maxActions;
    }

    public String getLeastActiveStaff() {
        return leastActiveStaff;
    }

    public int getMinActions() {
        return minActions;
    }

    public int getStatusChangeCount(HousekeepingStatus status) {
        return statusChangeCounts[status.getSequenceNumber() - 1];
    }

    public String getGeneratedDate() {
        return generatedDate;
    }
}
