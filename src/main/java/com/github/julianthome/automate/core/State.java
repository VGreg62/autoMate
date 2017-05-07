package com.github.julianthome.automate.core;

/**
 * Created by julian on 28/04/2017.
 */
public class State implements DotSerializer {

    public enum Kind {
        ACCEPT,
        NORMAL
    }

    private Kind kind;
    private int id;

    public State(Kind kind, int id){
        this.kind = kind;
        this.id = id;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public State(int id) {
        this(Kind.NORMAL,id);
    }

    public boolean isAccept() {
        return kind == Kind.ACCEPT;
    }

    public boolean isNormal() {
        return kind == Kind.NORMAL;
    }

    public int getId() {
        return id;
    }

    public String toDot() {
        return "s" + id;
    }

    public State clone() {
        return new State(this.kind,this.id);
    }

    @Override
    public boolean equals(Object other) {

        if(!(other instanceof State))
            return false;

        State s = (State)other;

        return this.id == s.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "s" + id;
    }

}
