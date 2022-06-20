package qn.simulation;

import jmt.commandline.Jmt;
import jmt.engine.simDispatcher.DispatcherJSIMschema;
import jmt.gui.common.xml.XMLArchiver;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Simulation {

    public static void runSimulation(String fileName) throws Exception {
        jmt.commandline.Jmt.main(new String[]{"sim", fileName});
    }

    public static boolean runSimulation(org.jdom2.Document document) throws Exception {
        return runSimulation(document, ".");
    }

    public static boolean runSimulation(org.jdom2.Document document, String baseFolder) throws Exception {
        return runSimulation(document, baseFolder, -1);
    }

    public static boolean runSimulation(org.jdom2.Document document, String baseFolder, int maxSimTimeSecs) throws Exception {
        String resultFileName = baseFolder + "/" + document.getRootElement().getAttributeValue("name") + "-result.jsim";
        //System.out.println("resultFileName " + resultFileName);
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        String documentAsStr = out.outputString(document);
        InputStream inputStream = new ByteArrayInputStream(documentAsStr.getBytes(StandardCharsets.UTF_8));


        File temp = File.createTempFile("tempfileSim", ".jsim");
        temp.deleteOnExit();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.parse(inputStream);

        File result = new File(resultFileName);
        Map<String, String> options = new HashMap<>();
        if (maxSimTimeSecs > 0) {
            options.put("-maxtime", String.valueOf(maxSimTimeSecs));
        }

        org.w3c.dom.Element sim = XMLArchiver.getSimFromArchiveDocument(doc);
        org.w3c.dom.Document doc2 = db.newDocument();
        Node dup = doc2.importNode(sim, true);
        NamedNodeMap attributes = dup.getAttributes();
        attributes.removeNamedItem("xsi:noNamespaceSchemaLocation");
        doc2.appendChild(dup);
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        Transformer aTransformer = tranFactory.newTransformer();
        Source src = new DOMSource(doc2);
        Result dest = new StreamResult(temp);
        aTransformer.transform(src, dest);
        DispatcherJSIMschema dispatcher = new DispatcherJSIMschema(temp);
        dispatcher.setTerminalSimulation(true);
        if (options.containsKey("-seed")) {
            try {
                dispatcher.setSimulationSeed(Long.parseLong(options.get("-seed")));
            } catch (NumberFormatException var19) {
                System.err.println("Invalid simulation seed. Should be a number.");
                System.exit(1);
            }
        }

        if (options.containsKey("-maxtime")) {
            try {
                String maxTime = options.get("-maxtime");
                dispatcher.setSimulationMaxDuration(Long.parseLong(maxTime) * 1000L);
            } catch (NumberFormatException var18) {
                System.err.println("Invalid maximum simulation time. Should be a number. " + options.get("-maxtime"));
                System.exit(1);
            }
        }

        boolean success = dispatcher.solveModel();
        File output = dispatcher.getOutputFile();
        assert output != null;
        Jmt.copyFile(output, result);
        output.delete();
        return success;
    }

    public static SimulationResults readSimulationResults(String fileName) throws JDOMException, IOException {
        return SimulationResults.buildSimulationResults(fileName);
    }

    public static String readSimulationResultsSystemResponseTime(String fileName) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(fileName + "-result.jsim");
        Element root = doc.getRootElement();
        Element result = root.getChildren().get(0);
        double meanValue = Double.parseDouble(result.getAttributeValue("meanValue"));
        DecimalFormat df = new DecimalFormat("#.####");
        //System.out.println("Average value = " + df.format(meanValue));
        return df.format(meanValue);
    }
}
