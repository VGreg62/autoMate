package com.github.julianthome.automate.slicer;

import com.github.julianthome.automate.core.BasicAutomaton;
import com.github.julianthome.automate.core.State;

import java.util.*;

public abstract class AutomatonSlicer implements Slicer {

    protected BasicAutomaton a = null;

    public AutomatonSlicer() {}
    public AutomatonSlicer(BasicAutomaton cn) {
        a = cn;
    }


    public abstract Collection<State> getNext(Collection<State> n);

    @Override
    public void setNetwork(BasicAutomaton a) {
        this.a = a;
    }

    @Override
    public Collection<State> slice(Collection<State> criteria) {

        assert this.a != null;

        Set<State> bw = new HashSet();
        LinkedList<State> incoming = new LinkedList();

        incoming.addAll(criteria);

        while(!incoming.isEmpty()) {
            State e = incoming.pop();
            if(!bw.contains(e)) {
                incoming.addAll(getNext(Collections.singleton(e)));
                bw.add(e);
            }

        }
        return bw;
    }

    @Override
    public Collection<State> slice(State criterion) {
        return this.slice(Collections.singleton(criterion));
    }

}
