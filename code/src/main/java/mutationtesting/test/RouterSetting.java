package mutationtesting.test;

public class RouterSetting {
    private final String name;
    private final String[] targets;//this could be not needed if we rely on the order. However, I fell that it is more robust in this way.
    private final double[] probabilities;

    public RouterSetting(String name, String[] targets, double[] probabilities) {
        this.name = name;
        this.targets = targets;
        this.probabilities = probabilities;
    }

    public String getName() {
        return name;
    }

    public String[] getTargets() {
        return targets;
    }

    public double[] getProbabilities() {
        return probabilities;
    }
}
