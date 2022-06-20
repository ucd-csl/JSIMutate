package mutationtesting.test;

public class ClosedClass extends CustomerClass {
    private final int population;
    private final double thinkingTime;

    public ClosedClass(String name, int population, String referenceStation, double thinkingTime) {
        super(name, referenceStation);
        this.population = population;
        this.thinkingTime = thinkingTime;
    }

    public int getPopulation() {
        return population;
    }

    public double getThinkingTime() {
        return thinkingTime;
    }
}
