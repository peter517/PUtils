package com.pengjun.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.pengjun.utils.NumberUtils;

public class NumberUtilsTest extends TestCase {
	public NumberUtilsTest(String testName) {
		super(testName);
	}

	public void testFormat() {
		Assert.assertEquals(1.3f, NumberUtils.formatFloat(1.33f), 0);
		Assert.assertEquals(1.3f, NumberUtils.formatDouble(1.33d), 0);
	}
}
