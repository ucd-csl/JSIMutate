package mutationtesting;

import mutationoperators.MutationOperator;
import mutationtesting.test.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import qn.QueueNetwork;
import qn.QueueNetworkParser;
import qn.Server;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TestExecutor {

    public static Document applyTest(String modelPath, QnTest qnTest) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(modelPath);
        return applyTest(document, qnTest);
    }

    public static Document applyTest(Document document, QnTest qnTest) throws JDOMException, IOException {
        Document clone = document.clone();
        Element root = clone.getRootElement();
        Element sim = root.getChild("sim");
        List<Element> nodes = sim.getChildren("node");
        for (OpenClass openClass : qnTest.getOpenClasses()) {
            String className = openClass.getName();
            String referenceStation = openClass.getReferenceStation();
            Element referenceStationNode = null;
            Optional<Element> selectedElement = nodes.stream().filter(f -> f.getAttributeValue("name").equals(referenceStation)).findFirst();
            if (selectedElement.isPresent()) {
                referenceStationNode = selectedElement.get();
            } else {
                throw new Error(referenceStation + " not found");
            }
            Element randomSourceNode = MutationOperator.getSectionFromNode(referenceStationNode, "RandomSource");
            List<Element> childrenServiceStrategy = randomSourceNode.getChildren("parameter").stream().filter(p -> p.getAttributeValue("name").equals("ServiceStrategy")).findFirst().orElse(null).getChildren();
            Element selectedSubParameter = null;
            for (int i = 0; i < childrenServiceStrategy.size() - 1; i++) {
                if (childrenServiceStrategy.get(i).getText().equals(className)) {
                    selectedSubParameter = childrenServiceStrategy.get(i + 1);
                    break;
                }
            }
            Element distrPar = selectedSubParameter.getChildren("subParameter").stream().filter(s -> s.getAttributeValue("name").equals("distrPar")).findFirst().orElse(null);
            if (openClass instanceof OpenClassExponential) {
                OpenClassExponential openClassExp = (OpenClassExponential) openClass;
                setValues(distrPar, new String[]{"lambda"}, new double[]{openClassExp.getInterarrivaldistributionLambda()});
            } else if (openClass instanceof OpenClassHyperExponential) {
                OpenClassHyperExponential openClassHyperExp = (OpenClassHyperExponential) openClass;
                setValues(distrPar, new String[]{"p", "lambda1", "lambda2"}, new double[]{openClassHyperExp.getP(), openClassHyperExp.getLambda1(), openClassHyperExp.getLambda2()});
            } else if (openClass instanceof OpenClassDeterministic) {
                OpenClassDeterministic openClassDet = (OpenClassDeterministic) openClass;
                setValues(distrPar, new String[]{"t"}, new double[]{openClassDet.getT()});
            } else if (openClass instanceof OpenClassUniform) {
                OpenClassUniform openClassUn = (OpenClassUniform) openClass;
                setValues(distrPar, new String[]{"min", "max"}, new double[]{openClassUn.getMin(), openClassUn.getMax()});
            } else if (openClass instanceof OpenClassGamma) {
                OpenClassGamma openClassGamma = (OpenClassGamma) openClass;
                setValues(distrPar, new String[]{"alpha", "beta"}, new double[]{openClassGamma.getAlpha(), openClassGamma.getBeta()});
            } else if (openClass instanceof OpenClassNormal) {
                OpenClassNormal openClassNormal = (OpenClassNormal) openClass;
                setValues(distrPar, new String[]{"mean", "standardDeviation"}, new double[]{openClassNormal.getMean(), openClassNormal.getStandardDeviation()});
            } else if (openClass instanceof OpenClassCoxian) {
                OpenClassCoxian openClassCoxian = (OpenClassCoxian) openClass;
                setValues(distrPar, new String[]{"lambda0", "lambda1", "phi0"}, new double[]{openClassCoxian.getLambda0(), openClassCoxian.getLambda1(), openClassCoxian.getPhi0()});
            }
        }
        Set<ClosedClass> closedClasses = qnTest.getClosedClasses();
        if (closedClasses.size() > 0) {
            List<Element> userClasses = sim.getChildren("userClass");
            List<Element> stationPopulations = sim.getChild("preload").getChildren("stationPopulations");
            for (ClosedClass closedClass : closedClasses) {
                String className = closedClass.getName();
                String referenceStation = closedClass.getReferenceStation();

                String newPopulation = String.valueOf(closedClass.getPopulation());
                Element userClass = userClasses.stream().filter(u -> u.getAttributeValue("name").equals(className)).findFirst().orElse(null);
                userClass.setAttribute("customers", newPopulation);
                Element stationPopulation = stationPopulations.stream().filter(u -> u.getAttributeValue("stationName").equals(referenceStation)).findFirst().orElse(null);
                Optional<Element> matchingClassPopulation = stationPopulation.getChildren("classPopulation").stream().filter(e -> e.getAttributeValue("refClass").equals(className)).findFirst();
                assert matchingClassPopulation.isPresent();
                matchingClassPopulation.get().setAttribute("population", newPopulation);

                String thinkingTime = String.valueOf(closedClass.getThinkingTime());
                //TODO apply thinking time in referenceStation
                Element referenceStationNode = nodes.stream().filter(n -> n.getAttributeValue("name").equals(referenceStation)).findFirst().orElse(null);
                Element delay = referenceStationNode.getChildren("section").stream().filter(n -> n.getAttributeValue("className").equals("Delay")).findFirst().orElse(null);
                Element server = referenceStationNode.getChildren("section").stream().filter(n -> n.getAttributeValue("className").equals("Server")).findFirst().orElse(null);
                assert delay == null || server == null;
                Element selected = (delay == null ? server : delay);
                assert selected != null;
            }
        }
        Set<RouterSetting> routerSettings = qnTest.getRouterSettings();
        for (RouterSetting routerSetting : routerSettings) {
            String[] targets = routerSetting.getTargets();
            double[] newProbabilities = routerSetting.getProbabilities();

            Element router = nodes.stream().filter(n -> n.getAttributeValue("name").equals(routerSetting.getName())).findFirst().orElse(null);
            router = MutationOperator.getSectionFromNode(router, "Router");
            Element probabilities = router.getChildren("parameter").stream().filter(c -> c.getAttribute("name").getValue().equals("RoutingStrategy")).findFirst().get().getChildren("subParameter").stream().filter(c -> c.getAttribute("name").getValue().equals("Probabilities")).findFirst().orElse(null);
            Element empiricalEntryArray = probabilities.getChildren("subParameter").stream().filter(c -> c.getAttribute("name").getValue().equals("EmpiricalEntryArray")).findFirst().orElse(null);
            List<Element> empiricalEntries = empiricalEntryArray.getChildren("subParameter").stream().filter(c -> c.getAttribute("name").getValue().equals("EmpiricalEntry")).collect(Collectors.toList());
            for (Element empiricalEntry : empiricalEntries) {
                List<Element> subParameters = empiricalEntry.getChildren("subParameter");
                Element nameElement = subParameters.stream().filter(c -> c.getAttributeValue("name").equals("stationName")).findFirst().orElse(null);
                String name = nameElement.getChild("value").getText();
                for (int i = 0; i < targets.length; i++) {
                    if (targets[i].equals(name)) {
                        Element probabilityElement = subParameters.stream().filter(c -> c.getAttribute("name").getValue().equals("probability")).findFirst().orElse(null);
                        probabilityElement.getChild("value").setText(String.valueOf(newProbabilities[i]));
                        break;
                    }
                }
            }
        }

        addMeasures(document, root, sim);
        return clone;
    }

    private static void addMeasures(Document document, Element root, Element sim) throws JDOMException, IOException {
        Element results = root.getChild("results");
        if (results != null) {
            results.detach();
            root.removeChild("results");
        }

        //0) remove all "measure" tags from "sim", and generates new ones as discussed with Catia
        List<Element> measures = sim.getChildren("measure");
        for (int i = 0; i < measures.size(); i++) {
            measures.get(i).detach();
        }
        sim.removeChildren("measure");

        int index = 0;
        List<Element> simChildren = sim.getChildren();
        Element el = null;
        String elName = null;
        do {
            el = simChildren.get(index);
            elName = el.getName();
            index++;
            //System.out.println(elName);
        }
        while (elName.equals("node") || elName.equals("userClass"));
        index = sim.indexOf(el);

        //1.1) generate "system response time" for all classes
        //<measure alpha="0.01" name="System Response Time" nodeType="" precision="0.03" referenceNode="" referenceUserClass="" type="System Response Time" verbose="false"/>
        Element measure = createMeasure("0.01", "System Response Time", "", "0.03", "", "", "System Response Time");
        sim.addContent(index++, measure);
        //1.2) generate "system response time" for each "user class"
        //<measure alpha="0.01" name="statusCar_System Response Time" nodeType="" precision="0.03" referenceNode="" referenceUserClass="statusCar" type="System Response Time" verbose="false"/>
        List<String> userClasses = sim.getChildren("userClass").stream().map(s -> s.getAttributeValue("name")).collect(Collectors.toList());
        for (String userClass : userClasses) {
            sim.addContent(index++, createMeasure("0.01", "System Response Time", "", "0.03", "", userClass, "System Response Time"));
        }
        //2.1) collect all "server" nodes
        QueueNetwork qn = QueueNetworkParser.parse(document);
        List<String> serversNames = qn.getNodes().stream().filter(n -> n instanceof Server).map(n -> n.getName()).collect(Collectors.toList());

        //2.21) generate utilization for each "server" node and each "user class"
        //<measure alpha="0.01" name="CAR.statusReport_statusCar_Utilization" nodeType="station" precision="0.03" referenceNode="CAR.statusReport" referenceUserClass="statusCar" type="Utilization" verbose="false"/>
        for (String server : serversNames) {
            //<measure alpha="0.01" name="SERVER_Utilization" nodeType="station" precision="0.03" referenceNode="SERVER" referenceUserClass="" type="Utilization" verbose="false" />
            sim.addContent(index++, createMeasure("0.01", server + "_Utilization", "station", "0.03", server, "", "Utilization"));
            for (String userClass : userClasses) {
                sim.addContent(index++, createMeasure("0.01", server + "_" + userClass + "_Utilization", "station", "0.03", server, userClass, "Utilization"));
            }
        }

        //<measure alpha="0.01" name="System Drop Rate" nodeType="" precision="0.03" referenceNode="" referenceUserClass="" type="System Drop Rate" verbose="false"/>
        sim.addContent(index++, createMeasure("0.01", "System Drop Rate", "", "0.03", "", "", "System Drop Rate"));
        //<measure alpha="0.01" name="statusCar_System Drop Rate" nodeType="" precision="0.03" referenceNode="" referenceUserClass="statusCar" type="System Drop Rate" verbose="false"/>
        for (String userClass : userClasses) {
            sim.addContent(index++, createMeasure("0.01", userClass + "_System Drop Rate", "", "0.03", "", userClass, "System Drop Rate"));
        }
        List<Element> servers = qn.getNodes().stream().filter(n -> n instanceof Server).map(n -> n.getXmlElement()).collect(Collectors.toList());
        for (Element server : servers) {
            String serverName = server.getAttributeValue("name");
            String size = MutationOperator.getSectionFromNode(server, "Queue").getChildren("parameter").stream().filter(p -> p.getAttributeValue("name").equals("size")).findFirst().get().getChild("value").getText();
            if (size.equals("-1")) {
                continue;
            }
            //<measure alpha="0.01" name="SERVER_Drop Rate" nodeType="station" precision="0.03" referenceNode="SERVER" referenceUserClass="" type="Drop Rate" verbose="false"/>
            sim.addContent(index++, createMeasure("0.01", serverName + "_Drop Rate", "station", "0.03", serverName, "", "Drop Rate"));
            for (String userClass : userClasses) {
                //<measure alpha="0.01" name="SERVER_statusCar_Drop Rate" nodeType="station" precision="0.03" referenceNode="SERVER" referenceUserClass="statusCar" type="Drop Rate" verbose="false"/>
                sim.addContent(index++, createMeasure("0.01", serverName + "_" + userClass + "_Drop Rate", "station", "0.03", serverName, userClass, "Drop Rate"));
            }
        }
        root.setContent(sim);//is this needed?
    }

    private static void setValues(Element distrPar, String[] subParameter, double[] newValue) {
        List<Element> subparameters = distrPar.getChildren("subParameter");
        for (int i = 0; i < subParameter.length; i++) {
            int finalI = i;
            Element value = subparameters.stream().filter(s -> s.getAttributeValue("name").equals(subParameter[finalI])).findFirst().orElse(null).getChild("value");
            value.setText(String.valueOf(newValue[finalI]));
        }
    }

    private static Element createMeasure(String alpha, String name, String nodeType, String precision, String referenceNode, String referenceUserClass, String type) {
        Element el = new Element("measure");
        el.setAttribute("alpha", alpha);
        el.setAttribute("name", name);
        el.setAttribute("nodeType", nodeType);
        el.setAttribute("precision", precision);
        el.setAttribute("referenceNode", referenceNode);
        el.setAttribute("referenceUserClass", referenceUserClass);
        el.setAttribute("type", type);
        el.setAttribute("verbose", "false");
        return el;
    }
}
