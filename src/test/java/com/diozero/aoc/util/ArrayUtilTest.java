package com.diozero.aoc.util;

import java.util.List;
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

	@Test
	public void test2() {
		List<Tuple2<String, String>> pairs = ArrayUtil.pairCombinations(List.of("a", "b", "c")).toList();
		Assertions.assertEquals(3, pairs.size());

		pairs = ArrayUtil.pairCombinations(List.of("a", "b", "c", "d")).toList();
		Assertions.assertEquals(6, pairs.size());

		pairs = ArrayUtil.pairCombinations(List.of("a", "b", "c", "d", "e")).toList();
		Assertions.assertEquals(10, pairs.size());
	}
}
