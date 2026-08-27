package entity;

import adt.ListInterface;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/* author: Ho Jia Ming */
public class HousekeepingReport {
    private ListInterface<HousekeepingLog> logs;
    private ListInterface<Room> rooms;
    private String generatedDate;
    private int totalStatusChanges;
    private int totalRoomsCleaned;
    private double averageCleaningTime;

    public HousekeepingReport(ListInterface<HousekeepingLog> logs, 
                              ListInterface<Room> rooms) {
        this.logs = logs;
        this.rooms = rooms;
        // Use LocalDateTime for date and time
        this.generatedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        calculateMetrics();
    }

    private void calculateMetrics() {
        totalStatusChanges = logs.size();
        
        // Count rooms that have been cleaned (reached READY status at least once)
        int cleanedCount = 0;
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.getRoomStatus() == RoomStatus.READY) {
                cleanedCount++;
            }
        }
        totalRoomsCleaned = cleanedCount;
        
        // Calculate average cleaning time (simplified - based on status changes per room)
        if (rooms.size() > 0) {
            averageCleaningTime = (double) totalStatusChanges / rooms.size();
        }
    }

    public ListInterface<HousekeepingLog> getLogs() {
        return logs;
    }

    public ListInterface<Room> getRooms() {
        return rooms;
    }

    public String getGeneratedDate() {
        return generatedDate;
    }

    public int getTotalStatusChanges() {
        return totalStatusChanges;
    }

    public int getTotalRoomsCleaned() {
        return totalRoomsCleaned;
    }

    public double getAverageCleaningTime() {
        return averageCleaningTime;
    }

    // Statistical breakdown by status
    public int getRoomCountByStatus(RoomStatus status) {
        int count = 0;
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomStatus() == status) {
                count++;
            }
        }
        return count;
    }

    // Statistical breakdown by staff member
    public int getLogCountByStaff(String staffName) {
        int count = 0;
        for (int i = 0; i < logs.size(); i++) {
            if (logs.get(i).getStaffName().equalsIgnoreCase(staffName)) {
                count++;
            }
        }
        return count;
    }
}