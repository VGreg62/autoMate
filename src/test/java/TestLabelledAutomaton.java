import com.github.julianthome.automate.core.BasicAutomaton;
import com.github.julianthome.automate.core.LabelledAutomaton;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by julian on 30/04/2017.
 */
public class TestLabelledAutomaton {

    final static Logger LOGGER = LoggerFactory.getLogger(TestLabelledAutomaton.class);

    @Test
    public void testSimpleLabelled() {

        LabelledAutomaton a1 = new LabelledAutomaton();
        a1 = (LabelledAutomaton)a1.append('a', 'b');
        a1 = (LabelledAutomaton)a1.append('t');
        a1 = (LabelledAutomaton)a1.append('e');

        a1.labelAllStates("a1");


        LabelledAutomaton a2 = new LabelledAutomaton();
        a2 = (LabelledAutomaton)a2.append('x');
        a2 = (LabelledAutomaton)a2.append('y');
        a2 = (LabelledAutomaton)a2.append('z');
        a2.labelAllStates("a2");


        LabelledAutomaton a3 = new LabelledAutomaton();
        a3 = (LabelledAutomaton)a3.append('b');
        a3 = (LabelledAutomaton)a3.append('z');
        a3 = (LabelledAutomaton)a3.append('z');
        a3.labelAllStates("a3");





        LOGGER.debug(a2.toDot());



        BasicAutomaton a = a1.union(a2).union(a3);

        a.minimize();
        LOGGER.debug(a.toDot());
    }

}


