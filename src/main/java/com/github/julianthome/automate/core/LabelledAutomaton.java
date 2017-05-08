package com.github.julianthome.automate.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class LabelledAutomaton extends BasicAutomaton {


    final static Logger LOGGER = LoggerFactory.getLogger(LabelledAutomaton.class);


    public LabelledAutomaton() {
        super(false, LabelledAutomatonFactory.getInstance());
    }


    public LabelledAutomaton(boolean acceptsEmptyString) {
        super(acceptsEmptyString, LabelledAutomatonFactory.getInstance());
    }



    public LabelledAutomaton(LabelledAutomaton l) {
        super(l);
    }

    @Override
    protected State createNewState(State.Kind kind) {
        return new LabelledState(kind, super.snum++);
    }

    @Override
    protected State createNewState(State other) {
        return createNewState(other.getKind(), Collections.singleton(other));
    }

    @Override
    protected State createNewState(State.Kind kind, Collection<State> other) {

        LOGGER.debug("super {}", snum);
        LabelledState ls = new LabelledState(kind, snum++);
        ls.getLabels().addAll(collectLabels(other));

        return ls;
    }



    private Set<String> collectLabels(Collection<State> other){

        Set<String> labels = new HashSet<>();

        for(State s : other) {
            if(s instanceof LabelledState) {
                labels.addAll(((LabelledState) s).getLabels());
            }
        }

        LOGGER.debug("LBLS {}", labels);

        return labels;
    }

    public void labelAllStates(String lbl) {
        for(State s : vertexSet()) {
            ((LabelledState)s).getLabels().add(lbl);
        }
    }

    @Override
    protected String vertexToDot(State n) {

        LOGGER.debug("BOOM");

        if(n instanceof LabelledState) {

            LabelledState ls = (LabelledState)n;

            String shape = "circle";
            String color = "";

            if (start.equals(n)) {
                color = "green";
                assert start.equals(n);
            }

            if (n.isAccept()) {
                shape = "doublecircle";
            }


            return "\t" + n.toDot() + " [label=\"" + n.toDot() + ls.getLabels()
                    .toString() +
                    "\"," +
                    "shape=\"" + shape + "\", color=\"" + color + "\"];\n";
        }

        return super.vertexToDot(n);
    }



}
