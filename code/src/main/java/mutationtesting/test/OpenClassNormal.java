package mutationtesting.test;

public class OpenClassNormal extends OpenClass {
    private final double mean;
    private final double standardDeviation;

    public OpenClassNormal(String name, String referenceStation, double mean, double standardDeviation) {
        super(name, referenceStation);
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    public double getMean() {
        return mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }
}
