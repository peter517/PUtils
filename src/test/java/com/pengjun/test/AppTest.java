package com.pengjun.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	public AppTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		TestSuite ts = new TestSuite();
		ts.addTestSuite(NumberUtilsTest.class);
		return ts;
	}

}
