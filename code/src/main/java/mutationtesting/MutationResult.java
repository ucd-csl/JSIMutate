package mutationtesting;

import mutation.Mutation;
import qn.simulation.MeasureResult;
import qn.simulation.SimulationResults;

import java.util.List;
import java.util.Objects;

public class MutationResult {
    private final Mutation mutation;
    private final boolean timeout;
    private final SimulationResults originalResult;
    private SimulationResults mutantResult;
    private final String testName;

    public MutationResult(Mutation mutation, SimulationResults originalResult, SimulationResults mutantResult, String testName) {
        this.mutation = mutation;
        this.originalResult = originalResult;
        this.mutantResult = mutantResult;
        this.timeout = false;
        this.testName = testName;
    }

    public MutationResult(Mutation mutation, String testName, boolean timeout) {
        this.mutation = mutation;
        this.timeout = timeout;
        this.testName = testName;
        this.originalResult = null;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[").append(mutation.getLocation()).append(",").append(mutation.getOperator()).append(",").append(mutation.getDescription()).append("]");
        if (this.originalResult != null) {
            result.append("\t").append(this.originalResult)
                    .append("\t").append(this.mutantResult);
        }
        return result.toString();
    }

    public String printMetrics() {
        String mutantAndLocation = getTestName() + "\t" + mutation.getLocation() + "\t" + mutation.getOperator() + "\t";
        StringBuilder result = new StringBuilder();
        if (mutantResult != null) {
            List<MeasureResult> originalMeasures = originalResult.getMeasuresResults();
            List<MeasureResult> mutantMeasures = mutantResult.getMeasuresResults();
            assert originalMeasures.size() == mutantMeasures.size();
            for (int i = 0; i < originalMeasures.size(); i++) {
                MeasureResult originalMeasure = originalMeasures.get(i);
                MeasureResult mutantMeasure = mutantResult.getMeasuresResults().stream()
                        .filter(mr -> mr.getMeasureType().equals(originalMeasure.getMeasureType())
                                && Objects.equals(originalMeasure.getClassField(), mr.getClassField())
                                && Objects.equals(originalMeasure.getStation(), mr.getStation()))
                        .findFirst().orElse(null);
                result.append(mutantAndLocation)
                        .append(originalMeasure.getFixedPart()).append("\t")
                        .append(originalMeasure.getVariablePart()).append("\t")
                        .append(mutantMeasure.getVariablePart()).append("\t")
                        .append(isTimeout()).append("\t")
                        .append(mutantMeasure != null && mutantMeasure.isSignificantlyDifferent(originalMeasure)).append("\n");
            }
        } else {
            result.append(mutantAndLocation)
                    .append(MeasureResult.getFixedPartNull()).append("\t")
                    .append(MeasureResult.getVariablePartNull()).append("\t")
                    .append(MeasureResult.getVariablePartNull()).append("\t")
                    .append(isTimeout()).append("\t")
                    .append(false).append("\n");
        }
        return result.toString();
    }

    public Mutation getMutation() {
        return mutation;
    }

    public SimulationResults getOriginalResult() {
        return originalResult;
    }

    public SimulationResults getMutantResult() {
        return mutantResult;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public String getTestName() {
        return testName;
    }

    public boolean isKilled() {
        if (originalResult == null) {
            return false;
        }
        for (MeasureResult originalMeasure : originalResult.getMeasuresResults()) {
            MeasureResult mutantMeasure = mutantResult.getMeasuresResults().stream()
                    .filter(mr -> mr.getMeasureType().equals(originalMeasure.getMeasureType())
                            && Objects.equals(originalMeasure.getClassField(), mr.getClassField())
                            && Objects.equals(originalMeasure.getStation(), mr.getStation()))
                    .findFirst().orElse(null);
            if (this.timeout ||
                    (mutantMeasure != null && mutantMeasure.isSignificantlyDifferent(originalMeasure))) {
                return true;
            }
        }
        return false;
    }

    public boolean isKilledWithMeasure(String measure) {
        if (originalResult == null) {
            return false;
        }
        String measureType = measure.split("_")[0];
        String classField = measure.split("_")[1];
        String station = measure.substring(measureType.length() + classField.length() + 2);
        MeasureResult originalMeasure = originalResult.getMeasuresResults().stream()
                .filter(mr -> mr.getMeasureType().equals(measureType)
                        && Objects.equals(classField, mr.getClassField())
                        && Objects.equals(station, mr.getStation()))
                .findFirst().orElse(null);
        MeasureResult mutantMeasure = mutantResult.getMeasuresResults().stream()
                .filter(mr -> mr.getMeasureType().equals(measureType)
                        && Objects.equals(classField, mr.getClassField())
                        && Objects.equals(station, mr.getStation()))
                .findFirst().orElse(null);
        return this.timeout ||
                (originalMeasure != null && mutantMeasure != null
                        && mutantMeasure.isSignificantlyDifferent(originalMeasure));
    }
}
