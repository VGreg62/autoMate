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

package com.github.julianthome.automate.ext;

import com.github.julianthome.automate.core.Automaton;
import com.github.julianthome.automate.core.AutomatonFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTrackAutomaton {

    final static Logger LOGGER = LoggerFactory.getLogger(TestTrackAutomaton.class);


    private Automaton getSimpleAutomaton0(String name) {


        Automaton a1 = AutomatonFactory.getInstance().getNewAutomaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');


        Automaton a2 = AutomatonFactory.getInstance().getNewAutomaton();

        Automaton a3 = AutomatonFactory.getInstance().getNewAutomaton();
        a3 = a3.append('x');
        a3 = a3.append('y');
        a3 = a3.append('z');


        Automaton a = a1.union(a2).union(a3);

        a.setName(name);
        return a;
    }

    @Test
    public void getSimpleAutomaton() {
        TrackAutomaton ma1 = new TrackAutomaton("a");
        TrackAutomaton ma2 = new TrackAutomaton("b");
        TrackAutomaton ma3 = ma1.union(ma2);
        TrackAutomaton ma4 = new TrackAutomaton(getSimpleAutomaton0("test"));
        ma3 = ma4.union(ma3);

        LOGGER.debug(ma3.toDot());

    }




}


