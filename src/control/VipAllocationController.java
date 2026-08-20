package control;

import adt.VipList;
import dao.VipCodeData;
import entity.Customer;

public class VipAllocationController {

    private final VipList<Customer> vipList;
    private final String[] validVipCodes;

    public VipAllocationController() {
        this.vipList = new VipList<>();
        this.validVipCodes = VipCodeData.createValidVipCodes();
    }

    public void registerVip(Customer customer) {
        if (customer != null) {
            vipList.add(customer);
        }
    }

    public boolean hasWaitingVip() {
        return !vipList.isEmpty();
    }

    public int waitingVipCount() {
        return vipList.size();
    }

    public VipList<Customer> getVipList() {
        return vipList;
    }

    public Customer peekNextVip() {
        if (vipList.isEmpty()) {
            return null;
        }
        return vipList.get(0);
    }

    public Customer getNextVip() {
        if (vipList.isEmpty()) {
            return null;
        }
        return vipList.remove(0);
    }

    public boolean removeVip(Customer customer) {
        return vipList.removeItem(customer);
    }

    public boolean isValidVipCode(String code) {
        if (code == null) {
            return false;
        }

        String trimmed = code.trim();

        for (String validCode : validVipCodes) {
            if (validCode.equals(trimmed)) {
                return true;
            }
        }

        return false;
    }
}