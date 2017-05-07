package com.github.julianthome.automate.slicer;

import com.github.julianthome.automate.core.Automaton;
import com.github.julianthome.automate.core.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class AutomatonSlicerBackward extends AutomatonSlicer {

    public AutomatonSlicerBackward(Automaton a) {
        super(a);
    }

    @Override
    public Collection<State> getNext(Collection<State> n) {
        Set<State> ret = new HashSet();
        for(State v : n) {
            ret.addAll(a.incomingEdgesOf(v).stream().map(e -> e.getSource())
                    .collect(Collectors.toSet()));
        }
        return ret;
    }
}
