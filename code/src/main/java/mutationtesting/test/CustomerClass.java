package mutationtesting.test;

public abstract class CustomerClass {
    private final String name;
    private final String referenceStation;

    public CustomerClass(String name, String referenceStation) {
        this.name = name;
        this.referenceStation = referenceStation;
    }

    public String getName() {
        return name;
    }

    public String getReferenceStation() {
        return referenceStation;
    }
}
