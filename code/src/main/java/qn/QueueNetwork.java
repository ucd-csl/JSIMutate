package qn;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

import java.util.ArrayList;
import java.util.Collection;

public class QueueNetwork {
    private final Collection<Node> nodes;
    private Document document;

    public QueueNetwork() {
        nodes = new ArrayList<Node>();
    }

    public QueueNetwork(Collection<Node> nodes, Document document) {
        this.nodes = nodes;
        this.document = document;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public Collection<Node> getNodes() {
        return nodes;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String printXml() {
        XMLOutputter out = new XMLOutputter();
        StringBuilder sb = new StringBuilder();

        Element root = document.getRootElement();
        Element sim = root.getChild("sim");
        String[] header = out.outputString(document).split("\n");
        sb.append(header[0] + "\n" + header[1] + "\n" + header[2] + "\n");
        for (Element uc : sim.getChildren("userClass")) {
            sb.append(out.outputString(uc)).append("\n");
        }
        for (Node node : nodes) {
            sb.append(out.outputString(node.getXmlElement())).append("\n");
        }
        for (Element m : sim.getChildren("measure")) {
            sb.append(out.outputString(m)).append("\n");
        }
        for (Node node : nodes) {
            String name = node.getName();
            for (Node child : node.getChildren()) {
                sb.append("<connection source=\"" + name + "\" target=\"" + child.getName() + "\"/>\n");
            }
        }
        for (Element p : sim.getChildren("preload")) {
            sb.append(out.outputString(p)).append("\n");
        }
        sb.append("</sim>\n");
        for (Element child : root.getChildren()) {
            if (!child.getName().equals("sim")) {
                sb.append(out.outputString(child)).append("\n");
            }
        }
        sb.append("</archive>\n");
        return sb.toString();
    }

}
