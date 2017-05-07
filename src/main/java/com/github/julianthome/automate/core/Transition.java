package com.github.julianthome.automate.core;

import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by julian on 28/04/2017.
 */
public class Transition extends DefaultEdge implements DotSerializer {

    final static Logger LOGGER = LoggerFactory.getLogger(Transition.class);


    private State src;
    private State tar;
    private TransitionLabel lbl;


    public Transition(State src, State tar, TransitionLabel lbl) {
        this.src = src;
        this.tar = tar;
        this.lbl = lbl;

    }

    public Transition(State src, State tar, char min, char max) {
        this.src = src;
        this.tar = tar;
        this.lbl = new CharRange(min, max);
    }

    public Transition(State src, State tar) {
        this.src = src;
        this.tar = tar;
        this.lbl = new Epsilon();
    }


    public Transition(State src, State tar, char c) {
        this(src,tar,c,c);
    }


    @Override
    public State getSource() {
        return this.src;
    }

    @Override
    public State getTarget() {
        return this.tar;
    }


    public boolean isEpsilon() {
        return lbl.isEpsilon();
    }

    public boolean isMatch() {
        return lbl.isMatch();
    }

    public TransitionLabel getLabel() {
        return lbl;
    }

    public void setLabel(TransitionLabel lbl) {
        this.lbl = lbl;
    }

    public Transition clone() {
        return new Transition(src.clone(), tar.clone(), lbl.clone());
    }


    @Override
    public boolean equals(Object o) {

        if(!(o instanceof Transition)) {
            return false;
        }

        Transition t = (Transition)o;

        return src.equals(t.src) && tar.equals(t.tar) &&
                lbl.equals(t.lbl);
    }

    @Override
    public int hashCode() {
        int hc = 0;
        hc = 37 * hc + src.hashCode();
        hc = 37 * hc + tar.hashCode();
        return 37 * hc + lbl.hashCode();
    }

    @Override
    public String toDot() {

        String color = "black";

        if(lbl.isEpsilon())
            color = "red";
        else if (lbl.isMatch())
            color = "blue";

        return src.toDot() + " -> " + tar.toDot() + "[label=\"" + lbl.toDot()
                + "\", color=\"" + color + "\"];";
    }
}
