package com.github.julianthome.automate.core;


import java.util.*;

public class BasicAutomaton extends Automaton<BasicAutomaton> {


    protected BasicAutomaton() {
        super(BasicAutomatonFactory.getInstance(),
                false);
    }

    public BasicAutomaton(BasicAutomaton a) {
        super(BasicAutomatonFactory
                .getInstance(),a);
    }

    public BasicAutomaton(State start, Collection<Transition> t) {
        super(BasicAutomatonFactory.getInstance(),start,t);
    }

    protected BasicAutomaton(boolean acceptsEmptyString) {
        super(BasicAutomatonFactory.getInstance(),acceptsEmptyString);
    }


}
