package com.diozero.aoc.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class MathUtilsTest {
	@Test
	public void testCountDigits() {
		Assertions.assertEquals(1, MathUtils.countDigits(0));
		Assertions.assertEquals(1, MathUtils.countDigits(9));
		Assertions.assertEquals(2, MathUtils.countDigits(10));
		Assertions.assertEquals(2, MathUtils.countDigits(99));
		Assertions.assertEquals(3, MathUtils.countDigits(100));
		Assertions.assertEquals(3, MathUtils.countDigits(999));
		Assertions.assertEquals(4, MathUtils.countDigits(1000));
		Assertions.assertEquals(4, MathUtils.countDigits(9999));
		Assertions.assertEquals(5, MathUtils.countDigits(10000));
		Assertions.assertEquals(5, MathUtils.countDigits(99999));

		Assertions.assertEquals(1, MathUtils.countDigits(0L));
		Assertions.assertEquals(1, MathUtils.countDigits(9L));
		Assertions.assertEquals(2, MathUtils.countDigits(10L));
		Assertions.assertEquals(2, MathUtils.countDigits(99L));
		Assertions.assertEquals(3, MathUtils.countDigits(100L));
		Assertions.assertEquals(3, MathUtils.countDigits(999L));
		Assertions.assertEquals(4, MathUtils.countDigits(1000L));
		Assertions.assertEquals(4, MathUtils.countDigits(9999L));
		Assertions.assertEquals(5, MathUtils.countDigits(10000L));
		Assertions.assertEquals(5, MathUtils.countDigits(99999L));
	}

	@Test
	public void testConcat() {
		Assertions.assertEquals(1, MathUtils.concat(0, 1));
		Assertions.assertEquals(11, MathUtils.concat(1, 1));
		Assertions.assertEquals(10, MathUtils.concat(1, 0));
		Assertions.assertEquals(100, MathUtils.concat(10, 0));
		Assertions.assertEquals(1010, MathUtils.concat(10, 10));
	}

	@Test
	public void testSplit() {
		Assertions.assertArrayEquals(new long[] { 1, 0 }, MathUtils.split(10, 1));
		Assertions.assertArrayEquals(new long[] { 1, 2 }, MathUtils.split(12, 1));
		Assertions.assertArrayEquals(new long[] { 12, 34 }, MathUtils.split(1234, 2));
		Assertions.assertArrayEquals(new long[] { 123, 4 }, MathUtils.split(1234, 3));
		Assertions.assertArrayEquals(new long[] { 1, 234 }, MathUtils.split(1234, 1));
		Assertions.assertArrayEquals(new long[] { 12, 34 }, MathUtils.split(1234));
	}
}
