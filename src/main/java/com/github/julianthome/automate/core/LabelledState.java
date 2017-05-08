package com.github.julianthome.automate.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;


public class LabelledState extends State {

    final static Logger LOGGER = LoggerFactory.getLogger(LabelledState.class);

    private Set<String> labels = new HashSet<>();

    public LabelledState(Kind kind, int id, Set<String> labels) {
        super(kind, id);
        this.labels.addAll(labels);
    }

    public LabelledState(Kind kind, int id) {
        super(kind, id);
    }

    @Override
    public State clone() {
        LOGGER.debug("Clone labelled");
        return new LabelledState(this.kind,this.id, this.labels);
    }

    public Set<String> getLabels() {
        return labels;
    }


}
