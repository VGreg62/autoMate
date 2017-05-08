package com.github.julianthome.automate.core;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestLabelledAutomaton {

    final static Logger LOGGER = LoggerFactory.getLogger(TestLabelledAutomaton.class);

    @Test
    public void testSimpleLabelled() {

        LabelledAutomaton a1 = new LabelledAutomaton();
        a1 = (LabelledAutomaton)a1.append('a', 'b');
        a1 = (LabelledAutomaton)a1.append('t');
        a1 = (LabelledAutomaton)a1.append('e');

        a1.labelAllStates("a1");


        LabelledAutomaton a2 = new LabelledAutomaton();
        a2 = (LabelledAutomaton)a2.append('x');
        a2 = (LabelledAutomaton)a2.append('y');
        a2 = (LabelledAutomaton)a2.append('z');
        a2.labelAllStates("a2");


        LabelledAutomaton a3 = new LabelledAutomaton();
        a3 = (LabelledAutomaton)a3.append('b');
        a3 = (LabelledAutomaton)a3.append('z');
        a3 = (LabelledAutomaton)a3.append('z');
        a3.labelAllStates("a3");


        LOGGER.debug(a2.toDot());

        Automaton a = a1.union(a2).union(a3);

        a.minimize();
        LOGGER.debug(a.toDot());
    }

    @Test
    public void testLabelledIntersection() {

        // concat(c,"x")

        LabelledAutomatonFactory fact = LabelledAutomatonFactory.getInstance();
        LabelledAutomaton c = fact.getAllAccepting();
        c.labelAllStates("c");

        LabelledAutomaton x = fact.getNewAutomaton();
        x = (LabelledAutomaton)x.append('x');
        x.labelAllStates("x");

        LabelledAutomaton a = fact.getAllAccepting();
        a.labelAllStates("a");

        LabelledAutomaton dash = fact.getNewAutomaton();
        dash = (LabelledAutomaton)dash.append('-');
        dash.labelAllStates("-");

        LabelledAutomaton b = fact.getAllAccepting();
        b.labelAllStates("b");


        // len(c)

        LabelledAutomaton lena = fact.getAnyAccepting();
        lena = (LabelledAutomaton)lena.repeatMin(7);

        lena.labelAllStates("lena");
        lena.labelAllStates("7");

        ((LabelledState)lena.getStart()).getLabels().add("c");



        LOGGER.debug(a.toDot());
        LOGGER.debug(lena.toDot());
        LabelledAutomaton isectalena = (LabelledAutomaton)a.intersect(lena);
        LOGGER.debug("LLLLENA >>");
        LOGGER.debug(isectalena.toDot());


//        LabelledAutomaton concat = (LabelledAutomaton)c.concat(x);
//
//        Assert.assertTrue(concat.match("Asterix"));
//        Assert.assertTrue(concat.match("Idefix"));
//        Assert.assertTrue(concat.match("Metusalix"));
//        Assert.assertTrue(concat.match("Unix"));
//        Assert.assertTrue(concat.match("Linux"));
//
//        Assert.assertFalse(concat.match("Asteri"));
//        Assert.assertFalse(concat.match("Idefi"));
//        Assert.assertFalse(concat.match("Metusali"));
//        Assert.assertFalse(concat.match("Uni"));
//        Assert.assertFalse(concat.match("Linu"));
//



    }

}


