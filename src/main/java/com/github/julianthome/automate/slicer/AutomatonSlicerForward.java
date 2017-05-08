package com.github.julianthome.automate.slicer;


import com.github.julianthome.automate.core.Automaton;
import com.github.julianthome.automate.core.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by julian on 07/12/2016.
 */
public class AutomatonSlicerForward extends AutomatonSlicer {


    public AutomatonSlicerForward(Automaton cn) {
        super(cn);
    }

    @Override
    public Collection<State> getNext(Collection<State> n) {
        Set<State> ret = new HashSet();
        for(State v : n) {
            ret.addAll(a.outgoingEdgesOf(v).stream().map(e -> e.getTarget())
                    .collect(Collectors.toSet()));
        }
        return ret;
    }

}
