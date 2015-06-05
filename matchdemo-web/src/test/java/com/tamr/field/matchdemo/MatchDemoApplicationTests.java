package com.tamr.field.matchdemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MatchDemoApplication.class)
@WebAppConfiguration
@TestPropertySource(locations="classpath:test.properties")
public class MatchDemoApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void testStrings(){
		assertTrue(this.checkEquals("a", "a"));
		assertTrue(this.checkEquals("", ""));
		assertTrue(this.checkEquals("", null));
		assertTrue(this.checkEquals(null, ""));
		assertTrue(this.checkEquals(null, null));

		assertFalse(checkEquals("a", "b"));
		assertFalse(checkEquals(null, "b"));
		assertFalse(checkEquals("a", null));
		assertFalse(checkEquals("", "b"));




	}
	private boolean checkEquals(Object a, Object b){

		boolean isStr0Empty = (a == null || a.equals(""));
		boolean isStr1Empty = (b == null || b.equals(""));

		if (isStr0Empty && isStr1Empty)
			return true;
		// at least one of them is not empty
		if (isStr0Empty)
			return false;
		if (isStr1Empty)
			return false;
		//none of them is empty
		return a.equals(b);

	}

}
