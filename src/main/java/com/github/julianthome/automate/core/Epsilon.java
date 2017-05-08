package com.github.julianthome.automate.core;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by julian on 30/04/2017.
 */
public class Epsilon implements TransitionLabel {

    public Epsilon() {}

    @Override
    public String toDot() {
        return "ε";
    }

    @Override
    public boolean match(TransitionLabel other) {
        return true;
    }

    @Override
    public boolean match(char c) {
        return true;
    }

    @Override
    public boolean isEpsilon() {
        return true;
    }

    @Override
    public boolean isMatch() {
        return false;
    }

    @Override
    public boolean isConsecutive(TransitionLabel l) {
       return false;
    }

    @Override
    public TransitionLabel join(TransitionLabel l) {
        return l.clone();
    }

    @Override
    public TransitionLabel isect(TransitionLabel l) {
        return l.clone();
    }

    @Override
    public boolean contains(TransitionLabel l) {
        return false;
    }

    @Override
    public Collection<TransitionLabel> minus(TransitionLabel l) {
        return new HashSet<>();
    }

    public Epsilon clone() {
        return new Epsilon();
    }

    @Override
    public int compareTo(TransitionLabel lbl) {

        if(lbl instanceof Epsilon)
            return 0;
        else return -1;
    }

    @Override
    public int hashCode() {
        return "ε".hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Epsilon))
            return false;

        return true;
    }

}
