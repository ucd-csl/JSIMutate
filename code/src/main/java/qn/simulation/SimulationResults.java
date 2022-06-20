package qn.simulation;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimulationResults {
    private final List<MeasureResult> measuresResults;

    public SimulationResults() {
        measuresResults = new ArrayList<>();
    }

    public static SimulationResults buildSimulationResults(String fileName) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(fileName + "-result.jsim");
        Element root = doc.getRootElement();
        SimulationResults sr = new SimulationResults();
        for (Element xmlMeasureResult : root.getChildren()) {
            sr.measuresResults.add(MeasureResult.buildMeasureResult(xmlMeasureResult));
        }
        return sr;
    }

    public static boolean compare(SimulationResults sr1, SimulationResults sr2, double delta) {
        //TODO
        return true;
    }

    public List<MeasureResult> getMeasuresResults() {
        return measuresResults;
    }
}

