package mutation;

public class Mutation {
    private final String location;
    private final String operator;
    private final String description;

    public Mutation(String location, String operator, String description) {
        this.location = location;
        this.operator = operator;
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public String getOperator() {
        return operator;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Mutation)) {
            return false;
        }
        Mutation other = (Mutation) o;
        return location.equals(other.location) && operator.equals(other.operator);
    }
}
