package com.github.julianthome.automate.core;

import java.util.Collection;


public interface AutomatonProvider<T extends Automaton> {
    T getAllAccepting();
    T getAnyAccepting();
    T getNewAutomaton();
    T getNewAutomaton(T a);
    T getEmtpyAutomaton();
    T getNewAutomaton(State start, Collection<Transition> t);
}
