package mutationtesting.test;

public class OpenClassExponential extends OpenClass {
    private final double interarrivaldistributionLambda;

    public OpenClassExponential(String name, String referenceStation, double interarrivaldistributionLambda) {
        super(name, referenceStation);
        this.interarrivaldistributionLambda = interarrivaldistributionLambda;
    }

    public double getInterarrivaldistributionLambda() {
        return interarrivaldistributionLambda;
    }
}
