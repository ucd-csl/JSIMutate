package jsimutate.inputfile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Workload {
    private String workloadID;
    @JsonProperty("closedClasses")
    private List<ClosedClass> closedClasses;
    @JsonProperty("openClassesCoxian")
    private List<OpenClassCoxian> openClassesCoxian;
    @JsonProperty("openClassesDeterministic")
    private List<OpenClassDeterministic> openClassesDeterministic;
    @JsonProperty("openClassesExponential")
    private List<OpenClassExponential> openClassesExponential;
    @JsonProperty("openClassesHyperExponential")
    private List<OpenClassHyperExponential> openClassesHyperExponential;
    @JsonProperty("openClassesGamma")
    private List<OpenClassGamma> openClassesGamma;
    @JsonProperty("openClassesNormal")
    private List<OpenClassNormal> openClassesNormal;
    @JsonProperty("openClassesUniform")
    private List<OpenClassUniform> openClassesUniform;

    public String getWorkloadID() {
        return workloadID;
    }

    public List<ClosedClass> getClosedClasses() {
        return closedClasses;
    }

    public List<OpenClassCoxian> getOpenClassesCoxian() {
        return openClassesCoxian;
    }

    public List<OpenClassDeterministic> getOpenClassesDeterministic() {
        return openClassesDeterministic;
    }

    public List<OpenClassExponential> getOpenClassesExponential() {
        return openClassesExponential;
    }

    public List<OpenClassHyperExponential> getOpenClassesHyperExponential() {
        return openClassesHyperExponential;
    }

    public List<OpenClassGamma> getOpenClassesGamma() {
        return openClassesGamma;
    }

    public List<OpenClassNormal> getOpenClassesNormal() {
        return openClassesNormal;
    }

    public List<OpenClassUniform> getOpenClassesUniform() {
        return openClassesUniform;
    }
}
