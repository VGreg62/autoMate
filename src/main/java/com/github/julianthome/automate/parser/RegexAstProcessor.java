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
import com.github.julianthome.automate.core.Automaton;
import com.github.julianthome.automate.core.AutomatonFactory;
import com.github.julianthome.automate.core.AutomatonProvider;
import com.github.julianthome.automate.utils.EscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.inmemantlr.tree.Ast;
import org.snt.inmemantlr.tree.AstNode;
import org.snt.inmemantlr.tree.AstProcessor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexAstProcessor extends AstProcessor<AbstractAutomaton, AbstractAutomaton> {

    final static Logger LOGGER = LoggerFactory.getLogger(RegexParser.class);


    private AutomatonProvider provider = null;

    public RegexAstProcessor(Ast ast) {
        this(ast, AutomatonFactory.getInstance());
    }

    public RegexAstProcessor(Ast ast, AutomatonProvider provider) {
        super(ast);
        this.provider = provider;
    }

    @Override
    public AbstractAutomaton getResult() {
        return smap.get(ast.getRoot());
    }

    @Override
    protected void initialize() {

    }

    private AbstractAutomaton concatChildren(AstNode n) {
        LOGGER.debug("expr");
        AbstractAutomaton cc = null;
        for (AstNode c : n.getChildren()) {
            if (cc == null) {
                cc = smap.get(c);
                continue;
            }
            cc = cc.concat(smap.get(c));
        }
        return cc;
    }

    private AbstractAutomaton unifyChildren(AstNode n) {
        LOGGER.debug("expr");
        AbstractAutomaton cc = null;
        for (AstNode c : n.getChildren()) {
            if (cc == null) {
                cc = smap.get(c);
                continue;
            }
            cc = cc.union(smap.get(c));
        }
        return cc;
    }

    @Override
    protected void process(AstNode n) throws ParserException {

        LOGGER.debug("++++++++++++++++++++++++++ {}:{}", n.getRule(), n.getId());

        switch (n.getRule()) {

            case "atom":
                LOGGER.debug("atom {}", n.getLabel());
                if (!n.hasChildren()) {
                    if (n.getLabel().equals(".")) {

                        LOGGER.debug("basic");
                        AbstractAutomaton a = provider.getAnyAccepting();
                        smap.put(n, a);
                    }
                } else {
                    assert n.getChildren().size() == 1;
                    simpleProp(n);
                }

                break;
            case "literal":
            case "cc_literal":
                if(n.getChildren().size() == 0) {
                    Automaton a = AutomatonFactory.getInstance().getNewAutomaton();

                    String lbl = EscapeUtils.unescapeSpecialCharacters(n.getLabel());

                    assert lbl.length() == 1;
                    LOGGER.debug("LBL {}", lbl);

                    a = a.append(lbl.charAt(0));

                    LOGGER.debug(a.toDot());
                    smap.put(n, a);
                } else {
                    assert n.getChildren().size() == 1;
                    simpleProp(n);
                }
                break;
            case "root":
            case "number":
            case "shared_literal":
            case "alternation":
                if(n.getChildren().size() > 1) {
                    smap.put(n, unifyChildren(n));
                } else {
                    //LOGGER.debug(this.ast.toDot());
                    LOGGER.debug("child {}", n.getChildren().size());
                    LOGGER.debug("id {} : {}", n.getId(), n.getLabel());
                    assert n.getChildren().size() <= 1;

                    if(n.getChildren().size() > 1)
                        throw new ParserException("Parsing error for token "
                                + n.getLabel());

                    simpleProp(n);
                }
                break;
            case "letter":
            case "digit":
                AbstractAutomaton a = provider.getNewAutomaton();
                a = a.append(n.getLabel().charAt(0));
                smap.put(n, a);
                break;
            case "cc_atom":
                String lbl = n.getLabel();
                LOGGER.debug("cc atom {}", lbl);

                if (lbl.length() == 3) {
                    AbstractAutomaton na = provider.getNewAutomaton();


                    char min = lbl.charAt(0);
                    char max = lbl.charAt(2);

                    LOGGER.debug("min {} max {}", min, max);

                    if (min > max) {
                        throw new ParserException("malformed range:" + n
                                .getLabel());
                    }

                    na = na.append(lbl.charAt(0), lbl.charAt(2));
                    smap.put(n, na);
                } else {
                    simpleProp(n);
                }
                break;

            case "character_class":

                LOGGER.debug("character class {}", n.getLabel());


                AbstractAutomaton na = null;

                if (n.getChildren().size() > 1) {

                    for (AstNode c : n.getChildren()) {

                        if (na == null) {
                            na = smap.get(c);
                            continue;
                        }

                        LOGGER.debug("C {}:{}", c.getLabel(), c.getId());

                        na = na.union(smap.get(c));

                    }
                    assert na != null;
                    LOGGER.debug("NA");
                    LOGGER.debug(na.toDot());
                    smap.put(n, na);
                } else {
                    simpleProp(n);
                }
                break;
            case "expr":
                if(n.getChildren().size() > 1) {
                    if(!n.getFirstChild().getLabel().equals("^")) {
                        smap.put(n, concatChildren(n));
                    } else {
                        // negation
                        assert n.getChildren().size() > 1;
                        List<AstNode> childs = n.getChildren();
                        AbstractAutomaton concat = provider.getNewAutomaton();
                        for(int i = 1; i < n.getChildren().size(); i++) {
                            concat = concat.concat(smap.get(n.getChild(i)));
                        }
                        concat = concat.complement();
                        smap.put(n, concat);
                    }
                } else {
                    assert n.getChildren().size() == 1;
                    simpleProp(n);
                }
                break;
            case "element":

                LOGGER.debug("handle element " + n.getLabel());

                if (n.getChildren().size() == 1) {
                    simpleProp(n);
                } else if (n.getChildren().size() == 2) {

                    AstNode last = n.getChildren().get(1);
                    AstNode first = n.getChildren().get(0);

                    LOGGER.debug("first:{}, last:{}",first,last);
                    String quant = last.getLabel();

                    assert smap.containsKey(first);

                    AbstractAutomaton fauto = smap.get(first);

                    //LOGGER.debug("fauto {}", fauto.toDot());

                    assert fauto != null;

                    Pattern pattern = Pattern.compile("\\{([0-9]*),?([0-9]*)\\}");
                    Matcher matcher = pattern.matcher(quant);


                    if (last != null &&
                            last.getRule().equals("quantifier")) {

                        if (quant.matches("(\\*|\\+|\\?)")) {
                            switch (quant) {
                                case "*":
                                    smap.put(n, fauto.star());
                                    break;
                                case "+":
                                    smap.put(n, fauto.plus());
                                    break;
                                case "?":
                                    smap.put(n, fauto.optional());
                                    break;
                            }
                        } else if (matcher.matches()) {

                            int min = -1;
                            int max = -1;

                            boolean nomin = false;
                            boolean nomax = false;

                            if (matcher.group(1) != null && !matcher.group(1)
                                    .isEmpty()) {
                                min = Integer.parseInt(matcher.group(1));
                            } else {
                                nomin = true;
                            }


                            if (matcher.group(2) != null && !matcher.group(2)
                                    .isEmpty()) {
                                max = Integer.parseInt(matcher.group(2));
                            } else {
                                nomax = true;
                            }

                            if(nomin && nomax) {
                                throw new ParserException("malformed " +
                                        "quantifier: " + quant);
                            } else if (nomin) {
                                smap.put(n, fauto.repeatMax(max));
                            } else if (nomax) {
                                smap.put(n, fauto.repeat(min, min));
                            } else {
                                smap.put(n, fauto.repeat(min, max));
                            }

                        } else {
                            throw new ParserException("malformed quantifier: " +
                                    "" + quant);
                        }
                    }
                }

                break;
        }


    }
}
