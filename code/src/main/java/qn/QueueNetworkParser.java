package qn;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class QueueNetworkParser {

    public static QueueNetwork parse(String modelPath) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(modelPath);
        return parse(doc);
    }

    public static QueueNetwork parse(Document doc) throws JDOMException, IOException {
        Collection<Node> nodes = new ArrayList<Node>();

        Element root = doc.getRootElement();
        Element sim = root.getChild("sim");
        Map<String, Node> mapNameNode = new HashMap<>();
        List<Fork> forks = new ArrayList<Fork>();
        for (Element el : sim.getChildren("node")) {
            Node node = buildNode(el);
            nodes.add(node);
            mapNameNode.put(node.getName(), node);
            if (node instanceof Fork) {
                forks.add((Fork) node);
            }
        }
        for (Element el : sim.getChildren("connection")) {
            String sourceName = el.getAttributeValue("source");
            String targetName = el.getAttributeValue("target");
            Node source = mapNameNode.get(sourceName);
            Node target = mapNameNode.get(targetName);
            source.addChild(target);
            target.addParent(source);
        }
        for (Fork fork : forks) {
            Node node = fork;
            while (true) {
                node = node.getChildren().get(0);
                if (node instanceof Join) {
                    fork.setJoin((Join) node);
                    break;
                }
            }
        }
        return new QueueNetwork(nodes, doc);
    }

    private static Node buildNode(Element el) {
        List<Element> sections = el.getChildren("section");
        List<String> sectionClassNames = sections.stream().map(s -> s.getAttributeValue("className")).collect(Collectors.toList());
        String name = el.getAttributeValue("name");
        boolean hasRandomSource = sectionClassNames.contains("RandomSource");
        if (hasRandomSource) {
            return new Source(name, el);
        }
        boolean hasFork = sectionClassNames.contains("Fork");
        if (hasFork) {
            return new Fork(name, el);
        }
        boolean hasJoin = sectionClassNames.contains("Join");
        if (hasJoin) {
            return new Join(name, el);
        }
        boolean hasSink = sectionClassNames.contains("JobSink");
        if (hasSink) {
            return new Sink(name, el);
        }
        boolean hasDelay = sectionClassNames.contains("Delay");
        if (hasDelay) {
            return new Delay(name, el);
        }
        boolean hasRouter = sectionClassNames.contains("Router");
        boolean hasQueue = sectionClassNames.contains("Queue");
        boolean hasServer = sectionClassNames.contains("Server");
        if (hasQueue && hasRouter && hasServer) {
            Element server = sections.stream().filter(s -> s.getAttributeValue("className").equals("Server")).findFirst().get();
            Element serviceStrategy = server.getChildren("parameter").stream().filter(s -> s.getAttributeValue("name").equals("ServiceStrategy")).findFirst().get();
            if (serviceStrategy.getChildren("subParameter").stream().allMatch(s -> s.getAttributeValue("name").equals("ZeroServiceTimeStrategy"))) {
                return new SimpleQueue(name, el);
            } else {
                return new Server(name, el);
            }
        }
        if (hasRouter && hasQueue) {
            return new Router(name, el);
        }

        System.err.println("Unknown node type, using generic Node. XML:\n" + new XMLOutputter().outputString(el));
        return new Node(name, el);
    }

}
