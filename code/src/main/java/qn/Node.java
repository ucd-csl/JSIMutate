package qn;

import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final String name;
    private final List<Node> parents;
    private final Element xmlElement;
    protected List<Node> children;

    public Node(String name, Element xmlElement) {
        this.name = name;
        this.xmlElement = xmlElement;
        parents = new ArrayList<Node>();
        children = new ArrayList<Node>();
    }

    public String getName() {
        return name;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public void addParent(Node parent) {
        parents.add(parent);
    }

    public List<Node> getChildren() {
        return children;
    }

    public List<Node> getParents() {
        return parents;
    }

    public Element getXmlElement() {
        return xmlElement;
    }
}
