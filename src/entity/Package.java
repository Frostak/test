package entity;

import java.text.DecimalFormat;


public class Package implements Comparable<Package> {

    private static DecimalFormat decimalFormatWeight = new DecimalFormat("0.000");
    private static DecimalFormat decimalFormatFee = new DecimalFormat("0.00");

    private double weight;
    private int postalCode;
    private double fee;

    public Package(double weight, int postalCode) {
        this.weight = weight;
        this.postalCode = postalCode;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public String toString(Boolean feeInitialized) {
        return String.format("%05d", postalCode)
                + " " + decimalFormatWeight.format(weight).replace(",", ".")
                + (feeInitialized ? " " + decimalFormatFee.format(fee).replace(",", ".") : "");
    }

    @Override
    public int compareTo(Package o) {
        return (int) (this.getWeight() - o.getWeight());
    }
}
