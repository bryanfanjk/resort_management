package control;

import adt.VipList;
import dao.VipCodeData;
import entity.Customer;
import entity.WaitingCustomer;

//author: Ng Yung Onn
public class VipAllocationController {

    private final VipList<WaitingCustomer> vipList;
    private final String[] validVipCodes;

    public VipAllocationController() {
        this.vipList = new VipList<>(100);
        this.validVipCodes = VipCodeData.createValidVipCodes();
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

     public void addVip(WaitingCustomer customer) {
        vipList.add(customer);
    }

    public WaitingCustomer getVip(int index) {
        return vipList.get(index);
    }

    public WaitingCustomer removeVip(int index) {
        return vipList.remove(index);
    }

    public WaitingCustomer peekNextVip() {
        if (vipList.isEmpty()) {
            return null;
        }

        return vipList.get(0);
    }

    public boolean hasWaitingVip() {
        return !vipList.isEmpty();
    }

    public int waitingVipCount() {
        return vipList.size();
    }

    public VipList<WaitingCustomer> getVipList() {
        return vipList;
    }
}