/*
* automat - bdd automaton package
*
* Copyright 2016, Julian Thomé <julian.thome.de@gmail.com>
*
* Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
* the European Commission - subsequent versions of the EUPL (the "Licence");
* You may not use this work except in compliance with the Licence. You may
* obtain a copy of the Licence at:
*
* https://joinup.ec.europa.eu/sites/default/files/eupl1.1.-licence-en_0.pdf
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/

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

}