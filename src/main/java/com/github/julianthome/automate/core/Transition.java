/**
 * autoMate - yet another automaton library for Java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Julian Thome <julian.thome.de@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/

package com.github.julianthome.automate.core;

import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transition extends DefaultEdge implements DotSerializer,
        Comparable<Transition> {

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

    @Override
    public int compareTo(Transition o) {
        return lbl.compareTo(o.lbl);
    }
}
