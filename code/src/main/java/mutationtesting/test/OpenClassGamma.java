package mutationtesting.test;

public class OpenClassGamma extends OpenClass {
    private final double alpha;
    private final double beta;

    public OpenClassGamma(String name, String referenceStation, double alpha, double beta) {
        super(name, referenceStation);
        this.alpha = alpha;
        this.beta = beta;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }
}
