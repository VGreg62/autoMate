package com.github.julianthome.automate.parser;

import com.github.julianthome.automate.core.AutomatonProvider;
import com.github.julianthome.automate.core.BasicAutomaton;
import com.github.julianthome.automate.core.BasicAutomatonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.inmemantlr.tree.Ast;
import org.snt.inmemantlr.tree.AstNode;
import org.snt.inmemantlr.tree.AstProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by julian on 28/04/2017.
 */
public class RegexAstProcessor extends AstProcessor<BasicAutomaton, BasicAutomaton> {

    final static Logger LOGGER = LoggerFactory.getLogger(RegexParser.class);


    private AutomatonProvider provider = null;

    public RegexAstProcessor(Ast ast) {
        this(ast, BasicAutomatonFactory.getInstance());
    }

    public RegexAstProcessor(Ast ast, AutomatonProvider provider) {
        super(ast);
        this.provider = provider;
    }

    @Override
    public BasicAutomaton getResult() {
        BasicAutomaton a = smap.get(ast.getRoot());
        a.minimize();
        return a;
    }

    @Override
    protected void initialize() {

    }

    private BasicAutomaton concatChildren(AstNode n) {
        LOGGER.debug("expr");
        BasicAutomaton cc = null;
        for (AstNode c : n.getChildren()) {
            if (cc == null) {
                cc = smap.get(c);
                continue;
            }
            cc = cc.concat(smap.get(c));
        }
        return cc;
    }

    private BasicAutomaton unifyChildren(AstNode n) {
        LOGGER.debug("expr");
        BasicAutomaton cc = null;
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

        switch (n.getRule()) {

            case "atom":
                LOGGER.debug("atom {}", n.getLabel());
                if (!n.hasChildren()) {
                    if (n.getLabel().equals(".")) {

                        LOGGER.debug("basic");
                        BasicAutomaton a = provider.getAnyAccepting();
                        smap.put(n, a);
                    }
                } else {
                    assert n.getChildren().size() == 1;
                    simpleProp(n);
                }

                break;
            case "root":
            case "number":
            case "literal":
            case "shared_literal":
            case "cc_literal":
            case "alternation":
                if(n.getChildren().size() > 1) {
                    smap.put(n, unifyChildren(n));
                } else {
                    assert n.getChildren().size() == 1;
                    simpleProp(n);
                }
                break;
            case "letter":
            case "digit":
                BasicAutomaton a = provider.getNewAutomaton();
                a = a.append(n.getLabel().charAt(0));
                smap.put(n, a);
                break;

            case "cc_atom":
                String lbl = n.getLabel();
                LOGGER.debug("cc atom {}", lbl);

                if (lbl.length() == 3) {
                    BasicAutomaton na = provider.getNewAutomaton();


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


                BasicAutomaton na = null;

                if (n.getChildren().size() > 1) {

                    for (AstNode c : n.getChildren()) {

                        if (na == null) {
                            na = smap.get(c);
                            continue;
                        }

                        na = na.union(smap.get(c));

                    }
                    assert na != null;
                    LOGGER.debug("NA");
                    LOGGER.debug(na.toDot());
                    smap.put(n, na);
                } else {
                    simpleProp(n);
                }

            case "expr":
                if(n.getChildren().size() > 1) {
                    smap.put(n, concatChildren(n));
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

                    String quant = last.getLabel();

                    BasicAutomaton fauto = smap.get(first);

                    LOGGER.debug("fauto {}", fauto.toDot());

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
                                LOGGER.debug("nomax {}", min);
                                smap.put(n, fauto.repeatMin(min));
                            } else {
                                smap.put(n, fauto.repeat(min, max));
                            }

                            LOGGER.info("min " + min + " max" + max);
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
