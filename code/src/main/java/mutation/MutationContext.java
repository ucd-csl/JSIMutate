package mutation;

import java.util.ArrayList;
import java.util.Collection;

public class MutationContext {

    private final Collection<Mutation> mutations = new ArrayList<>();
    private Mutation mutationToApply = null;

    public void registerMutation(Mutation mutation) {
        if (mutationToApply == null) {
            mutations.add(mutation);
        }
    }

    public boolean shouldMutate(Mutation mutation) {
        return mutationToApply != null && mutationToApply.equals(mutation);
    }

    public void setMutationToApply(Mutation mutationToApply) {
        this.mutationToApply = mutationToApply;
    }

    public Collection<Mutation> getMutations() {
        return mutations;
    }
}
