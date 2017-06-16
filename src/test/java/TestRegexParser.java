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

import com.github.julianthome.automate.core.AbstractAutomaton;
import com.github.julianthome.automate.parser.RegexParser;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;


public class TestRegexParser {

    final static Logger LOGGER = LoggerFactory.getLogger(TestRegexParser.class);

    @Test
    public void simple() {
        AbstractAutomaton a = RegexParser.INSTANCE.getAutomaton("c*");
    }

    @Test
    public void testPattern0() {
        AbstractAutomaton a = RegexParser.INSTANCE.getAutomaton("abc*[a-z]?d");
        Assert.assertTrue(a.match("abd"));
        Assert.assertTrue(a.match("abcccccccccd"));
        Assert.assertTrue(a.match("abccccccccczd"));
        Assert.assertFalse(a.match(""));
        Assert.assertFalse(a.match("abczzd"));
    }


    @Test
    public void testPattern1() {

        AbstractAutomaton a = RegexParser.INSTANCE.getAutomaton(".*");

        Assert.assertTrue(a.match("abd"));
        Assert.assertTrue(a.match("abcccccccccd"));
        Assert.assertTrue(a.match("abccccccccczd"));

        Assert.assertTrue(a.match(""));
        Assert.assertTrue(a.match("abczzd"));
        Assert.assertTrue(a.match("afdasdkjf2123u-13.4nj;af0391h41;jac " +
                "afjasdpifw"));
    }


    @Test
    public void testPattern2() {

        AbstractAutomaton a = RegexParser.INSTANCE.getAutomaton
                ("[3-9][0-9]|2[2-9]|[1-9][0-9]{2,}");

        Random rn = new Random();

        int x = 22;

        Assert.assertTrue(a.match(String.valueOf(x)));

        for(int i = 0; i < 100; i++) {

            int answer = 22 + rn.nextInt(1000);

            LOGGER.debug("test with {}", answer);

            Assert.assertTrue(a.match(String.valueOf(answer)));
        }

        for(int i = 0; i < x; i ++) {
            Assert.assertFalse(a.match(String.valueOf(i)));
        }

        LOGGER.debug(a.toDot());
    }


    @Test
    public void testPattern3() {
        AbstractAutomaton a = RegexParser.INSTANCE.getAutomaton("\\)+");
        Assert.assertTrue(a.match(")"));
        Assert.assertTrue(a.match(")))))))))))))))))"));
        Assert.assertFalse(a.match(""));
        LOGGER.debug(a.toDot());
    }

    @Test
    public void testMinMax() {
        AbstractAutomaton a = RegexParser.INSTANCE.getAutomaton("[a-z0-9_-]{3,16}");

        Assert.assertFalse(a.match("09"));
        Assert.assertTrue(a.match("abcabcabc"));
        Assert.assertTrue(a.match("aaaaaaaaaa000000"));
        Assert.assertFalse(a.match("aaaaaaaaaa000000z"));

    }


    @Test
    public void testUsername() {
        AbstractAutomaton a = RegexParser.INSTANCE.getAutomaton("^[a-z0-9_-]{3,16}");

        Assert.assertTrue(a.match("HELLO"));
        Assert.assertTrue(a.match("JOHN"));
        Assert.assertTrue(a.match("jo"));
        Assert.assertTrue(a.match("xy"));
        Assert.assertTrue(a.match(""));

        Assert.assertFalse(a.match("john"));
        Assert.assertFalse(a.match("hello"));


        Assert.assertTrue(a.match
                ("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));

    }

}