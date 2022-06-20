package mutationoperators;

import mutation.Mutation;
import mutation.MutationContext;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import qn.Join;
import qn.Node;
import qn.QueueNetwork;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MutationOperator {

    protected final MutationContext context;

    public MutationOperator(MutationContext context) {
        this.context = context;
    }

    public static Element getSectionFromNode(Element node, String sectionName) {
        return node.getChildren("section").stream().filter(f -> f.getAttributeValue("className").equals(sectionName)).findFirst().get();
    }

    public static Element getLambdaValueElement(Element serverElement) {
        return serverElement.getChildren("parameter").stream()
                .filter(s -> s.getAttributeValue("name").equals("ServiceStrategy")).findFirst().get()
                .getChildren("subParameter").stream()
                .filter(s -> s.getAttributeValue("name").equals("ServiceTimeStrategy")).findFirst().get()
                .getChildren("subParameter").stream()
                .filter(s -> s.getAttributeValue("name").equals("distrPar")).findFirst().get()
                .getChildren("subParameter").stream()
                .filter(s -> s.getAttributeValue("name").equals("lambda")).findFirst().get()
                .getChild("value");
    }

    protected abstract Document mutate(QueueNetwork qn);

    public void identifyMutants(QueueNetwork qn) {
        context.setMutationToApply(null);
        Document mutant = mutate(qn);
        assert mutant != null;
    }

    public Document generateMutant(QueueNetwork qn, Mutation mutation) {
        assert context != null;
        assert mutation != null;
        context.setMutationToApply(mutation);
        Document mutant = mutate(qn);
        assert mutant != null;
        return mutant;
    }

    public MutationContext getContext() {
        return context;
    }

    protected Element getSectionFromNode(Document doc, String nodeName, String sectionName) {
        return doc.getRootElement().getChild("sim")
                .getChildren("node").stream().filter(f -> f.getAttributeValue("name").equals(nodeName)).findFirst().get()
                .getChildren("section").stream().filter(f -> f.getAttributeValue("className").equals(sectionName)).findFirst().get();
    }

    public Element createConnection(String sourceName, String targetName) {
        Element newConnection = new Element("connection");
        Attribute sourceAtt = new Attribute("source", sourceName);
        Attribute targetAtt = new Attribute("target", targetName);
        newConnection.setAttribute(sourceAtt);
        newConnection.setAttribute(targetAtt);
        return newConnection;
    }

    protected Set<Node> getDescendants(Node node, Join join) {
        Set<Node> nodes = new HashSet<>();
        nodes.add(node);
        for (Node child : node.getChildren()) {
            if (!child.equals(join)) {
                nodes.addAll(getDescendants(child, join));
            }
        }
        return nodes;
    }

    protected boolean arePathsIndependent(List<Set<Node>> paths) {
        for (int i = 0; i < paths.size() - 1; i++) {
            Set<Node> setI = new HashSet<>(paths.get(i));
            for (int j = i + 1; j < paths.size(); j++) {
                Set<Node> setJ = new HashSet<>(paths.get(j));
                List<Node> intersection = setI.stream().filter(setJ::contains).collect(Collectors.toList());
                if (intersection.size() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

}
