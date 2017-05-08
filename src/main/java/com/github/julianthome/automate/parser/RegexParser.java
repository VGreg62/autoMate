package com.github.julianthome.automate.parser;


import com.github.julianthome.automate.core.Automaton;
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



    public Automaton getAutomaton(String rexp) {

        RegexAstProcessor rap = null;
        Automaton ret = null;

        try {
            gp.parse(rexp);

            LOGGER.debug(dlist.getAst().toDot());

            rap = new RegexAstProcessor(dlist.getAst());
        } catch (IllegalWorkflowException e) {
            System.err.println("DNF transformer- intial parsing error");
            return null;
        }

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
