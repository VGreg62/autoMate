package com.github.julianthome.automate.core;


import java.util.*;

public class BasicAutomaton extends Automaton {

    protected BasicAutomaton() {
        super(false);
    }

    public BasicAutomaton(BasicAutomaton a) {
        super(a);
    }

    public BasicAutomaton(State start, Collection<Transition> t) {
        super(start,t);
    }

    protected BasicAutomaton(boolean acceptsEmptyString) {
        super(acceptsEmptyString);
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

    @Override
    public Automaton getAllAccepting() {
        return BasicAutomatonFactory.getInstance().getAllAccepting();
    }

    @Override
    public Automaton getAnyAccepting() {
        return BasicAutomatonFactory.getInstance().getAnyAccepting();
    }

    @Override
    public Automaton getNewAutomaton() {
        return new BasicAutomaton();
    }

    @Override
    public Automaton getNewAutomaton(Automaton a) {
        return BasicAutomatonFactory.getInstance().getNewAutomaton(a);
    }

    @Override
    public Automaton getEmtpyAutomaton() {
        return BasicAutomatonFactory.getInstance().getEmtpyAutomaton();
    }

    @Override
    public Automaton clone() {
        return new BasicAutomaton(this);
    }

    @Override
    public Automaton getNewAutomaton(State start, Collection<Transition> t) {
        return new BasicAutomaton(start,t);
    }
}
