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

package com.github.julianthome.automate.ext;


import com.github.julianthome.automate.core.Automaton;

public class MemAutomatonNode implements Comparable<MemAutomatonNode> {

    public enum Kind {
        UNION,
        INTERSECTION,
        MINUS,
        CONCAT,
        PLUS,
        STAR,
        COMPLEMENT,
        LEAF
    }

    private Automaton a = null;
    private Kind kind = Kind.LEAF;
    private int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MemAutomatonNode(Kind kind, Automaton a, int id) {
        this.a = a;
        this.kind = kind;
        this.id = id;
    }

    public MemAutomatonNode(MemAutomatonNode n) {
        this.a = new Automaton(a);
        this.kind = n.kind;
        this.id = n.id;
    }

    public Automaton getAutomaton() {
        return a;
    }

    public void setAutomaton(Automaton a) {
        this.a = a;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind k) {
        this.kind = k;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof MemAutomatonNode))
            return false;

        MemAutomatonNode o = (MemAutomatonNode)other;

        return id == o.id;
    }

    @Override
    public int compareTo(MemAutomatonNode n) {
        return id - n.getId();
    }
}
