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

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMatch {

    final static Logger LOGGER = LoggerFactory.getLogger(TestMatch.class);

    private Automaton getSimpleAutomaton() {

        Automaton a1 = new Automaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');


        Automaton a2 = new Automaton();

        Automaton a3 = new Automaton();
        a3 = a3.append('x');
        a3 = a3.append('y');
        a3 = a3.append('z');


        Automaton a = a1.union(a2).union(a3);
        return a;
    }

    @Test
    public void testAppend() {
        Automaton a = new Automaton();
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
        Automaton a = new Automaton().append('h', 'z').append('e').append('l');
        Automaton b = new Automaton().append('l').append('l').append('o');
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

        Automaton a4 = new Automaton();
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

        Automaton a1 = new Automaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');


        Automaton a2 = new Automaton();
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

        Automaton a1 = new Automaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');


        Automaton a2 = new Automaton();
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

        Automaton a1 = new Automaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');

        Automaton a2 = new Automaton();
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
    public void testComplement() {

        Automaton a = new Automaton();
        a = a.append('a', 'b');
        a = a.append('t');
        a = a.append('e');

        a = a.complement();

        Assert.assertFalse(a.match("ate"));
        Assert.assertFalse(a.match("bte"));
        Assert.assertTrue(a.match("xyzate"));
        Assert.assertTrue(a.match("atexyzatexyz"));
        Assert.assertTrue(a.match("xyzateatexyz"));

        Assert.assertTrue(a.match("atexyzatexyzate"));
        Assert.assertTrue(a.match("xyzateatexyzate"));

        LOGGER.debug(a.toDot());
    }


    @Test
    public void testMinus() {
        Automaton a = getSimpleAutomaton();

        Automaton c = a.complement();

        Automaton isect = a.intersect(c);


        LOGGER.debug(a.toDot());
        LOGGER.debug(c.toDot());
        LOGGER.debug(isect.toDot());

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
        Automaton a = new Automaton(true);

        LOGGER.debug(a.toDot());

        Assert.assertTrue(a.match(""));
    }

    @Test
    public void testMinimization() {
        Automaton a = new Automaton(true);

        Automaton a1 = new Automaton();
        a1 = a1.append('a', 'e');
        a1 = a1.append('t');
        a1 = a1.append('e');

        Automaton a2 = new Automaton();
        a2 = a2.append('x');
        a2 = a2.append('y');
        a2 = a2.append('z');

        Automaton a3 = new Automaton();
        a3 = a3.append('b', 'g');
        a3 = a3.append('u');
        a3 = a3.append('t');

        Automaton a4 = new Automaton();
        a4 = a4.append('x');
        a4 = a4.append('0');
        a4 = a4.append('0');

        Automaton a5 = a1.union(a2).union(a3).union(a4);

        Assert.assertTrue(a5.match("ate"));
        Assert.assertTrue(a5.match("bte"));
        Assert.assertTrue(a5.match("cte"));
        Assert.assertTrue(a5.match("dte"));
        Assert.assertTrue(a5.match("ete"));

        Assert.assertTrue(a5.match("but"));
        Assert.assertTrue(a5.match("dut"));
        Assert.assertTrue(a5.match("eut"));
        Assert.assertTrue(a5.match("fut"));
        Assert.assertTrue(a5.match("gut"));

        Assert.assertTrue(a5.match("x00"));

        Assert.assertFalse(a5.match("gte"));
    }

    @Test
    public void testMatch() {

        AutomatonFactory fact = AutomatonFactory.getInstance();
        Automaton c = fact.getAllAccepting();


        Automaton x = fact.getNewAutomaton();
        x = x.append('x');


        Automaton concat = c.concat(x);

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


