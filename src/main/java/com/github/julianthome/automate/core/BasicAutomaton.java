package com.github.julianthome.automate.core;

import com.github.julianthome.automate.slicer.AutomatonSlicerBackward;
import com.github.julianthome.automate.slicer.AutomatonSlicerForward;
import com.github.julianthome.automate.utils.Tuple;
import dk.brics.automaton.Automaton;
import org.jgrapht.graph.DirectedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BasicAutomaton extends DirectedPseudograph<State, Transition> {

    final static Logger LOGGER = LoggerFactory.getLogger(BasicAutomaton.class);

    protected State start;

    protected int snum = 0;

    protected AutomatonProvider provider = null;

    public BasicAutomaton() {
        this(false, BasicAutomatonFactory.getInstance());
        assert provider != null;
    }

    public BasicAutomaton(AutomatonProvider provider) {
        this(false, provider);
    }

    public BasicAutomaton(boolean acceptsEmptyString) {
        this(acceptsEmptyString, BasicAutomatonFactory.getInstance());
    }

    public BasicAutomaton(boolean acceptsEmptyString, AutomatonProvider
            provider) {
        super(Transition.class);
        start = createNewState((acceptsEmptyString ? State.Kind.ACCEPT :
                State.Kind.NORMAL));
        addVertex(start);
        this.provider = provider;
    }

    public BasicAutomaton(BasicAutomaton a) {
        super(Transition.class);

        Map<State, State> smap = new HashMap<>();

        LOGGER.debug("a {}", a.start.getId());

        for (State s : a.vertexSet()) {
            if (!smap.containsKey(s)) {
                smap.put(s, s.clone());
            }
            if (a.start.equals(s)) {
                assert smap.containsKey(s);
                start = smap.get(s);
                addVertex(start);
            }
        }

        for (Transition e : a.edgeSet()) {
            addTransition(new Transition(smap.get(e.getSource()),
                    smap.get(e.getTarget()),
                    e.getLabel().clone()));
        }

        assert a.start != null;
        snum = a.snum;

        this.provider = a.provider;
    }

    private BasicAutomaton(State start, Collection<Transition> t,
                           AutomatonProvider provider) {
        super(Transition.class);

        Map<State, State> smap = new HashMap<>();

        for (Transition trans : t) {

            smap.put(trans.getSource(), createNewState(trans.getSource()));

            smap.put(trans.getTarget(), createNewState(trans.getTarget()));

            if (start.equals(trans.getSource()))
                this.start = trans.getSource();

            if (start.equals(trans.getTarget()))
                this.start = trans.getTarget();

            addTransition(trans);
        }

    }



    protected State createNewState(State.Kind kind) {
        return new State(kind, snum++);
    }

    protected State createNewState(State other) {
        return createNewState(other.getKind());
    }

    protected State createNewState(State.Kind kind, Collection<State> other) {
        return createNewState(kind);
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

    private void merge(State a, State b) {
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


    private Set<State> getAcceptStates() {
        return vertexSet().stream().filter(v -> v.isAccept()).collect
                (Collectors.toSet());
    }

    public BasicAutomaton expand() {
        BasicAutomaton cp = provider.getNewAutomaton(this);
        cp.addVirtualEnd();
        return cp;
    }

    private State addVirtualEnd() {

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

    public BasicAutomaton concat(BasicAutomaton b) {
        return concat(b, true);
    }

    public BasicAutomaton concat(BasicAutomaton b, boolean rmaccept) {

        if (this.isEmpty() && b.isEmpty()) {
            return new BasicAutomaton(true);
        } else if (this.isEmpty()) {
            return new BasicAutomaton(b);
        } else if (b.isEmpty()) {
            return new BasicAutomaton(this);
        }

        BasicAutomaton first = provider.getNewAutomaton(this);
        BasicAutomaton snd = provider.getNewAutomaton(b);

        State end = first.addVirtualEnd();
        LOGGER.debug("ffst");
        LOGGER.debug(first.toDot());

        Map<State, State> smap = new HashMap<>();

        for (State s : snd.vertexSet()) {
            smap.put(s, first.createNewState(s));
        }

        for (Transition t : snd.edgeSet()) {
            first.addTransition(new Transition(smap.get(t.getSource()), smap.get(t
                    .getTarget()), t.getLabel().clone()));
        }

        first.addTransition(new Transition(end, smap.get(snd.start)));

        if (rmaccept)
            end.setKind(State.Kind.NORMAL);

        return first.postProcess();
    }


    public BasicAutomaton intersect(BasicAutomaton a) {


        BasicAutomaton ret = provider.getNewAutomaton();

        LinkedList<Tuple<State, State>> worklist = new LinkedList<>();


        Map<State, State> smap = new HashMap<>();

        smap.put(a.getStart(), ret.getStart());

        worklist.add(new Tuple<>(this.start, a.start));

        Set<Tuple<State, State>> visited = new HashSet<>();

        while (!worklist.isEmpty()) {

            Tuple<State, State> s = worklist.pop();

            if (visited.contains(s)) {
                continue;
            }

            visited.add(s);

            for (Transition fst : outgoingEdgesOf(s.getKey())) {

                for (Transition snd : a.outgoingEdgesOf(s.getVal())) {

                    TransitionLabel lbl = fst.getLabel().isect(snd.getLabel());

                    if (lbl != null) {

                        if (!smap.containsKey(snd.getSource()))
                            smap.put(snd.getSource(), ret.createNewState(snd
                                    .getSource()));

                        if (!smap.containsKey(snd.getTarget()))
                            smap.put(snd.getTarget(), ret.createNewState(snd
                                    .getTarget()));


                        ret.addTransition(new Transition(smap.get(snd.getSource
                                ()), smap.get(snd.getTarget()), lbl));

                        worklist.add(new Tuple<>(fst.getTarget(), snd.getTarget()));
                    }
                }

            }
        }


        return ret.postProcess();
    }

    public BasicAutomaton union(BasicAutomaton other) {


        BasicAutomaton ret = provider.getNewAutomaton();
        Map<State, State> smap1 = new HashMap<>();
        Map<State, State> smap2 = new HashMap<>();

        for (State s : this.vertexSet()) {
            if (!smap1.containsKey(s)) {
                smap1.put(s, ret.createNewState(s));
            }
        }

        for (State s : other.vertexSet()) {
            if (!smap2.containsKey(s)) {
                smap2.put(s, ret.createNewState(s));
            }
        }

        for (Transition e : this.edgeSet()) {
            Transition tn = new Transition(smap1.get(e.getSource()), smap1.get
                    (e.getTarget()), e.getLabel().clone());
            ret.addTransition(tn);
        }

        for (Transition e : other.edgeSet()) {
            Transition tn = new Transition(smap2.get(e.getSource()), smap2.get
                    (e.getTarget()), e.getLabel().clone());
            ret.addTransition(tn);
        }


        ret.addTransition(new Transition(ret.start, smap1.get(this.start)));
        ret.addTransition(new Transition(ret.start, smap2.get(other.start)));

        LOGGER.debug("RETT ");

        LOGGER.debug(ret.toDot());

        LOGGER.debug("UNION");
        //return ret;

        return ret.postProcess();
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


    public BasicAutomaton optional() {
        return this.union(new BasicAutomaton(true));
    }

    public BasicAutomaton star() {
        BasicAutomaton opt = optional();

        for (State a : opt.getAcceptStates()) {
            opt.addTransition(new Transition(a, opt.start));
        }
        return opt.postProcess();
    }

    public BasicAutomaton repeat(int min, int max) {

        BasicAutomaton pat = new BasicAutomaton(this);
        BasicAutomaton tauto = new BasicAutomaton();

        if (min == 0) {
            tauto = pat.optional();
        }

        for (int i = 0; i < min; i++) {
            tauto = tauto.concat(pat);
        }

        for (int i = 0; i < max - min; i++) {
            tauto = tauto.concat(pat, false);
        }

        return tauto.postProcess();
    }

    public BasicAutomaton plus() {
        BasicAutomaton pat = new BasicAutomaton(this);
        return pat.concat(pat.star(), false);
    }

    public BasicAutomaton append(char c) {
        LOGGER.debug("append 1");
        return append(new CharRange(c, c));
    }

    public BasicAutomaton append(char min, char max) {
        LOGGER.debug("append 2");
        return append(new CharRange(min, max));
    }

    public BasicAutomaton complement() {
        // @TODO implement

        return null;
    }

    public BasicAutomaton minus(Automaton other) {
        // @TODO implement

        return null;
    }

    private void collapseStates(Predicate<State> p) {

        Set<State> fs;

        do {
            fs = vertexSet().stream()
                    .filter(p).collect(Collectors.toSet());

            merge(fs);

        } while (!fs.isEmpty() && fs.size() > 1);
    }


    private void removeUnreachableStates() {
        AutomatonSlicerForward fw = new AutomatonSlicerForward(this);
        AutomatonSlicerBackward bw = new AutomatonSlicerBackward(this);
        Collection<State> chop = fw.slice(this.start);
        chop.retainAll(bw.slice(this.getAcceptStates()));
        Set<State> vertices = new HashSet<>(vertexSet());
        vertices.removeAll(chop);
        removeAllVertices(vertices);
    }

    public void checkTransitions() {

        for(State s : vertexSet()) {
            checkForRedundatTransitions(s);
        }
    }


    private void checkForRedundatTransitions(State s) {


        Set<Transition> toAdd = new HashSet<>();
        Set<Transition> toRm = new HashSet<>();
        Set<Transition> sorted = getSortedTransitions(s);

        Transition prev = null;

        for(Transition t : sorted) {

            if(prev == null)
                prev = t;

            if(prev.getLabel().contains(t.getLabel()) && !prev.getLabel()
                    .equals(t.getLabel())) {

                Collection<TransitionLabel> lbl = prev.getLabel().minus(t
                        .getLabel());

                toAdd.add(new Transition(prev.getSource(), prev.getTarget(),
                        t.getLabel().clone()));


                toRm.add(prev);
                LOGGER.debug("rm {}", prev);

                for(TransitionLabel l : lbl) {
                    toAdd.add(new Transition(prev.getSource(), prev.getTarget(), l));
                }
            } else {
                prev = t;
            }
        }

        removeAllEdges(toRm);
        addTransitions(toAdd);
    }


    private BasicAutomaton postProcess() {
        BasicAutomaton a = determinize();
        a.minimize();
        a.checkTransitions();
        return a.determinize();
    }


    private Set<Transition> getSortedTransitions(State s) {
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


    /**
     * generate DFA from NFA by means of subset construction
     *
     * @return
     */
    public BasicAutomaton determinize() {

        LOGGER.debug(this.toDot());

        BasicAutomaton dfa = provider.getNewAutomaton();

        LOGGER.debug(this.toDot());

        Map<State, Set<State>> eclosure = getEpsilonClosure();

        LOGGER.debug("determinze");
        LOGGER.debug("E-closure {}", eclosure);

        Map<Set<State>, State> nstat = new HashMap<>();

        nstat.put(eclosure.get(start), dfa.start);

        if (eclosure.get(start).stream().anyMatch(s -> s.isAccept())) {
            dfa.start.setKind(State.Kind.ACCEPT);
        }

        _determinize(eclosure.get(start), new HashSet<>(), nstat,
                getEpsilonClosure(),
                this,
                dfa);

        return dfa;

    }


    private static void _determinize(Set<State> vdfastate,
                                     Set<Set<State>> visited,
                                     Map<Set<State>, State> nstat,
                                     Map<State, Set<State>> eclosure,
                                     BasicAutomaton nfa,
                                     BasicAutomaton dfa) {



        if (visited.contains(vdfastate))
            return;

        visited.add(vdfastate);

        Set<State> vdfaclosure = vdfastate
                .stream()
                .map(v -> eclosure.get(v))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        // get transition map for old automaton
        Map<TransitionLabel, Set<State>> m = nfa
                .getCombinedTransitionMap(vdfaclosure, t -> !t.isEpsilon());

        LOGGER.debug("%%%");
        LOGGER.debug("VV {}", m.values());
        // create (if necessary) corresponding states in the new automaton
        for (Set<State> ss : m.values()) {

            LOGGER.debug("T {}", ss);

            State.Kind kind = ss.stream()
                    .map(eclosure::get)
                    .flatMap(Collection::stream)
                    .anyMatch(e -> e.isAccept()) ? State.Kind.ACCEPT : State.Kind.NORMAL;


            LOGGER.debug("SS {} :: {}", ss, kind);
            if (!nstat.containsKey(ss)) {
                nstat.put(ss, dfa.createNewState(kind, ss));
            }
        }


        for (Map.Entry<TransitionLabel, Set<State>> e : m.entrySet()) {

            dfa.addTransition(new Transition(nstat.get(vdfastate), nstat.get
                    (e.getValue()), e.getKey().clone()));

        }

        for (Set<State> nxt : m.values()) {
            //if(!nstat.containsKey(nxt))
            _determinize(nxt, visited, nstat, eclosure, nfa, dfa);
        }

    }


    private Map<TransitionLabel, Set<State>> getCombinedTransitionMap
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



    public BasicAutomaton append(TransitionLabel r) {

        assert this.provider != null;
        BasicAutomaton a = provider.getNewAutomaton(this);
        assert a.start != null;
        assert a.provider != null;


        LOGGER.debug("vs {}", a.vertexSet().size());

        LOGGER.debug("this");
        LOGGER.debug(this.toDot());
        LOGGER.debug("append");
        LOGGER.debug(a.toDot());

        if (a.isEmpty()) {

            LOGGER.debug("1");
            State n = a.createNewState(State.Kind.ACCEPT);
            LOGGER.debug("2");
            a.addTransition(new Transition(a.start, n, r.clone()));

        } else {

            State vend = a.addVirtualEnd();

            LOGGER.debug("vend");

            LOGGER.debug(a.toDot());

            a.incomingEdgesOf(vend).stream().forEach(x -> x.setLabel(r.clone
                    ()));

        }

        assert a.provider != null;
        return a.postProcess();
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

    private Map<State, Set<State>> getEpsilonClosure() {

        Map<State, Set<State>> eclosure = new HashMap<>();

        for (State s : vertexSet()) {
            _computeEpsilonClosureForState(s, eclosure);
        }

        return eclosure;
    }


    /**
     * generate NFA from Epsilon-NFA
     *
     * @return
     */
    public BasicAutomaton eliminateEpsilons() {

        if (edgeSet().stream().filter(e -> e.isEpsilon()).count() == 0) {
            return new BasicAutomaton(this);
        }

        Map<State, Set<State>> emap = getEpsilonClosure();

        Collection<Transition> ntrans = new HashSet<>();

        Set<State> naccept = new HashSet<>();

        for (Map.Entry<State, Set<State>> cl : emap.entrySet()) {

            State stat = cl.getKey();
            Set<State> closure = cl.getValue();

            for (State s : closure) {

                boolean accept = closure.stream().filter(x -> x
                        .isAccept()).count() > 0;

                if (accept) {
                    naccept.addAll(closure);
                }

                Set<Transition> trans = outgoingEdgesOf(s).stream().filter(e
                        -> !e.isEpsilon()).collect(Collectors.toSet());


                for (Transition t : trans) {

                    Set<State> reachable = emap.get(t.getTarget());

                    for (State reach : reachable) {
                        LOGGER.debug("trans {} --> {}", s, reach);
                        ntrans.add(new Transition(stat, reach, t.getLabel()
                                .clone()));
                    }

                }
            }

        }

        for (Transition t : ntrans) {
            if (naccept.contains(t.getSource()))
                t.getSource().setKind(State.Kind.ACCEPT);

            if (naccept.contains(t.getTarget()))
                t.getTarget().setKind(State.Kind.ACCEPT);
        }


        BasicAutomaton a = new BasicAutomaton(start, ntrans, this.provider);


        Set<State> accepts = a.getAcceptStates().stream().filter(v -> a
                .outgoingEdgesOf(v).size() == 0).collect(Collectors.toSet());

        State first = null;

        for (State s : accepts) {

            LOGGER.debug("id {} {}", first, s);
            if (first == null) {
                first = s;
                continue;
            }

            a.merge(first, s);
        }

        return a;
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


}
