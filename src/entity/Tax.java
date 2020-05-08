package entity;

public class Tax {

    private double weight;
    private double fee;

    public Tax(double weight, double fee) {
        this.weight = weight;
        this.fee = fee;
    }

    public double getWeight() {
        return weight;
    }

    public double getFee() {
        return fee;
    }
}
