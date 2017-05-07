package com.github.julianthome.automate.parser;

import com.github.julianthome.automate.core.Automaton;
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
public class RegexAstProcessor extends AstProcessor<Automaton, Automaton> {

    final static Logger LOGGER = LoggerFactory.getLogger(RegexParser.class);

    public RegexAstProcessor(Ast ast) {
        super(ast);
    }

    @Override
    public Automaton getResult() {
        Automaton a = smap.get(ast.getRoot());
        a.minimize();
        return a;
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void process(AstNode n) {

        switch (n.getRule()) {
            case "root":
            case "number":
            case "literal":
            case "atom":
            case "shared_literal":
            case "cc_literal":
            case "alternation":
                simpleProp(n);
                break;

            case "letter":
            case "digit":
                Automaton a = new Automaton();
                a = a.append(n.getLabel().charAt(0));
                smap.put(n, a);
                break;

            case "cc_atom":
                String lbl = n.getLabel();
                LOGGER.debug("cc atom {}", lbl);

                if(lbl.length() == 3) {
                    Automaton na = new Automaton();
                    na = na.append(lbl.charAt(0), lbl.charAt(2));
                    smap.put(n, na);
                } else {
                    simpleProp(n);
                }
                break;

            case "character_class":

                LOGGER.debug("character class {}", n.getLabel());


                Automaton na = null;

                if(n.getChildren().size() > 1) {

                    for(AstNode c : n.getChildren()) {

                        if(na == null) {
                            na = smap.get(c);
                            continue;
                        }

                        na = na.union(smap.get(c));

                    }
                    assert na != null;
                    LOGGER.debug("NA");
                    LOGGER.debug(na.toDot());
                    smap.put(n,na);
                } else {
                    simpleProp(n);
                }

            case "expr":
                Automaton cc = null;
                for(AstNode c : n.getChildren()) {
                    if(cc == null) {
                        cc = smap.get(c);
                        continue;
                    }
                    cc = cc.concat(smap.get(c));
                }
                smap.put(n, cc);

            case "element":

                LOGGER.debug("handle element " + n.getLabel());

                if (n.getChildren().size() == 1) {
                    simpleProp(n);
                } else if (n.getChildren().size() == 2) {

                    AstNode last = n.getChildren().get(1);
                    AstNode first = n.getChildren().get(0);

                    String quant = last.getLabel();

                    Automaton fauto = smap.get(first);

                    LOGGER.debug("fauto {}", fauto.toDot());

                    assert fauto != null;


                    if (last != null && last.getRule().equals("quantifier")) {

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

                        int min = -1;
                        int max = -1;

                        Pattern pattern = Pattern.compile("\\{([0-9]*),?([0-9]*)\\}");
                        Matcher matcher = pattern.matcher(quant);

                        if (matcher.matches()) {
                            if (matcher.group(1) != null) {
                                min = Integer.parseInt(matcher.group(1));
                            }
                            if (matcher.group(2) != null) {
                                max = Integer.parseInt(matcher.group(2));
                            }

                            smap.put(n, fauto.repeat(min, max));

                            LOGGER.info("min " + min + " max" + max);
                        }
                    }
                }

                break;
        }


    }
}
