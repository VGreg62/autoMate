package com.github.julianthome.automate.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by julian on 08/05/2017.
 */
public class BasicAutomatonFactory implements
        AutomatonProvider<Automaton> {

    final static Logger LOGGER = LoggerFactory.getLogger(LabelledAutomatonFactory.class);

    private static BasicAutomatonFactory fact = null;


    public static BasicAutomatonFactory getInstance () {
        if(fact == null) {
            fact = new BasicAutomatonFactory();
        }
        return fact;
    }

    @Override
    public Automaton getAllAccepting() {
        Automaton all = new BasicAutomaton();
        all.start.setKind(State.Kind.ACCEPT);
        all.addTransition(new Transition(all.start,all.start, CharRange.ANY.clone()));
        return all;
    }

    @Override
    public Automaton getAnyAccepting() {
        Automaton any = new BasicAutomaton();
        State acc = any.createNewState(State.Kind.ACCEPT);
        any.addTransition(new Transition(any.start, acc, CharRange.ANY.clone()));
        return any;
    }

    @Override
    public Automaton getNewAutomaton() {
        return new BasicAutomaton();
    }

    @Override
    public Automaton getNewAutomaton(Automaton a) {
        return new BasicAutomaton((BasicAutomaton)a);
    }

    @Override
    public Automaton getEmtpyAutomaton() {
        return new BasicAutomaton(true);
    }

}
