package com.github.julianthome.automate.core;

import java.util.Collection;


public interface TransitionLabel extends DotSerializer, Cloneable,
        Comparable<TransitionLabel> {

    boolean match(TransitionLabel other);
    boolean match(char c);

    boolean isEpsilon();
    boolean isMatch();

    boolean isConsecutive(TransitionLabel l);

    TransitionLabel join(TransitionLabel l);
    TransitionLabel isect(TransitionLabel l);
    boolean contains(TransitionLabel l);

    Collection<TransitionLabel> minus(TransitionLabel l);

    @Override
    int hashCode();

    TransitionLabel clone();

    int compareTo(TransitionLabel lbl);

}
