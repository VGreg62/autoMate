package com.github.julianthome.automate.slicer;


import com.github.julianthome.automate.core.BasicAutomaton;
import com.github.julianthome.automate.core.State;

import java.util.Collection;

public interface Slicer {
    void setNetwork(BasicAutomaton cn);
    Collection<State> slice(Collection<State> criteria);
    Collection<State> slice(State criterion);
}
