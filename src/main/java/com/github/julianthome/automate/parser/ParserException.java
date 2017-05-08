package com.github.julianthome.automate.parser;

import org.snt.inmemantlr.exceptions.AstProcessorException;

public class ParserException extends AstProcessorException {
    ParserException(String message) {
        super(message);
    }
}
