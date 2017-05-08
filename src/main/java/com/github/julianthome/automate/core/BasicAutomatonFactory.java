package com.github.julianthome.automate.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by julian on 08/05/2017.
 */
public class BasicAutomatonFactory implements AutomatonProvider<BasicAutomaton> {

    final static Logger LOGGER = LoggerFactory.getLogger(LabelledAutomatonFactory.class);

    private static BasicAutomatonFactory fact = null;


    public static BasicAutomatonFactory getInstance () {
        if(fact == null) {
            fact = new BasicAutomatonFactory();
        }
        return fact;
    }

    @Override
    public BasicAutomaton getAllAccepting() {
        BasicAutomaton all = new BasicAutomaton();
        all.start.setKind(State.Kind.ACCEPT);
        all.addTransition(new Transition(all.start,all.start, CharRange.ANY.clone()));
        return all;
    }

    @Override
    public BasicAutomaton getAnyAccepting() {
        BasicAutomaton any = new BasicAutomaton();
        State acc = any.createNewState(State.Kind.ACCEPT);
        any.addTransition(new Transition(any.start, acc, CharRange.ANY.clone()));
        return any;
    }

    @Override
    public BasicAutomaton getNewAutomaton() {
        return new BasicAutomaton();
    }

    @Override
    public BasicAutomaton getNewAutomaton(BasicAutomaton a) {
        return new BasicAutomaton((BasicAutomaton)a);
    }

    @Override
    public BasicAutomaton getEmtpyAutomaton() {
        return new BasicAutomaton(true);
    }

    @Override
    public BasicAutomaton getNewAutomaton(State start, Collection<Transition> t) {
        return new BasicAutomaton(start, t);
    }

}
