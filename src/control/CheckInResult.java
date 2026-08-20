package control;

import entity.Customer;

/**
 * Author: <Your Name Here>
 *
 * Immutable result of a single WalkInController.checkIn() call. Both
 * STANDARD_NO_CODE and STANDARD_INVALID_CODE route the customer to the
 * Standard queue (confirmed fallback behavior), but they're distinct
 * outcomes so the boundary can print a different message for "no code
 * entered" versus "code entered but didn't match" - one is an expected
 * normal path, the other is worth flagging to staff as a typo/invalid
 * entry.
 */
public class CheckInResult {

    public enum Outcome {
        VIP_REGISTERED,
        STANDARD_NO_CODE,
        STANDARD_INVALID_CODE
    }

    private final Outcome outcome;
    private final Customer customer;

    private CheckInResult(Outcome outcome, Customer customer) {
        this.outcome = outcome;
        this.customer = customer;
    }

    public static CheckInResult vipRegistered(Customer customer) {
        return new CheckInResult(Outcome.VIP_REGISTERED, customer);
    }

    public static CheckInResult standardNoCode(Customer customer) {
        return new CheckInResult(Outcome.STANDARD_NO_CODE, customer);
    }

    public static CheckInResult standardInvalidCode(Customer customer) {
        return new CheckInResult(Outcome.STANDARD_INVALID_CODE, customer);
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public Customer getCustomer() {
        return customer;
    }
}
