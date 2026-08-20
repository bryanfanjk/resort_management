package control;

import adt.QueueInterface;
import adt.VipQueue;
import entity.Customer;

/**
 * Author: <Your Name Here>
 *
 * VipAllocationController is Module 2's control class. It owns the VIP
 * queue and exposes a small service interface for WalkInController
 * (Module 1's control class) to call into - this is what keeps the
 * module boundary real in code, not just conceptual: WalkInController
 * never touches VipQueue directly, only through these methods.
 *
 * No I/O here at all, per ECB rules for control classes.
 */
public class VipAllocationController {

    private final QueueInterface<Customer> vipQueue;

    public VipAllocationController() {
        this.vipQueue = new VipQueue<>();
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
}
