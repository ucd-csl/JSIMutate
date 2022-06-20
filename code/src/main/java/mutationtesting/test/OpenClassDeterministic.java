package mutationtesting.test;

public class OpenClassDeterministic extends OpenClass {
    private final double t;

    public OpenClassDeterministic(String name, String referenceStation, double t) {
        super(name, referenceStation);
        this.t = t;
    }

    public double getT() {
        return t;
    }
}
