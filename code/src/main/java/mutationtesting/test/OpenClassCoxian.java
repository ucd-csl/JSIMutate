package mutationtesting.test;

public class OpenClassCoxian extends OpenClass {
    private final double lambda0;
    private final double lambda1;
    private final double phi0;

    public OpenClassCoxian(String name, String referenceStation, double lambda0, double lambda1, double phi0) {
        super(name, referenceStation);
        this.lambda0 = lambda0;
        this.lambda1 = lambda1;
        this.phi0 = phi0;
    }

    public double getLambda0() {
        return lambda0;
    }

    public double getLambda1() {
        return lambda1;
    }

    public double getPhi0() {
        return phi0;
    }
}
