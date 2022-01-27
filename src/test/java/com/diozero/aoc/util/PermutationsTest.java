package com.diozero.aoc.util;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class PermutationsTest {
	@Test
	public void integerList() {
		System.out.println("A list of 5 items (0..4) has " + factorial(5) + " permutations");

		int num_items = 0;
		List<Integer> int_list = IntStream.range(0, num_items).boxed().toList();
		Assertions.assertEquals(factorial(num_items), ArrayUtil.permutations(int_list).count());

		num_items++;
		int_list = IntStream.range(0, num_items).boxed().toList();
		Assertions.assertEquals(factorial(num_items), ArrayUtil.permutations(int_list).count());
		System.out.println(ArrayUtil.permutations(int_list).toList());

		num_items++;
		int_list = IntStream.range(0, num_items).boxed().toList();
		Assertions.assertEquals(factorial(num_items), ArrayUtil.permutations(int_list).count());
		System.out.println(ArrayUtil.permutations(int_list).toList());

		num_items++;
		int_list = IntStream.range(0, num_items).boxed().toList();
		Assertions.assertEquals(factorial(num_items), ArrayUtil.permutations(int_list).count());
		System.out.println(ArrayUtil.permutations(int_list).toList());

		num_items++;
		int_list = IntStream.range(0, num_items).boxed().toList();
		Assertions.assertEquals(factorial(num_items), ArrayUtil.permutations(int_list).count());

		num_items++;
		int_list = IntStream.range(0, num_items).boxed().toList();
		Assertions.assertEquals(factorial(num_items), ArrayUtil.permutations(int_list).count());

		num_items++;
		int_list = IntStream.range(0, num_items).boxed().toList();
		Assertions.assertEquals(factorial(num_items), ArrayUtil.permutations(int_list).count());

		num_items++;
		int_list = IntStream.range(0, num_items).boxed().toList();
		Assertions.assertEquals(factorial(num_items), ArrayUtil.permutations(int_list).count());
	}

	public static long factorial(int n) {
		if (n == 0) {
			return 0;
		}

		return LongStream.rangeClosed(1, n).reduce(1, (long x, long y) -> x * y);
	}
}
