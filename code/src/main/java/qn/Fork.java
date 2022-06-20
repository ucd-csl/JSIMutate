package qn;

import org.jdom2.Element;

public class Fork extends Node {
    private Join join;

    public Fork(String name, Element xmlElement) {
        super(name, xmlElement);
    }

    public Join getJoin() {
        return join;
    }

    public void setJoin(Join join) {
        this.join = join;
    }
}
