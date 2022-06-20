package qn;

import org.jdom2.Element;

public class Join extends Node {
    private Fork fork;

    public Join(String name, Element xmlElement) {
        super(name, xmlElement);
    }

    public void setFork(Fork fork) {
        this.fork = fork;
    }
}
