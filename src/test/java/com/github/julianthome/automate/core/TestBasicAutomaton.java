package com.github.julianthome.automate.core;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by julian on 30/04/2017.
 */
public class TestBasicAutomaton {

    final static Logger LOGGER = LoggerFactory.getLogger(TestBasicAutomaton.class);

    private Automaton getSimpleAutomaton() {

        Automaton a1 = new BasicAutomaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');


        Automaton a2 = new BasicAutomaton();

        Automaton a3 = new BasicAutomaton();
        a3 = a3.append('x');
        a3 = a3.append('y');
        a3 = a3.append('z');


        Automaton a = a1.union(a2).union(a3);
        return a;
    }

    @Test
    public void testAppend() {
        Automaton a = new BasicAutomaton();
        a = a.append('a', 'b');
        a = a.append('g');
        a = a.append('d');

        LOGGER.debug(a.toDot());

        Assert.assertTrue(a.match("agd"));
        Assert.assertTrue(a.match("bgd"));

        Assert.assertFalse(a.match(""));
        Assert.assertFalse(a.match("dgfc"));

    }


    @Test
    public void testConcat() {
        Automaton a = new BasicAutomaton().append('h', 'z').append('e').append('l');
        Automaton b = new BasicAutomaton().append('l').append('l').append('o');
        Automaton c = a.concat(b);

        Assert.assertFalse(c.match("gelllo"));
        Assert.assertTrue(c.match("melllo"));
    }

    @Test
    public void testUnion() {

        Automaton a = getSimpleAutomaton();

        LOGGER.debug(a.toDot());

        Assert.assertTrue(a.match("bte"));
        Assert.assertTrue(a.match("xyz"));

        Assert.assertFalse(a.match(""));
        Assert.assertFalse(a.match("asfasdfasd"));
        Assert.assertFalse(a.match("b"));
        LOGGER.debug(a.toDot());

    }

    @Test
    public void testIntersection() {

        Automaton a = getSimpleAutomaton();

        Automaton a4 = new BasicAutomaton();
        a4 = a4.append('x');
        a4 = a4.append('y');
        a4 = a4.append('z');

        Automaton isect = a.intersect(a4);

        LOGGER.debug(isect.toDot());


        Assert.assertTrue(isect.match("xyz"));
        Assert.assertFalse(isect.match(""));

        Assert.assertFalse(isect.match("asfasdfasd"));
        Assert.assertFalse(isect.match("b"));
    }


    @Test
    public void testKleene() {

        Automaton a = getSimpleAutomaton();

        Automaton kleene = a.star();

        Assert.assertTrue(kleene.match("xyzxyzxyzate"));
        Assert.assertTrue(kleene.match("ateateatexyz"));
        Assert.assertTrue(kleene.match("bte"));
        Assert.assertFalse(kleene.match("btee"));
        Assert.assertFalse(kleene.match("xxxxx"));

        LOGGER.debug(kleene.toDot());
    }

    @Test
    public void testOptional() {

        Automaton a1 = new BasicAutomaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');


        Automaton a2 = new BasicAutomaton();
        a2 = a2.append('x');
        a2 = a2.append('y');
        a2 = a2.append('z');


        Automaton a = a1.union(a2);

        Assert.assertTrue(a.match("ate"));
        Assert.assertTrue(a.match("xyz"));
        Assert.assertFalse(a.match(""));

        a = a.optional();

        Assert.assertTrue(a.match("ate"));
        Assert.assertTrue(a.match("xyz"));
        Assert.assertTrue(a.match(""));

        LOGGER.debug(a.toDot());
    }

    @Test
    public void testPlus() {

        Automaton a1 = new BasicAutomaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');


        Automaton a2 = new BasicAutomaton();
        a2 = a2.append('x');
        a2 = a2.append('y');
        a2 = a2.append('z');


        Automaton a = a1.union(a2);

        Assert.assertTrue(a.match("ate"));
        Assert.assertTrue(a.match("xyz"));
        Assert.assertFalse(a.match("ateate"));
        Assert.assertFalse(a.match("xyzxyz"));


        a = a.plus();

        LOGGER.debug(a.toDot());

        Assert.assertTrue(a.match("ate"));
        Assert.assertTrue(a.match("xyz"));
        Assert.assertTrue(a.match("ateate"));
        Assert.assertTrue(a.match("xyzxyzateate"));
    }


    @Test
    public void testRepeat() {

        Automaton a1 = new BasicAutomaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');

        Automaton a2 = new BasicAutomaton();
        a2 = a2.append('x');
        a2 = a2.append('y');
        a2 = a2.append('z');

        Automaton a = a1.union(a2);

        a = a.repeat(2,5);

        LOGGER.debug(a.toDot());


        Assert.assertFalse(a.match("ate"));
        Assert.assertFalse(a.match("xyz"));
        Assert.assertTrue(a.match("atexyz"));
        Assert.assertTrue(a.match("xyzate"));
        Assert.assertTrue(a.match("atexyzatexyz"));
        Assert.assertTrue(a.match("xyzateatexyz"));

        Assert.assertTrue(a.match("atexyzatexyzate"));
        Assert.assertTrue(a.match("xyzateatexyzate"));

        Assert.assertFalse(a.match("atexyzatexyzatexyz"));
        Assert.assertFalse(a.match("xyzateatexyzatexyz"));

    }


    @Test
    public void testDeterminize() {
        Automaton a = getSimpleAutomaton();

        Automaton det = a.determinize();

        LOGGER.debug(det.toDot());
    }


    @Test
    public void testExpansion() {
        Automaton a = getSimpleAutomaton();
        a = a.expand();

        LOGGER.debug(a.toDot());
    }

    @Test
    public void testEmptyString() {
        Automaton a = new BasicAutomaton(true);

        LOGGER.debug(a.toDot());

        Assert.assertTrue(a.match(""));
    }

    @Test
    public void testMinimization() {
        Automaton a = new BasicAutomaton(true);

        Automaton a1 = new BasicAutomaton();
        a1 = a1.append('a', 'e');
        a1 = a1.append('t');
        a1 = a1.append('e');

        Automaton a2 = new BasicAutomaton();
        a2 = a2.append('x');
        a2 = a2.append('y');
        a2 = a2.append('z');

        Automaton a3 = new BasicAutomaton();
        a3 = a3.append('b', 'c');
        a3 = a3.append('u');
        a3 = a3.append('t');

        Automaton a4 = new BasicAutomaton();
        a4 = a4.append('x');
        a4 = a4.append('0');
        a4 = a4.append('0');

        Automaton a5 = a1.union(a2).union(a3).union(a4);

        LOGGER.debug(a5.toDot());


        a5.minimize();

        LOGGER.debug(a5.toDot());

        a5.checkTransitions();
        LOGGER.debug(a5.toDot());

        Automaton aet = a5.determinize();
        LOGGER.debug(aet.toDot());
    }

    @Test
    public void testMatch() {

        BasicAutomatonFactory fact = BasicAutomatonFactory.getInstance();
        BasicAutomaton c = fact.getAllAccepting();


        BasicAutomaton x = fact.getNewAutomaton();
        x = x.append('x');


        BasicAutomaton concat = c.concat(x);

        Assert.assertTrue(concat.match("Asterix"));
        Assert.assertTrue(concat.match("Idefix"));
        Assert.assertTrue(concat.match("Metusalix"));
        Assert.assertTrue(concat.match("Unix"));
        Assert.assertTrue(concat.match("Linux"));

        Assert.assertFalse(concat.match("Asteri"));
        Assert.assertFalse(concat.match("Idefi"));
        Assert.assertFalse(concat.match("Metusali"));
        Assert.assertFalse(concat.match("Uni"));
        Assert.assertFalse(concat.match("Linu"));
    }


}


