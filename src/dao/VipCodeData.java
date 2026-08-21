package dao;

/**
 * Author: <Your Name Here>
 *
 * Hardcoded VIP verification codes - deliberately just codes, no names
 * or other details attached. This is the "VIP list" used purely as a
 * yes/no verification gate during manual Walk-In: a manually-entered
 * customer's own typed name becomes their Customer record's name
 * regardless of whether their code matches; this list only answers
 * "is this code valid?"
 */
public final class VipCodeData {

    private VipCodeData() {
        // static utility class - never instantiated
    }

    public static String[] createValidVipCodes() {
        return new String[]{
            "VIP100",
            "VIP200",
            "VIP300"
        };
    }
}
