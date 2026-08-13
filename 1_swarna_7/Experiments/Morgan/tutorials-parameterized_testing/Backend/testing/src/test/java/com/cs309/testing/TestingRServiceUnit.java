package com.cs309.testing;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;

// Import Local classes
import com.cs309.testing.Service.ReverseStringService;

@RunWith(SpringRunner.class)
public class TestingRServiceUnit {

	@Test
	public void testReverse()  {
		// create an instance of SUT
		ReverseStringService rss = new ReverseStringService();

		//check if it works
		assertEquals("olleh", rss.reverse("hello"));
	}

}
