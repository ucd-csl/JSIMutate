package mutationoperators;

import mutation.Mutation;
import mutation.MutationContext;
import org.jdom2.Document;
import org.jdom2.Element;
import qn.QueueNetwork;
import qn.Server;

import java.util.stream.Collectors;

public class ChangeNumberServersP1 extends MutationOperator {

    public ChangeNumberServersP1(MutationContext context) {
        super(context);
    }

    @Override
    protected Document mutate(QueueNetwork qn) {
        Document clone = qn.getDocument().clone();
        for (Server server : qn.getNodes().stream().filter(n -> n instanceof Server).map(n -> (Server) n).collect(Collectors.toList())) {
            Element serverElement = getSectionFromNode(clone, server.getName(), "Server");
            Element valueElement = serverElement.getChildren("parameter").stream()
                    .filter(p -> p.getAttributeValue("name").equals("maxJobs"))
                    .findFirst().get().getChild("value");
            int oldNumberServers = Integer.parseInt(valueElement.getText());
            int newNumberServers = oldNumberServers + 1;
            String description = "Change number of servers from " + oldNumberServers + " to " + newNumberServers;
            String operator = this.getClass().getSimpleName();
            Mutation mutation = new Mutation(server.getName(), operator, description);
            context.registerMutation(mutation);
            if (context.shouldMutate(mutation)) {
                valueElement.setText(String.valueOf(newNumberServers));
            }
        }
        assert clone != null;
        return clone;
    }
}
