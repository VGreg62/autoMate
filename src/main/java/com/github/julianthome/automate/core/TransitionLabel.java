package com.github.julianthome.automate.core;

/**
 * Created by julian on 28/04/2017.
 */
public interface TransitionLabel extends DotSerializer, Cloneable {

    boolean match(TransitionLabel other);
    boolean match(char c);

    boolean isEpsilon();
    boolean isMatch();

    TransitionLabel isect(TransitionLabel l);

    @Override
    int hashCode();

    TransitionLabel clone();

}
