package com.github.julianthome.automate.core;

/**
 * Created by julian on 08/05/2017.
 */
public interface AutomatonProvider<T extends BasicAutomaton> {
    T getAllAccepting();
    T getAnyAccepting();
    T getNewAutomaton();
    T getNewAutomaton(T a);
}
