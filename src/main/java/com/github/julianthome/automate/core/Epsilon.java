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

import java.util.Collection;
import java.util.HashSet;


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
