package mutationtesting.test;

public class OpenClassUniform extends OpenClass {
    private final double min;
    private final double max;

    public OpenClassUniform(String name, String referenceStation, double min, double max) {
        super(name, referenceStation);
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
