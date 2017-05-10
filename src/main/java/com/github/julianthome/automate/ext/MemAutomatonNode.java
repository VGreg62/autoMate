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
