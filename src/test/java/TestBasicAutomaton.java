import com.github.julianthome.automate.core.BasicAutomaton;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by julian on 30/04/2017.
 */
public class TestBasicAutomaton {

    final static Logger LOGGER = LoggerFactory.getLogger(TestBasicAutomaton.class);

    private BasicAutomaton getSimpleAutomaton() {

        BasicAutomaton a1 = new BasicAutomaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');


        BasicAutomaton a2 = new BasicAutomaton();

        BasicAutomaton a3 = new BasicAutomaton();
        a3 = a3.append('x');
        a3 = a3.append('y');
        a3 = a3.append('z');


        BasicAutomaton a = a1.union(a2).union(a3);
        return a;
    }

    @Test
    public void testAppend() {
        BasicAutomaton a = new BasicAutomaton();
        a = a.append('a', 'b');
        a = a.append('g');
        a = a.append('d');

        LOGGER.debug(a.toDot());

        Assert.assertTrue(a.match("agd"));
        Assert.assertTrue(a.match("bgd"));

        Assert.assertFalse(a.match(""));
        Assert.assertFalse(a.match("dgfc"));

    }


    @Test
    public void testConcat() {
        BasicAutomaton a = new BasicAutomaton().append('h', 'z').append('e').append('l');
        BasicAutomaton b = new BasicAutomaton().append('l').append('l').append('o');
        BasicAutomaton c = a.concat(b);

        Assert.assertFalse(c.match("gelllo"));
        Assert.assertTrue(c.match("melllo"));
    }

    @Test
    public void testUnion() {

        BasicAutomaton a = getSimpleAutomaton();

        LOGGER.debug(a.toDot());

        Assert.assertTrue(a.match("bte"));
        Assert.assertTrue(a.match("xyz"));

        Assert.assertFalse(a.match(""));
        Assert.assertFalse(a.match("asfasdfasd"));
        Assert.assertFalse(a.match("b"));
        LOGGER.debug(a.toDot());

    }

    @Test
    public void testIntersection() {

        BasicAutomaton a = getSimpleAutomaton();

        BasicAutomaton a4 = new BasicAutomaton();
        a4 = a4.append('x');
        a4 = a4.append('y');
        a4 = a4.append('z');

        BasicAutomaton isect = a.intersect(a4);

        LOGGER.debug(isect.toDot());


        Assert.assertTrue(isect.match("xyz"));
        Assert.assertFalse(isect.match(""));

        Assert.assertFalse(isect.match("asfasdfasd"));
        Assert.assertFalse(isect.match("b"));
    }


    @Test
    public void testKleene() {

        BasicAutomaton a = getSimpleAutomaton();

        BasicAutomaton kleene = a.star();

        Assert.assertTrue(kleene.match("xyzxyzxyzate"));
        Assert.assertTrue(kleene.match("ateateatexyz"));
        Assert.assertTrue(kleene.match("bte"));
        Assert.assertFalse(kleene.match("btee"));
        Assert.assertFalse(kleene.match("xxxxx"));

        LOGGER.debug(kleene.toDot());
    }

    @Test
    public void testOptional() {

        BasicAutomaton a1 = new BasicAutomaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');


        BasicAutomaton a2 = new BasicAutomaton();
        a2 = a2.append('x');
        a2 = a2.append('y');
        a2 = a2.append('z');


        BasicAutomaton a = a1.union(a2);

        Assert.assertTrue(a.match("ate"));
        Assert.assertTrue(a.match("xyz"));
        Assert.assertFalse(a.match(""));

        a = a.optional();

        Assert.assertTrue(a.match("ate"));
        Assert.assertTrue(a.match("xyz"));
        Assert.assertTrue(a.match(""));

        LOGGER.debug(a.toDot());
    }

    @Test
    public void testPlus() {

        BasicAutomaton a1 = new BasicAutomaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');


        BasicAutomaton a2 = new BasicAutomaton();
        a2 = a2.append('x');
        a2 = a2.append('y');
        a2 = a2.append('z');


        BasicAutomaton a = a1.union(a2);

        Assert.assertTrue(a.match("ate"));
        Assert.assertTrue(a.match("xyz"));
        Assert.assertFalse(a.match("ateate"));
        Assert.assertFalse(a.match("xyzxyz"));


        a = a.plus();

        LOGGER.debug(a.toDot());

        Assert.assertTrue(a.match("ate"));
        Assert.assertTrue(a.match("xyz"));
        Assert.assertTrue(a.match("ateate"));
        Assert.assertTrue(a.match("xyzxyzateate"));
    }


    @Test
    public void testRepeat() {

        BasicAutomaton a1 = new BasicAutomaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');

        BasicAutomaton a2 = new BasicAutomaton();
        a2 = a2.append('x');
        a2 = a2.append('y');
        a2 = a2.append('z');

        BasicAutomaton a = a1.union(a2);

        a = a.repeat(2,5);

        LOGGER.debug(a.toDot());


        Assert.assertFalse(a.match("ate"));
        Assert.assertFalse(a.match("xyz"));
        Assert.assertTrue(a.match("atexyz"));
        Assert.assertTrue(a.match("xyzate"));
        Assert.assertTrue(a.match("atexyzatexyz"));
        Assert.assertTrue(a.match("xyzateatexyz"));

        Assert.assertTrue(a.match("atexyzatexyzate"));
        Assert.assertTrue(a.match("xyzateatexyzate"));

        Assert.assertFalse(a.match("atexyzatexyzatexyz"));
        Assert.assertFalse(a.match("xyzateatexyzatexyz"));

    }


    @Test
    public void testDeterminize() {
        BasicAutomaton a = getSimpleAutomaton();

        BasicAutomaton det = a.determinize();

        LOGGER.debug(det.toDot());
    }


    @Test
    public void testExpansion() {
        BasicAutomaton a = getSimpleAutomaton();
        a = a.expand();

        LOGGER.debug(a.toDot());
    }

    @Test
    public void testEmptyString() {
        BasicAutomaton a = new BasicAutomaton(true);

        LOGGER.debug(a.toDot());

        Assert.assertTrue(a.match(""));
    }

    @Test
    public void testMinimization() {
        BasicAutomaton a = new BasicAutomaton(true);

        BasicAutomaton a1 = new BasicAutomaton();
        a1 = a1.append('a', 'b');
        a1 = a1.append('t');
        a1 = a1.append('e');

        BasicAutomaton a2 = new BasicAutomaton();
        a2 = a2.append('x');
        a2 = a2.append('y');
        a2 = a2.append('z');

        BasicAutomaton a3 = new BasicAutomaton();
        a3 = a3.append('j');
        a3 = a3.append('u');
        a3 = a3.append('t');

        BasicAutomaton a4 = new BasicAutomaton();
        a4 = a4.append('x');
        a4 = a4.append('0');
        a4 = a4.append('0');

        BasicAutomaton a5 = a1.union(a2).union(a3).union(a4);

        LOGGER.debug(a5.toDot());


        a5.minimize();

        LOGGER.debug(a5.toDot());


    }


}


