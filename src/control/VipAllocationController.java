package control;

import adt.VipQueue;
import dao.VipCodeData;
import entity.Customer;
import adt.ListInterface;

/**
 * Author: <Your Name Here>
 *
 * VipAllocationController is Module 2's control class. It owns the VIP
 * queue AND the VIP verification codes - code verification is
 * inherently VIP-specific logic, so it belongs here, not in
 * WalkInController, keeping the module boundary real in code.
 *
 * No I/O here at all, per ECB rules for control classes.
 */
public class VipAllocationController {

    private final ListInterface<Customer> vipQueue;
    private final String[] validVipCodes;

    public VipAllocationController() {
        this.vipQueue = new VipQueue<>();
        this.validVipCodes = VipCodeData.createValidVipCodes();
    }

    public void registerVip(Customer customer) {
        vipQueue.enqueue(customer);
    }

    public Customer getNextVip() {
        return vipQueue.dequeue();
    }

    public Customer peekNextVip() {
        return vipQueue.peekFront();
    }

    public boolean hasWaitingVip() {
        return !vipQueue.isEmpty();
    }

    public int waitingVipCount() {
        return vipQueue.size();
    }

    /**
     * Checks a manually-entered code against the hardcoded valid-code
     * list. Exact match only, case-sensitive as entered - no retry
     * loop here, single-shot check, matching the confirmed design.
     */
    public boolean isValidVipCode(String code) {
        if (code == null) {
            return false;
        }
        for (String validCode : validVipCodes) {
            if (validCode.equals(code)) {
                return true;
            }
        }
        return false;
    }
}

