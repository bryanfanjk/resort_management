package entity;

/**
 * Author: <Your Name Here>
 *
 * Top-level customer classification. Deliberately kept as just these two
 * values PERMANENTLY - this does NOT get extended into tiers later.
 * Phase B introduces a separate VipTier enum that only applies to
 * customers whose CustomerType is VIP; CustomerType itself never changes.
 */
public enum CustomerType {
    STANDARD,
    VIP
}
