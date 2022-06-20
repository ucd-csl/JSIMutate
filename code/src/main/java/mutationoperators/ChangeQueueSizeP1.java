package mutationoperators;

import mutation.Mutation;
import mutation.MutationContext;
import org.jdom2.Document;
import org.jdom2.Element;
import qn.QueueNetwork;
import qn.Server;

import java.util.stream.Collectors;

public class ChangeQueueSizeP1 extends MutationOperator {

    public ChangeQueueSizeP1(MutationContext context) {
        super(context);
    }

    @Override
    protected Document mutate(QueueNetwork qn) {
        Document clone = qn.getDocument().clone();
        for (Server queue : qn.getNodes().stream().filter(n -> n instanceof Server).map(n -> (Server) n).collect(Collectors.toList())) {
            Element queueElement = getSectionFromNode(clone, queue.getName(), "Queue");
            Element valueElement = queueElement.getChildren("parameter").stream()
                    .filter(p -> p.getAttributeValue("name").equals("size"))
                    .findFirst().get().getChild("value");
            int oldSize = Integer.parseInt(valueElement.getText());
            int newSize = oldSize + 1;
            String description = "Change queue size from " + oldSize + " to " + newSize;
            String operator = this.getClass().getSimpleName();
            Mutation mutation = new Mutation(queue.getName(), operator, description);
            context.registerMutation(mutation);
            if (context.shouldMutate(mutation)) {
                valueElement.setText(String.valueOf(newSize));
            }
        }
        return clone;
    }
}
