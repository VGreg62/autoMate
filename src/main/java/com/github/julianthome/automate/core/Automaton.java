package com.github.julianthome.automate.core;


import java.util.*;

public class Automaton extends AbstractAutomaton<Automaton> {


    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected Automaton(String name) {
        this();
        this.name = name;
    }

    protected Automaton() {
        super(AutomatonFactory.getInstance(),
                false);
    }

    public Automaton(Automaton a) {
        super(AutomatonFactory
                .getInstance(),a);
    }

    public Automaton(State start, Collection<Transition> t) {
        super(AutomatonFactory.getInstance(),start,t);
    }

    protected Automaton(boolean acceptsEmptyString) {
        super(AutomatonFactory.getInstance(),acceptsEmptyString);
    }


}
