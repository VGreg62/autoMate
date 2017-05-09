package com.github.julianthome.automate.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by julian on 08/05/2017.
 */
public class AutomatonFactory implements AutomatonProvider<Automaton> {

    final static Logger LOGGER = LoggerFactory.getLogger(AutomatonFactory.class);

    private static AutomatonFactory fact = null;


    public static AutomatonFactory getInstance () {
        if(fact == null) {
            fact = new AutomatonFactory();
        }
        return fact;
    }

    @Override
    public Automaton getAllAccepting() {
        Automaton all = new Automaton();
        all.start.setKind(State.Kind.ACCEPT);
        all.addTransition(new Transition(all.start,all.start, CharRange.ANY.clone()));
        return all;
    }

    @Override
    public Automaton getAnyAccepting() {
        Automaton any = new Automaton();
        State acc = any.createNewState(State.Kind.ACCEPT);
        any.addTransition(new Transition(any.start, acc, CharRange.ANY.clone()));
        return any;
    }

    @Override
    public Automaton getNewAutomaton() {
        return new Automaton();
    }

    @Override
    public Automaton getNewAutomaton(Automaton a) {
        return new Automaton((Automaton)a);
    }

    @Override
    public Automaton getEmtpyAutomaton() {
        return new Automaton(true);
    }

    @Override
    public Automaton getNewAutomaton(State start, Collection<Transition> t) {
        return new Automaton(start, t);
    }

}
