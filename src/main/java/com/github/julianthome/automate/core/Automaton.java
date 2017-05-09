package com.github.julianthome.automate.core;


import java.util.*;

public class Automaton extends AbstractAutomaton<Automaton> {


    protected Automaton() {
        super(BasicAutomatonFactory.getInstance(),
                false);
    }

    public Automaton(Automaton a) {
        super(BasicAutomatonFactory
                .getInstance(),a);
    }

    public Automaton(State start, Collection<Transition> t) {
        super(BasicAutomatonFactory.getInstance(),start,t);
    }

    protected Automaton(boolean acceptsEmptyString) {
        super(BasicAutomatonFactory.getInstance(),acceptsEmptyString);
    }


}
