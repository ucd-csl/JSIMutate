package mutationoperators;

import mutation.Mutation;
import mutation.MutationContext;
import org.jdom2.Document;
import qn.QueueNetwork;

import java.util.ArrayList;
import java.util.List;

public class CompoundOperator extends MutationOperator {

    private final List<MutationOperator> operators = new ArrayList<>();

    public CompoundOperator(MutationContext context) {
        super(context);
    }

    public void addOperator(MutationOperator operator) {
        operators.add(operator);
    }

    @Override
    protected Document mutate(QueueNetwork qn) {
        return null;
    }

    @Override
    public void identifyMutants(QueueNetwork qn) {
        for (MutationOperator operator : operators) {
            operator.identifyMutants(qn);
        }
    }

    @Override
    public Document generateMutant(QueueNetwork qn, Mutation mutation) {
        context.setMutationToApply(mutation);
        for (MutationOperator operator : operators) {
            if (mutation.getOperator().equals(operator.getClass().getSimpleName())) {
                return operator.mutate(qn);
            }
        }
        return null;
    }
}
