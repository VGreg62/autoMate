package com.github.julianthome.automate.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class LabelledAutomatonFactory implements
        AutomatonProvider<LabelledAutomaton> {

    final static Logger LOGGER = LoggerFactory.getLogger(LabelledAutomatonFactory.class);

    private static LabelledAutomatonFactory fact = null;


    public static LabelledAutomatonFactory getInstance () {
        if(fact == null) {
            fact = new LabelledAutomatonFactory();
        }
        return fact;
    }

    @Override
    public LabelledAutomaton getAllAccepting() {
        LabelledAutomaton all = new LabelledAutomaton();
        all.start.setKind(State.Kind.ACCEPT);
        all.addTransition(new Transition(all.start,all.start, CharRange.ANY
                .clone()));
        return all;
    }

    @Override
    public LabelledAutomaton getAnyAccepting() {
        LabelledAutomaton any = new LabelledAutomaton();
        State acc = any.createNewState(State.Kind.ACCEPT);
        any.addTransition(new Transition(any.start, acc, CharRange.ANY.clone()));
        return any;
    }

    @Override
    public LabelledAutomaton getNewAutomaton() {
        return new LabelledAutomaton();
    }

    @Override
    public LabelledAutomaton getNewAutomaton(LabelledAutomaton a) {
        return new LabelledAutomaton(a);
    }

    @Override
    public LabelledAutomaton getEmtpyAutomaton() {
        return new LabelledAutomaton(true);
    }

    @Override
    public LabelledAutomaton getNewAutomaton(State start, Collection<Transition> t) {
        return new LabelledAutomaton(start,t);
    }
}
