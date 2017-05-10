package com.github.julianthome.automate.ext;


import org.jgrapht.graph.DefaultEdge;

public class MemAutomatonEdge extends DefaultEdge {

    @Override
    public MemAutomatonNode getSource() {
        return (MemAutomatonNode)super.getSource();
    }

    @Override
    public MemAutomatonNode getTarget() {
        return (MemAutomatonNode)super.getTarget();
    }

}
