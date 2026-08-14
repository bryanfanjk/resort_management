/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author NYO
 */
public enum LoyaltyTier {

    STANDARD(1, "Standard"),
    SILVER(2, "Silver"),
    GOLD(3, "Gold"),
    DIAMOND(4, "Diamond");

    private final int rank;
    private final String label;

    LoyaltyTier(int rank, String label) {
        this.rank = rank;
        this.label = label;
    }

    public int getRank() {
        return rank;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
