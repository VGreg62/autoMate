package com.github.julianthome.automate.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class LabelledAutomaton extends Automaton<LabelledAutomaton>{


    final static Logger LOGGER = LoggerFactory.getLogger(LabelledAutomaton.class);

    protected LabelledAutomaton() {
        super(LabelledAutomatonFactory.getInstance(),false);
    }

    public LabelledAutomaton(LabelledAutomaton a) {
        super
                (LabelledAutomatonFactory.getInstance(),a);
    }

    public LabelledAutomaton(State start, Collection<Transition> t) {
        super(LabelledAutomatonFactory.getInstance(),start,t);
    }

    protected LabelledAutomaton(boolean acceptsEmptyString) {
        super(LabelledAutomatonFactory.getInstance(),acceptsEmptyString);
    }
    @Override
    protected State createNewState(State.Kind kind) {
        return new LabelledState(kind, super.snum++);
    }



    @Override
    protected State createNewState(State ... other) {
        LabelledState ns = (LabelledState)createNewState(Arrays.asList(other));

        ns.getLabels().addAll(collectLabels(Arrays.asList(other)));

        return ns;
    }

    @Override
    protected State createNewState(Collection<State> other) {
        boolean accept = other.stream().filter(s -> s.isAccept()).count() ==
                other.size();

        return createNewState(accept ? State.Kind.ACCEPT : State.Kind.NORMAL,
                other);
    }

    @Override
    protected State createNewState(State.Kind kind, Collection<State> other) {

        LOGGER.debug("super {}", snum);
        LabelledState ls = new LabelledState(kind, snum++);
        ls.getLabels().addAll(collectLabels(other));

        return ls;
    }


    @Override
    public Automaton clone() {
        return new LabelledAutomaton(this);
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


    @Override
    public LabelledAutomaton intersect(LabelledAutomaton a) {

        LabelledState tstart = (LabelledState)this.start;
        LabelledState astart = (LabelledState)a.start;

        LabelledAutomaton first = (LabelledAutomaton)super.intersect(a);

        LabelledState flabel = (LabelledState)first.getStart();

        flabel.getLabels().addAll(tstart.getLabels());
        flabel.getLabels().addAll(astart.getLabels());

        return first;
    }

}
