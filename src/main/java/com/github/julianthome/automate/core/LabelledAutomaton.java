package com.github.julianthome.automate.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class LabelledAutomaton extends Automaton {


    final static Logger LOGGER = LoggerFactory.getLogger(LabelledAutomaton.class);

    protected LabelledAutomaton() {
        super(false);
    }

    public LabelledAutomaton(LabelledAutomaton a) {
        super(a);
    }

    public LabelledAutomaton(State start, Collection<Transition> t) {
        super(start,t);
    }

    protected LabelledAutomaton(boolean acceptsEmptyString) {
        super(acceptsEmptyString);
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
    public Automaton getAllAccepting() {
        return LabelledAutomatonFactory.getInstance().getAllAccepting();
    }

    @Override
    public Automaton getAnyAccepting() {
        return LabelledAutomatonFactory.getInstance().getAnyAccepting();
    }

    @Override
    public Automaton getNewAutomaton() {
        return LabelledAutomatonFactory.getInstance().getNewAutomaton();
    }

    @Override
    public Automaton getNewAutomaton(Automaton a) {
        return new LabelledAutomaton((LabelledAutomaton)a);
    }

    @Override
    public Automaton getEmtpyAutomaton() {
        return LabelledAutomatonFactory.getInstance().getNewAutomaton();
    }

    @Override
    public Automaton clone() {
        return new LabelledAutomaton(this);
    }

    @Override
    public Automaton getNewAutomaton(State start, Collection<Transition> t) {
        return null;
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
    public Automaton intersect(Automaton a) {

        LabelledState tstart = (LabelledState)this.start;
        LabelledState astart = (LabelledState)a.start;

        LabelledAutomaton first = (LabelledAutomaton)super.intersect(a);

        LabelledState flabel = (LabelledState)first.getStart();

        flabel.getLabels().addAll(tstart.getLabels());
        flabel.getLabels().addAll(astart.getLabels());

        return first;
    }

}
