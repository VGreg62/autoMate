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

import com.github.julianthome.automate.parser.RegexParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by julian on 08/05/2017.
 */
public class AutomatonFactory implements AutomatonProvider<Automaton> {

    final static Logger LOGGER = LoggerFactory.getLogger(AutomatonFactory.class);

    private static AutomatonFactory fact = null;


    public static AutomatonFactory getInstance() {
        if(fact == null) {
            fact = new AutomatonFactory();
        }
        return fact;
    }

    @Override
    public Automaton getAllAccepting() {
        Automaton all = new Automaton();
        all.start.setKind(State.Kind.ACCEPT);
        all.addTransition(new Transition(all.start,all.start, CharRange.ANY.clone()));
        return all;
    }

    @Override
    public Automaton getAnyAccepting() {
        Automaton any = new Automaton();
        State acc = any.createNewState(State.Kind.ACCEPT);
        any.addTransition(new Transition(any.start, acc, CharRange.ANY.clone()));
        return any;
    }

    @Override
    public Automaton getNewAutomaton() {
        return new Automaton();
    }

    @Override
    public Automaton getNewAutomaton(Automaton a) {
        return new Automaton(a);
    }

    @Override
    public Automaton getEmtpyAutomaton() {
        return new Automaton(true);
    }

    @Override
    public Automaton getNewAutomaton(State start, Collection<Transition> t) {
        return new Automaton(start, t);
    }

    @Override
    public Automaton getNewAutomaton(String rexp) {
        return (Automaton)RegexParser.INSTANCE.getAutomaton(rexp);
    }

    @Override
    public Automaton getNewNamedAutomaton(String name, String rexp) {
        return null;
    }

}
