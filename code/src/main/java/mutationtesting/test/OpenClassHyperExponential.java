package mutationtesting.test;

public class OpenClassHyperExponential extends OpenClass {
    private final double p;
    private final double lambda1;
    private final double lambda2;

    public OpenClassHyperExponential(String name, String referenceStation, double p, double lambda1, double lambda2) {
        super(name, referenceStation);
        this.p = p;
        this.lambda1 = lambda1;
        this.lambda2 = lambda2;
    }

    public double getP() {
        return p;
    }

    public double getLambda1() {
        return lambda1;
    }

    public double getLambda2() {
        return lambda2;
    }
}
