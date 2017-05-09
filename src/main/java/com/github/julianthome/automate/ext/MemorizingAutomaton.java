package com.github.julianthome.automate.ext;

import com.github.julianthome.automate.core.AutomatonInterface;

/**
 * Created by julian on 09/05/2017.
 */
public class MemorizingAutomaton implements AutomatonInterface<MemorizingAutomaton> {



    @Override
    public MemorizingAutomaton union(MemorizingAutomaton other) {
        return null;
    }

    @Override
    public MemorizingAutomaton star() {
        return null;
    }

    @Override
    public MemorizingAutomaton plus() {
        return null;
    }

    @Override
    public MemorizingAutomaton optional() {
        return null;
    }

    @Override
    public MemorizingAutomaton repeat(int min, int max) {
        return null;
    }

    @Override
    public MemorizingAutomaton repeatMax(int max) {
        return null;
    }

    @Override
    public MemorizingAutomaton repeatMin(int min) {
        return null;
    }

    @Override
    public MemorizingAutomaton append(char c) {
        return null;
    }

    @Override
    public MemorizingAutomaton append(char min, char max) {
        return null;
    }

    @Override
    public MemorizingAutomaton concat(MemorizingAutomaton other) {
        return null;
    }

    @Override
    public MemorizingAutomaton concat(MemorizingAutomaton other, boolean accept) {
        return null;
    }

    @Override
    public MemorizingAutomaton intersect(MemorizingAutomaton other) {
        return null;
    }

    @Override
    public MemorizingAutomaton determinize() {
        return null;
    }

    @Override
    public MemorizingAutomaton expand() {
        return null;
    }

    @Override
    public MemorizingAutomaton complement() {
        return null;
    }

    @Override
    public MemorizingAutomaton minus(MemorizingAutomaton other) {
        return null;
    }
}
