package com.diozero.aoc.util;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class ArrayUtilTest {
	@Test
	public void test1() {
		int[] array = IntStream.range(0, 6).toArray();
		ArrayUtil.reverse(array);
		Assertions.assertArrayEquals(array, new int[] { 5, 4, 3, 2, 1, 0 });

		array = IntStream.range(0, 7).toArray();
		ArrayUtil.reverse(array);
		Assertions.assertArrayEquals(array, new int[] { 6, 5, 4, 3, 2, 1, 0 });
	}
}
