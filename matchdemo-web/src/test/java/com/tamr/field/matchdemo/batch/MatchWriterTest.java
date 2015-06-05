package com.tamr.field.matchdemo.batch;

import com.tamr.field.matchdemo.TestMatchDemoConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by jellin on 5/29/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations="classpath:test.properties")
@SpringApplicationConfiguration(classes = TestMatchDemoConfig.class)
public class MatchWriterTest {

    @Autowired
    MatchWriter matchWriter;

    @Test
    public void testHandleMatchesAllEqual(){
        HashMap m1 = new HashMap();
        HashMap m2 = new HashMap();
        m1.put("attr1","val1");
        m2.put("attr1","val1");
        m1.put("attr2","val1");
        m2.put("attr2","val1");

        assertTrue(matchWriter.recordsEqual(m1, m2));
    }

    @Test
    public void testHandleMatchesAllUnEqual(){
        HashMap m1 = new HashMap();
        HashMap m2 = new HashMap();
        m1.put("attr1","val1");
        m2.put("attr1","val1");
        m1.put("attr2","val2");
        m2.put("attr2","val1");
        assertFalse(matchWriter.recordsEqual(m1, m2));
    }

    @Test
    public void testHandleMatchesAllUnEqualNull(){
        HashMap m1 = new HashMap();
        HashMap m2 = new HashMap();
        m1.put("attr1","val1");
        m2.put("attr1", null);
        m1.put("attr2","val1");
        m2.put("attr2","val1");
        assertFalse(matchWriter.recordsEqual(m1, m2));
    }

    @Test
    public void testHandleMatchesAllEqualNull(){
        HashMap m1 = new HashMap();
        HashMap m2 = new HashMap();
        m1.put("attr1", "");
        m2.put("attr1", null);
        assertTrue(matchWriter.recordsEqual(m1, m2));
    }

    @Test
    public void testHandleMatchesAllEqualNullIgnore(){
        HashMap m1 = new HashMap();
        HashMap m2 = new HashMap();
        m1.put("attr1", "");
        m2.put("attr1", null);
        m1.put("ignore1","a");
        m2.put("ignore1", "b");
        m1.put("attr2", "");
        m2.put("attr2", null);
        assertTrue(matchWriter.recordsEqual(m1, m2));
    }

}
