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

package com.github.julianthome.automate.parser;


import com.github.julianthome.automate.core.AbstractAutomaton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.inmemantlr.GenericParser;
import org.snt.inmemantlr.exceptions.AstProcessorException;
import org.snt.inmemantlr.exceptions.CompilationException;
import org.snt.inmemantlr.exceptions.IllegalWorkflowException;
import org.snt.inmemantlr.listener.DefaultTreeListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum RegexParser {

    INSTANCE;

    final static Logger LOGGER = LoggerFactory.getLogger(RegexParser.class);

    private static GenericParser gp = null;
    private static DefaultTreeListener dlist = null;
    private static String gfile = RegexParser.class.getClassLoader().getResource
            ("Regex.g4")
            .getFile();

    private static Set<String> filter  = new HashSet<>(Arrays.asList(new String []{
            "alternation", "expr", "literal",
            "quantifier", "atom", "letter", "number", "element",
            "character_class", "cc_atom", "cc_literal", "digit"
    }));



    static{
        LOGGER.debug("gfile {}", gfile);

        File f = new File(gfile);
        try {
            gp = new GenericParser(f);
        } catch (FileNotFoundException e) {
            LOGGER.debug(e.getMessage());
            System.exit(-1);
        }

        dlist = new DefaultTreeListener(s -> filter.contains(s));

        gp.setListener(dlist);
        try {
            gp.compile();
        } catch (CompilationException e) {
            LOGGER.debug(e.getMessage());
            System.exit(-1);
        }
    }



    public AbstractAutomaton getAutomaton(String rexp) {

        RegexAstProcessor rap = null;
        AbstractAutomaton ret = null;

        try {
            gp.parse(rexp);

            //LOGGER.debug(dlist.getAst().toDot());

            rap = new RegexAstProcessor(dlist.getAst());
        } catch (IllegalWorkflowException e) {
            System.err.println("DNF transformer- intial parsing error");
            return null;
        }


        LOGGER.debug(dlist.getAst().toDot());


        try {
            rap.process();
            ret = rap.getResult();
        } catch (AstProcessorException e) {
            LOGGER.error("cannot construct automaton {}", e.getMessage());
            System.exit(-1);
        }

        return ret;

    }

}
