package jsimutate.inputfile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import mutationtesting.test.QnTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InputFile {
    private String jsimgFile;

    @JsonProperty("workloads")
    private final List<Workload> workloads = new ArrayList<>();


    public static InputFile readInputFile(String inputFileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(inputFileName), InputFile.class);
    }

    public String getJsimgFile() {
        return jsimgFile;
    }

    public void setJsimgFile(String jsimgFile) {
        this.jsimgFile = jsimgFile;
    }

    public List<Workload> getWorkloads() {
        return workloads;
    }

    public Set<QnTest> buildTestSuite() {
        Set<QnTest> testSuite = new LinkedHashSet<>();
        for (Workload testInputFile : this.getWorkloads()) {
            QnTest qnTest = new QnTest(testInputFile.getWorkloadID());
            List<ClosedClass> closedClasses = testInputFile.getClosedClasses();
            if (closedClasses != null) {
                for (ClosedClass t : closedClasses) {
                    qnTest.addClosedClass(t.getName(), t.getPopulation(), t.getReferenceStation(), t.getThinkingTime());
                }
            }
            List<OpenClassCoxian> openClassesCoxian = testInputFile.getOpenClassesCoxian();
            if (openClassesCoxian != null) {
                for (OpenClassCoxian t : openClassesCoxian) {
                    qnTest.addOpenClassCoxian(t.getName(), t.getReferenceStation(), t.getLambda0(), t.getLambda1(), t.getPhi0());
                }
            }
            List<OpenClassDeterministic> openClassesDeterministic = testInputFile.getOpenClassesDeterministic();
            if (openClassesDeterministic != null) {
                for (OpenClassDeterministic t : openClassesDeterministic) {
                    qnTest.addOpenClassDeterministic(t.getName(), t.getReferenceStation(), t.getT());
                }
            }
            List<OpenClassExponential> openClassesExponential = testInputFile.getOpenClassesExponential();
            if (openClassesExponential != null) {
                for (OpenClassExponential t : openClassesExponential) {
                    qnTest.addOpenClassExponential(t.getName(), t.getReferenceStation(), t.getInterarrivaldistributionLambda());
                }
            }
            List<OpenClassHyperExponential> openClassesHyperExponential = testInputFile.getOpenClassesHyperExponential();
            if (openClassesHyperExponential != null) {
                for (OpenClassHyperExponential t : openClassesHyperExponential) {
                    qnTest.addOpenClassHyperExponential(t.getName(), t.getReferenceStation(), t.getP(), t.getLambda1(), t.getLambda2());
                }
            }
            List<OpenClassGamma> openClassesGamma = testInputFile.getOpenClassesGamma();
            if (openClassesGamma != null) {
                for (OpenClassGamma t : openClassesGamma) {
                    qnTest.addOpenClassGamma(t.getName(), t.getReferenceStation(), t.getAlpha(), t.getBeta());
                }
            }
            List<OpenClassNormal> openClassesNormal = testInputFile.getOpenClassesNormal();
            if (openClassesNormal != null) {
                for (OpenClassNormal t : openClassesNormal) {
                    qnTest.addOpenClassNormal(t.getName(), t.getReferenceStation(), t.getMean(), t.getStandardDeviation());
                }
            }
            List<OpenClassUniform> openClassesUniform = testInputFile.getOpenClassesUniform();
            if (openClassesUniform != null) {
                for (OpenClassUniform t : openClassesUniform) {
                    qnTest.addOpenClassUniform(t.getName(), t.getReferenceStation(), t.getMin(), t.getMax());
                }
            }
            testSuite.add(qnTest);
        }
        return testSuite;
    }
}
