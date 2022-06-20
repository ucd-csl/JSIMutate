package mutationoperators;

import mutation.Mutation;
import mutation.MutationContext;
import org.jdom2.Document;
import org.jdom2.Element;
import qn.Node;
import qn.QueueNetwork;
import qn.Server;
import qn.SimpleQueue;

import java.util.List;
import java.util.stream.Collectors;

public class ChangeQueueingStrategy extends MutationOperator {

    public ChangeQueueingStrategy(MutationContext context) {
        super(context);
    }

    @Override
    protected Document mutate(QueueNetwork qn) {
        Document clone = qn.getDocument().clone();
        for (Node node : qn.getNodes().stream().filter(n -> n instanceof SimpleQueue || n instanceof Server).collect(Collectors.toList())) {
            Element queueElement = getSectionFromNode(clone, node.getName(), "Queue");

            Element queuePutStrategyElement = queueElement.getChildren("parameter").stream()
                    .filter(p -> p.getAttributeValue("name").equals("QueuePutStrategy")).findFirst().get();
            Mutation mutation = null;

            String location = node.getName();
            String operator = this.getClass().getSimpleName();

            List<Element> subParameterElements = queuePutStrategyElement.getChildren("subParameter");
            for (Element subParameterElement : subParameterElements) {
                String oldStrategy = subParameterElement.getAttribute("name").getValue();
                switch (oldStrategy) {
                    case "TailStrategy": {
                        if (mutation == null) { //Create and register mutation only once if multiple classes
                            String description = "Change queueing strategy from FCFS to LCFS";
                            mutation = new Mutation(location, operator, description);
                            context.registerMutation(mutation);
                        }
                        if (context.shouldMutate(mutation)) {
                            subParameterElement.setAttribute("name", "HeadStrategy");
                            subParameterElement.setAttribute("classPath", "jmt.engine.NetStrategies.QueuePutStrategies.HeadStrategy");
                        }
                        break;
                    }
                    case "HeadStrategy": {
                        String description = "Change queueing stategy from LCFS to FCFS";
                        if (mutation == null) { //Create and register mutation only once if multiple classes
                            mutation = new Mutation(location, operator, description);
                            context.registerMutation(mutation);
                        }
                        if (context.shouldMutate(mutation)) {
                            subParameterElement.setAttribute("name", "TailStrategy");
                            subParameterElement.setAttribute("classPath", "jmt.engine.NetStrategies.QueuePutStrategies.TailStrategy");
                        }
                        break;
                    }
                }
            }
        }
        return clone;
    }
}
