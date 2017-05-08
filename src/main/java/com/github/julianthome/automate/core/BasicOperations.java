package com.github.julianthome.automate.core;


import com.github.julianthome.automate.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class BasicOperations <T extends Automaton> {

    final static Logger LOGGER = LoggerFactory.getLogger(BasicOperations.class);

    private static BasicOperations bop = null;
    private AutomatonProvider<T> provider = null;

    public void init(AutomatonProvider<T> p){
        provider = p;
    }

    public static BasicOperations getInstance() {
        if(bop == null)
            bop = new BasicOperations();

        return bop;
    }

    public T union (T first, T snd) {
        T ret = provider.getNewAutomaton();

        Map<State, State> smap1 = new HashMap<>();
        Map<State, State> smap2 = new HashMap<>();

        Set<State> vs = first.vertexSet();
        for (State s : vs) {
            if (!smap1.containsKey(s)) {
                smap1.put(s, ret.createNewState(s));
            }
        }

        Set<State> os = snd.vertexSet();
        for (State s : os) {
            if (!smap2.containsKey(s)) {
                smap2.put(s, ret.createNewState(s));
            }
        }

        Set<Transition> ftrans = first.edgeSet();

        for (Transition e : ftrans) {
            Transition tn = new Transition(smap1.get(e.getSource()), smap1.get
                    (e.getTarget()), e.getLabel().clone());
            ret.addTransition(tn);
        }

        Set<Transition> ot = snd.edgeSet();

        for (Transition e : ot) {
            Transition tn = new Transition(smap2.get(e.getSource()), smap2.get
                    (e.getTarget()), e.getLabel().clone());
            ret.addTransition(tn);
        }


        ret.addTransition(new Transition(ret.start, smap1.get(first.start)));
        ret.addTransition(new Transition(ret.start, smap2.get(snd.start)));

        LOGGER.debug("RETT ");

        LOGGER.debug(ret.toDot());

        LOGGER.debug("UNION");
        //return ret;

        return postProcess(ret);
    }


    public T intersect(T fst, T snd) {

        T ret = provider.getNewAutomaton();

        LinkedList<Tuple<State, State>> worklist = new LinkedList<>();

        Map<State, State> smap = new HashMap<>();

        smap.put(snd.getStart(), ret.getStart());

        worklist.add(new Tuple<>(fst.start, snd.start));

        Set<Tuple<State, State>> visited = new HashSet<>();

        while (!worklist.isEmpty()) {

            Tuple<State, State> s = worklist.pop();

            if (visited.contains(s)) {
                continue;
            }

            visited.add(s);

            Set<Transition> fstt = fst.outgoingEdgesOf(s.getKey());

            Set<Transition> sndt = snd.outgoingEdgesOf(s.getVal());


            for (Transition tfst : fstt) {

                for (Transition tsnd : sndt) {

                    TransitionLabel lbl = tfst.getLabel().isect(tsnd.getLabel
                            ());

                    if (lbl != null) {

                        if (!smap.containsKey(tsnd.getSource()))
                            smap.put(tsnd.getSource(), ret.createNewState(tsnd
                                    .getSource(), tfst.getSource()));

                        if (!smap.containsKey(tsnd.getTarget()))
                            smap.put(tsnd.getTarget(), ret.createNewState(tsnd
                                    .getTarget(), tfst.getTarget()));


                        ret.addTransition(new Transition(smap.get(tsnd
                                .getSource()), smap.get(tsnd.getTarget()),
                                lbl));

                        worklist.add(new Tuple<>(tfst.getTarget(), tsnd
                                .getTarget()));
                    }
                }

            }
        }


        return postProcess(ret);
    }

    public T concat(T fst, T snd) {
        return concat(fst,snd,true);
    }

    public T concat(T fst, T snd,  boolean rmaccept) {

        if (fst.isEmpty() && snd.isEmpty()) {
            return provider.getEmtpyAutomaton();
        } else if (fst.isEmpty()) {
            return provider.getNewAutomaton(snd);
        } else if (snd.isEmpty()) {
            return provider.getNewAutomaton(fst);
        }


        T a = provider.getNewAutomaton(fst);
        T b = provider.getNewAutomaton(snd);

        State end = a.addVirtualEnd();
        LOGGER.debug("ffst");
        LOGGER.debug(a.toDot());

        Map<State, State> smap = new HashMap<>();

        Set<State> vs = b.vertexSet();

        for (State s : vs) {
            smap.put(s, a.createNewState(s));
        }

        Set<Transition> es = b.edgeSet();
        for (Transition t : es) {
            a.addTransition(new Transition(smap.get(t.getSource()), smap.get(t
                    .getTarget()), t.getLabel().clone()));
        }

        a.addTransition(new Transition(end, smap.get(b.start)));

        if (rmaccept)
            end.setKind(State.Kind.NORMAL);

        return postProcess(a);
    }


    public T optional(T fst) {
        return union(fst,provider.getEmtpyAutomaton());
    }

    public T star(T fst) {
        T opt = optional(fst);

        Set<State> acc = opt.getAcceptStates();

        for (State a : acc) {
            opt.addTransition(new Transition(a, opt.start));
        }
        return postProcess(opt);
    }

    public T repeatMin(T fst, int min) {

        T pat = provider.getNewAutomaton(fst);
        T tauto = provider.getNewAutomaton();

        if (min == 0) {
            tauto = optional(pat);
        }

        for (int i = 0; i < min; i++) {
            tauto = concat(tauto,pat);
        }


        return postProcess(concat(tauto, star(pat)));
    }

    public T repeatMax(T fst, int max) {

        T pat = provider.getNewAutomaton(fst);
        T tauto = provider.getNewAutomaton();

        if (max == 0) {
            T b = provider.getNewAutomaton();
            b.start.setKind(State.Kind.NORMAL);
        }

        for (int i = 0; i < max; i++) {
            tauto = concat(tauto,pat, false);
        }

        return postProcess(tauto);

    }

    public T repeat(T fst, int min, int max) {

        T pat = provider.getNewAutomaton(fst);
        T tauto = provider.getNewAutomaton();

        if (min == 0) {
            tauto = optional(pat);
        }

        for (int i = 0; i < min; i++) {
            tauto = concat(tauto, pat);
        }

        for (int i = 0; i < max - min; i++) {
            tauto = concat(tauto,pat, false);
        }

        return postProcess(tauto);
    }

    public T plus(T fst) {
        T pat = provider.getNewAutomaton(fst);
        return concat(pat, star(pat), false);
    }

    public T append(T fst, char c) {
        LOGGER.debug("append 1");
        return append(fst,new CharRange(c, c));
    }

    public T append(T fst, char min, char max) {
        LOGGER.debug("append 2");
        return append(fst,new CharRange(min, max));
    }

    public T append(T fst, TransitionLabel r) {

        T a = provider.getNewAutomaton(fst);
        assert a.start != null;


        if (a.isEmpty()) {

            LOGGER.debug("1");
            State n = a.createNewState(State.Kind.ACCEPT);
            LOGGER.debug("2");
            a.addTransition(new Transition(a.start, n, r.clone()));

        } else {

            State vend = a.addVirtualEnd();

            LOGGER.debug("vend");

            LOGGER.debug(a.toDot());

            a.incomingEdgesOf(vend).stream().forEach(x -> ((Transition)x)
                    .setLabel(r.clone()));

        }

        return postProcess(a);
    }

    public T determinize(T fst) {

        LOGGER.debug(fst.toDot());

        T dfa = provider.getNewAutomaton();

        Map<State, Set<State>> eclosure = fst.getEpsilonClosure();

        LOGGER.debug("determinze");
        LOGGER.debug("E-closure {}", eclosure);

        Map<Set<State>, State> nstat = new HashMap<>();

        nstat.put(eclosure.get(fst.start), dfa.start);

        if (eclosure.get(fst.start).stream().anyMatch(s -> s.isAccept())) {
            dfa.start.setKind(State.Kind.ACCEPT);
        }

        _determinize(eclosure.get(fst.start), new HashSet<>(), nstat,
                eclosure,
                fst,
                dfa);

        return dfa;

    }


    private void _determinize(Set<State> vdfastate,
                              Set<Set<State>> visited,
                              Map<Set<State>, State> nstat,
                              Map<State, Set<State>> eclosure,
                              Automaton nfa,
                              Automaton dfa) {



        if (visited.contains(vdfastate))
            return;

        visited.add(vdfastate);

        Set<State> vdfaclosure = vdfastate
                .stream()
                .map(v -> eclosure.get(v))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());


        // get transition map for old automaton
        Map<TransitionLabel, Set<State>> m = nfa.getCombinedTransitionMap
                (vdfaclosure, t -> !((Transition)t).isEpsilon());

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


    public T eliminateEpsilons(T fst) {

        Set<Transition> ed = fst.edgeSet();
        if (ed.stream().filter(e -> e.isEpsilon()).count() == 0) {
            return provider.getNewAutomaton(fst);
        }

        Map<State, Set<State>> emap = fst.getEpsilonClosure();

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

                Set<Transition> out = fst.outgoingEdgesOf(s);

                Set<Transition> trans = out.stream().filter(e
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

        T a = provider.getNewAutomaton(fst.start, ntrans);


        Set<State> acc =  a.getAcceptStates();

        Set<State> accepts = acc.stream().filter(v -> a
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

    protected T expand(T fst) {
        T cp = provider.getNewAutomaton(fst);
        cp.addVirtualEnd();
        return cp;
    }

    protected T postProcess(T fst) {
        T a = determinize(fst);
        a.minimize();
        a.checkTransitions();
        return determinize(a);
    }
}
