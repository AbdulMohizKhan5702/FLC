package flc.model;

public class ExerciseType {
    private String name;
    private double price;

    public ExerciseType(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return name + " (£" + String.format("%.2f", price) + ")";
    }
}
