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

import com.github.julianthome.automate.slicer.AutomatonSlicerBackward;
import com.github.julianthome.automate.slicer.AutomatonSlicerForward;
import com.github.julianthome.automate.utils.Tuple;
import org.jgrapht.graph.DirectedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractAutomaton<T extends AbstractAutomaton>
        extends DirectedPseudograph<State, Transition>
        implements AutomatonInterface<T> {

    final static Logger LOGGER = LoggerFactory.getLogger(AbstractAutomaton.class);

    protected State start;

    protected int snum = 0;


    BasicOperations<T> dispatch = null;


    protected AbstractAutomaton(AutomatonProvider<T> provider, AbstractAutomaton a) {
        super(Transition.class);

        Map<State, State> smap = new HashMap<>();

        Set<State> vs = a.vertexSet();
        LOGGER.debug("a {}", a.start.getId());

        for (State s : vs) {
            if (!smap.containsKey(s)) {
                smap.put(s, s.clone());
            }
            if (a.start.equals(s)) {
                assert smap.containsKey(s);
                start = smap.get(s);
                addVertex(start);
            }
        }

        Set<Transition> es = a.edgeSet();
        for (Transition e : es) {
            addTransition(new Transition(smap.get(e.getSource()),
                    smap.get(e.getTarget()),
                    e.getLabel().clone()));
        }

        assert a.start != null;
        snum = a.snum;
        dispatch = BasicOperations.getInstance();
        dispatch.init(provider);
    }

    protected AbstractAutomaton(AutomatonProvider<T> provider,
                                State start,
                                Collection<Transition> t) {
        super(Transition.class);

        Map<State, State> smap = new HashMap<>();

        for (Transition trans : t) {

            smap.put(trans.getSource(), createNewState(trans.getSource().getKind()));

            smap.put(trans.getTarget(), createNewState(trans.getTarget().getKind()));

            if (start.equals(trans.getSource()))
                this.start = trans.getSource();

            if (start.equals(trans.getTarget()))
                this.start = trans.getTarget();

            addTransition(trans);
        }
        dispatch = BasicOperations.getInstance();
        dispatch.init(provider);
    }

    protected AbstractAutomaton(AutomatonProvider<T> provider, boolean
            acceptsEmptyString) {
        super(Transition.class);
        start = createNewState((acceptsEmptyString ? State.Kind.ACCEPT :
                State.Kind.NORMAL));
        addVertex(start);
        dispatch = BasicOperations.getInstance();
        dispatch.init(provider);
    }


    public boolean isEmpty() {
        return vertexSet().size() == 1 && edgeSet().size() == 0;
    }


    public boolean addTransition(Transition trans) {
        return addEdge(trans.getSource(), trans.getTarget(), trans);
    }

    @Override
    public boolean addEdge(State src, State tar, Transition t) {

        if (!vertexSet().contains(src))
            super.addVertex(src);

        if (!vertexSet().contains(tar))
            super.addVertex(tar);

        return super.addEdge(src, tar, t);
    }

    @Override
    public Transition addEdge(State src, State tar) {
        throw new NotImplementedException();
    }

    private void addTransitions(Collection<Transition> trans) {
        trans.stream().forEach(t -> addTransition(t));
    }


    @Override
    public T union(T other) {
        return dispatch.union((T)this, other);
    }

    @Override
    public T star() {
        return dispatch.star((T)this);
    }

    @Override
    public T plus() {
        return dispatch.plus((T)this);
    }

    @Override
    public T optional() {
        return dispatch.optional((T) this);
    }

    @Override
    public T repeat(int min, int max) {
        return dispatch.repeat((T)this, min, max);
    }

    @Override
    public T repeatMax(int max) {
        return dispatch.repeatMax((T)this, max);
    }

    @Override
    public T repeatMin(int min) {
        return dispatch.repeatMin((T)this, min);
    }

    @Override
    public T append(char c) {
        return dispatch.append((T)this, c);
    }

    @Override
    public T append(char min, char max) {
        return dispatch.append((T)this, min, max);
    }

    public T append(TransitionLabel lbl) {
        return dispatch.append((T)this, lbl);
    }

    @Override
    public T concat(T other) {
        return dispatch.concat((T) this, other);
    }

    @Override
    public T concat(T other, boolean accept) {
        return dispatch.concat((T) this, other, accept);
    }

    @Override
    public T intersect(T other) {
        return dispatch.intersect((T) this, other);
    }

    @Override
    public T determinize() {
        return dispatch.determinize((T)this);
    }

    @Override
    public T expand(){
        return dispatch.expand((T)this);
    }


    @Override
    public T complement() {
        return dispatch.complement((T)this);
    }


    @Override
    public T minus(T other) {
        return dispatch.minus((T)this, other);
    }

    private void merge(Collection<State> states) {

        State first = null;

        for (State s : states) {

            if (first == null) {
                first = s;
                continue;
            }

            merge(first, s);
        }
    }

    protected void merge(State a, State b) {
        assert containsVertex(a);
        assert containsVertex(b);

        Set<Transition> out = outgoingEdgesOf(b);
        Set<Transition> in = incomingEdgesOf(b);

        Set<Transition> trans = new HashSet<>();

        for (Transition t : out) {
            trans.add(new Transition(a, t.getTarget(), t.getLabel().clone
                    ()));
        }

        for (Transition t : in) {
            trans.add(new Transition(t.getSource(), a, t.getLabel().clone
                    ()));
        }

        removeVertex(b);
        addTransitions(trans);
    }


    protected Set<State> getAcceptStates() {
        return vertexSet().stream().filter(v -> v.isAccept()).collect
                (Collectors.toSet());
    }

    protected State addVirtualEnd() {

        Set<State> end = getAcceptStates();
        State nend = createNewState(State.Kind.ACCEPT);

        if (isEmpty()) {
            addTransition(new Transition(start, nend));
            return nend;
        }

        for (State e : end) {
            e.setKind(State.Kind.NORMAL);
            addTransition(new Transition(e, nend));
        }
        return nend;
    }


    private Set<State> getConnectedOutNodes(State s) {
        return outgoingEdgesOf(s).stream().map(Transition::getTarget)
                .collect(Collectors.toSet());
    }

    private Set<State> getConnectedInNodes(State s) {
        return incomingEdgesOf(s).stream().map(Transition::getSource)
                .collect(Collectors.toSet());
    }

    private Collection<State> getMatches(State s, char c) {
        return outgoingEdgesOf(s).stream().filter(e -> e.getLabel().match(c))
                .map(Transition::getTarget).collect(Collectors.toSet());
    }


    private void collapseStates(Predicate<State> p) {

        Set<State> fs;

        do {
            fs = vertexSet().stream()
                    .filter(p).collect(Collectors.toSet());

            merge(fs);

        } while (!fs.isEmpty() && fs.size() > 1);
    }


    protected void removeUnreachableStates() {
        if(!hasAcceptStates()) {
            Set<State> toRm = vertexSet().stream().filter(x -> !x.equals
                    (start)).collect(Collectors.toSet());
            removeAllVertices(toRm);
        } else {
            AutomatonSlicerForward fw = new AutomatonSlicerForward(this);
            AutomatonSlicerBackward bw = new AutomatonSlicerBackward(this);
            Collection<State> chop = fw.slice(this.start);
            chop.retainAll(bw.slice(this.getAcceptStates()));
            Set<State> vertices = new HashSet<>(vertexSet());
            vertices.removeAll(chop);
            removeAllVertices(vertices);
        }
    }

    public void eliminateRedundantTransitions() {
        for(State s : vertexSet()) {
            checkForRedundatTransitions(s);
        }
    }


    private Tuple<Set<TransitionLabel>, Set<TransitionLabel>> splitLabels
            (TransitionLabel t1, TransitionLabel t2) {

        Set<TransitionLabel> fst = new HashSet<>();
        Set<TransitionLabel> snd = new HashSet<>();

        TransitionLabel isect = t1.isect(t2);

        fst.addAll(t1.minus(isect));
        snd.addAll(t2.minus(isect));
        fst.add(isect);
        snd.add(isect);

        LOGGER.debug("new split {} {}", fst, snd);

        return new Tuple<>(fst,snd);
    }

    private void checkForRedundatTransitions(State s) {

        Set<Transition> toAdd = new HashSet<>();
        Set<Transition> toRm = new HashSet<>();
        Set<Transition> sorted = getSortedTransitions(s);

        Transition prev = null;

        for(Transition t : sorted) {

            if(prev == null)
                prev = t;

            if(prev.getLabel().isect(t.getLabel()) != null && !prev.getLabel().equals(t.getLabel())) {


                Tuple<Set<TransitionLabel>, Set<TransitionLabel>>
                lbls = splitLabels(prev.getLabel(), t.getLabel());

                for(TransitionLabel l : lbls.getKey()) {
                    toAdd.add(new Transition(prev.getSource(), prev.getTarget(),
                        l.clone()));
                }


                for(TransitionLabel l : lbls.getVal()) {
                    toAdd.add(new Transition(t.getSource(), t.getTarget(),
                            l.clone()));
                }

                toRm.add(prev);
                toRm.add(t);
                LOGGER.debug("rm {}", prev);


            } else {
                prev = t;
            }
        }

        removeAllEdges(toRm);
        addTransitions(toAdd);
    }


    public void eliminateAcceptStates() {
        getAcceptStates().forEach(v -> v.setKind(State.Kind.NORMAL));
        assert !hasAcceptStates();
    }

    public boolean hasAcceptStates() {
        return getAcceptStates().size() > 0;
    }



    protected Set<Transition> getSortedTransitions(State s) {
        Set<Transition> sorted = new TreeSet<>();
        sorted.addAll(outgoingEdgesOf(s));
        return sorted;
    }


    public void minimize() {

        // delete unreachable states
        //Automat a = new Automat(this);
        //a.collapseStates(s -> !s.isAccept() && outDegreeOf(s) == 0);
        //a.collapseStates(s -> s.isAccept() && outDegreeOf(s) == 0);

        removeUnreachableStates();
        Map<Set<State>, Boolean> inequality = new HashMap<>();

        for(State v1 : vertexSet()){
            for(State v2: vertexSet()){

                if(v1.equals(v2))
                    continue;

                Set<State> s = new HashSet<>();
                s.add(v1);
                s.add(v2);

                boolean ineq = false;

                if((v1.isAccept() && !v2.isAccept()) ||
                        (v2.isAccept() && !v1.isAccept()))
                                ineq = true;

                if(!inequality.containsKey(s))
                    inequality.put(s, ineq);
            }
        }

        LOGGER.debug(inequality.toString());

        boolean change;

        do {
            change = false;

            Set<Set<State>> ineq = new HashSet<>();

            for (Map.Entry<Set<State>, Boolean> e : inequality.entrySet()) {

                LOGGER.debug("CALL with {}", e.getKey());
                if(e.getValue() == false && statesNotEqual(e.getKey(),
                        inequality)) {
                    LOGGER.debug("{} not equal", e.getKey());
                    ineq.add(e.getKey());
                }
            }

            for(Set<State> i : ineq) {
                if(inequality.get(i) == false) {
                    LOGGER.debug("set {} to true", i);
                    inequality.put(i, true);
                    change = true;
                }
            }

        } while (change);


        Set<Set<State>> tomerge = inequality.entrySet().stream()
                .filter(e -> e.getValue() == false).
                        map(e -> e.getKey())
                .collect(Collectors.toSet());

        LOGGER.debug("tomerge {}", tomerge);



        Map<State, Set<State>> group = new HashMap<>();

        for(Set<State> m : tomerge) {

            State [] s = m.toArray(new State [m.size()]);

            State fst = s[0];
            State snd = s[1];

            if(!group.containsKey(fst)){
                group.put(fst, new HashSet<>());
            }

            if(!group.containsKey(snd)){
                group.put(snd, new HashSet<>());
            }

            group.get(fst).add(snd);
            group.get(fst).add(fst);
            group.get(snd).add(fst);
            group.get(snd).add(snd);
        }

        Set<Set<State>> merged = new HashSet<>();
        merged.addAll(group.values());


        for(Set<State> m : merged) {
            merge(m);
        }

    }


    private boolean statesNotEqual(Collection<State> states, Map<Set<State>,
            Boolean>  emap) {
        assert states.size() == 2;

        State [] stat = states.toArray(new State[states.size()]);

        return statesNotEqual(stat[0],stat[1], emap);
    }

    private boolean statesNotEqual(State a, State b, Map<Set<State>,
            Boolean> emap) {

        LOGGER.debug("{} vs {}", a, b);

        if(outDegreeOf(a) != outDegreeOf(b))
            return true;

        if(isConnectedToAcceptState(a) && ! isConnectedToAcceptState(b))
            return true;

        if(isConnectedToAcceptState(b) && ! isConnectedToAcceptState(a))
            return true;

        Set<Transition> ta = outgoingEdgesOf(a);

        if(!ta.containsAll(outgoingEdgesOf(b))) {
            return true;
        }

        Set<State> outas = getConnectedOutNodes(a);
        Set<State> outbs = getConnectedOutNodes(a);

        for(State outa : outas) {
            for (State outb : outbs) {
                Set<State> t = new HashSet<>();
                t.add(outa);
                t.add(outb);
                if(emap.containsKey(t) && emap.get(t) == true)
                    return true;
            }
        }

        LOGGER.debug("HO ");
        return false;
    }



    private boolean isConnectedToAcceptState(State s) {
        return getConnectedOutNodes(s).stream().anyMatch(x -> x.isAccept());
    }



    protected Map<TransitionLabel, Set<State>> getCombinedTransitionMap
            (Collection<State> stats, Predicate<Transition> filter) {

        LOGGER.debug("get combined transition map for {}", stats);
        Map<TransitionLabel, Set<State>> m = new HashMap<>();

        for (State s : stats) {
            Map<TransitionLabel, Set<State>> ret = getTransitionMap(s, filter);
            for(Map.Entry<TransitionLabel, Set<State>> e : ret.entrySet()) {
                if (!m.containsKey(e.getKey())) {
                    m.put(e.getKey(), new HashSet<>());
                }
                m.get(e.getKey()).addAll(e.getValue());
            }
        }

        return m;
    }

    private Map<TransitionLabel, Set<State>> getTransitionMap(State s,
                                                              Predicate<Transition> filter) {

        Map<TransitionLabel, Set<State>> m = new HashMap<>();


        for (Transition t : outgoingEdgesOf(s).stream().filter(filter)
                .collect(Collectors.toSet())) {

            LOGGER.debug("Trans {}", t);

            if (!m.containsKey(t.getLabel())) {
                m.put(t.getLabel(), new HashSet<>());
            }

            m.get(t.getLabel()).add(t.getTarget());
        }

        LOGGER.debug("MM {}", m);

        return m;
    }



    private void _computeEpsilonClosureForState(State s, Map<State, Set<State>> emap) {

        if (!emap.containsKey(s))
            emap.put(s, new HashSet<>());
        else
            return;

        emap.get(s).add(s);

        Set<Transition> epsilons = outgoingEdgesOf(s).stream().filter(e -> e
                .isEpsilon()).collect
                (Collectors.toSet());

        for (Transition epsilon : epsilons) {
            _computeEpsilonClosureForState(epsilon.getTarget(), emap);
            emap.get(s).addAll(emap.get(epsilon.getTarget()));
        }
    }


    private Set<State> getEpsilonClosure(State s) {
        Map<State, Set<State>> eclosure = new HashMap<>();
        _computeEpsilonClosureForState(s, eclosure);
        return eclosure.get(s);
    }

    protected Map<State, Set<State>> getEpsilonClosure() {

        Map<State, Set<State>> eclosure = new HashMap<>();

        for (State s : vertexSet()) {
            _computeEpsilonClosureForState(s, eclosure);
        }

        return eclosure;
    }




    public boolean match(String s) {

        assert start != null;
        LinkedList<Tuple<State, Integer>> worklist = new
                LinkedList<>();

        worklist.add(new Tuple(start, 0));

        int ept = s.length();

        while (!worklist.isEmpty()) {

            Tuple<State, Integer> t = worklist.pop();

            if (t.getKey().isAccept() && t.getVal() == ept)
                return true;

            if (t.getVal() >= ept)
                continue;

            Collection<State> m = getMatches(t.getKey(), s.charAt(t.getVal()));

            for (State nxt : m) {
                worklist.add(new Tuple(nxt, t.val + 1));
            }

        }

        return false;
    }


    protected String vertexToDot(State n) {
        String shape = "circle";
        String color = "";

        if (start.equals(n)) {
            color = "green";
            assert start.equals(n);
        }

        if (n.isAccept()) {
            shape = "doublecircle";
        }

        return "\t" + n.toDot() + " [label=\"" + n.toDot() + "\"," +
                "shape=\"" + shape + "\", color=\"" + color + "\"];\n";
    }

    public String toDot() {

        StringBuilder sb = new StringBuilder();
        sb.append("digraph {\n" +
                "\trankdir=TB;\n");

        sb.append("\tnode [fontname=Helvetica,fontsize=11];\n");
        sb.append("\tedge [fontname=Helvetica,fontsize=10];\n");


        for (State n : this.vertexSet()) {
            sb.append(vertexToDot(n));
        }


        for (Transition e : this.edgeSet()) {
            sb.append("\t" + e.toDot() + "\n");
        }
        sb.append("}\n");

        return sb.toString();
    }


    public State getStart() {
        return start;
    }


    protected State createNewState(State.Kind kind) {
        return new State(kind, snum++);
    }

}
