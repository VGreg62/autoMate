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

    protected State createNewState(State.Kind kind) {
        return new State(kind, snum++);
    }

    protected State createNewState(State ... other) {
        return createNewState(Arrays.asList(other));
    }

    protected State createNewState(Collection<State> other) {

        boolean accept = other.stream().filter(s -> s.isAccept()).count() ==
                other.size();

        return createNewState(accept ? State.Kind.ACCEPT : State.Kind.NORMAL,
                other);
    }

    protected State createNewState(State.Kind kind, Collection<State> other) {
        return createNewState(kind);
    }

}
