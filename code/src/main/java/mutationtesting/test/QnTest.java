package mutationtesting.test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class QnTest {
    private final Set<OpenClass> openClasses;
    private final Set<ClosedClass> closedClasses;
    private final Set<RouterSetting> routerSettings;
    private String testName;

    public QnTest() {
        openClasses = new HashSet<>();
        closedClasses = new HashSet<>();
        routerSettings = new HashSet<>();
    }

    public QnTest(String testName) {
        this();
        this.testName = testName;
    }

    public void addOpenClassExponential(String name, String referenceStation, double interarrivaldistributionLambda) {
        openClasses.add(new OpenClassExponential(name, referenceStation, interarrivaldistributionLambda));
    }

    public void addOpenClassHyperExponential(String name, String referenceStation, double p, double lambda1, double lambda2) {
        openClasses.add(new OpenClassHyperExponential(name, referenceStation, p, lambda1, lambda2));
    }

    public void addOpenClassDeterministic(String name, String referenceStation, double t) {
        openClasses.add(new OpenClassDeterministic(name, referenceStation, t));
    }

    public void addOpenClassNormal(String name, String referenceStation, double mean, double standardDeviation) {
        openClasses.add(new OpenClassNormal(name, referenceStation, mean, standardDeviation));
    }

    public void addOpenClassGamma(String name, String referenceStation, double alfa, double beta) {
        openClasses.add(new OpenClassGamma(name, referenceStation, alfa, beta));
    }

    public void addOpenClassCoxian(String name, String referenceStation, double lambda0, double lambda1, double phi0) {
        openClasses.add(new OpenClassCoxian(name, referenceStation, lambda0, lambda1, phi0));
    }

    public void addOpenClassUniform(String name, String referenceStation, double min, double max) {
        openClasses.add(new OpenClassUniform(name, referenceStation, min, max));
    }

    public void addClosedClass(String name, int population, String referenceStation, double thinkingTime) {
        closedClasses.add(new ClosedClass(name, population, referenceStation, thinkingTime));
    }

    public void addRouterSetting(String name, String[] targets, double[] priorities) {
        routerSettings.add(new RouterSetting(name, targets, priorities));
    }

    public String getTestName() {
        return testName;
    }

    public Set<OpenClass> getOpenClasses() {
        return openClasses;
    }

    public Set<ClosedClass> getClosedClasses() {
        return closedClasses;
    }

    public Set<RouterSetting> getRouterSettings() {
        return routerSettings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QnTest qnTest = (QnTest) o;
        return testName.equals(qnTest.testName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testName);
    }
}

