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

package com.github.julianthome.automate.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class EscapeUtils {

    private static Character[] sarray = new Character[]{'+', '{', '}', '(',
            ')', '[', ']', '&', '^', '-', '?', '*', '\"', '$', '<', '>', '.',
            '|', '#' ,'~', '\\'};

    private static Set<Character> special = new HashSet<Character>(Arrays.asList(sarray));


    public static String escapeSpecialCharacters(String s) {
        StringBuilder out = new StringBuilder();
        char pred = ' ';
        for (char c : s.toCharArray()) {
            if (pred != '\\' && special.contains(c)) {
                out.append("\\" + c);
            } else if (pred == '\\' && special.contains(c)) {
                out.deleteCharAt(out.length() - 1); // delete NULL
                out.append(c);
            } else {
                out.append(c);
            }
            pred = c;
        }
        return out.toString();
    }

    public static String unescapeSpecialCharacters(String s) {
        if(s == null)
            return "";

        StringBuilder out = new StringBuilder();
        char pred = ' ';
        for (char c : s.toCharArray()) {
            if (pred == '\\' && special.contains(c)) {
                out.deleteCharAt(out.length() - 1); // delete NULL
                out.append(c);
            } else {
                out.append(c);
            }
            pred = c;

            System.out.println("PRED " + pred);
            System.out.println("NXT " + c);
        }
        return out.toString();
    }

}
