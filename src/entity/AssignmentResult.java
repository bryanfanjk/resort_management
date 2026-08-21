package entity;

import adt.List;

/** Result of one staff-triggered waiting-list assignment attempt. */
public class AssignmentResult {

    private final List<WaitingCustomer> skippedCustomers;
    private final Reservation assignedReservation;

    public AssignmentResult(List<WaitingCustomer> skippedCustomers,
                            Reservation assignedReservation) {
        this.skippedCustomers = skippedCustomers;
        this.assignedReservation = assignedReservation;
    }

    public List<WaitingCustomer> getSkippedCustomers() {
        return skippedCustomers;
    }

    public Reservation getAssignedReservation() {
        return assignedReservation;
    }
}
