package resort.management1;

import boundary.VIPAllocationUI;

/**
 * Author: <Your Name Here>
 *
 * Entry point for the VIP & Loyalty Tier Priority Allocation prototype.
 * Per ECB conventions, main() simply hands control straight to the
 * boundary class - it contains no business logic itself.
 */
public class Main {
    public static void main(String[] args) {
        VIPAllocationUI ui = new VIPAllocationUI();
        ui.start();
    }
}
