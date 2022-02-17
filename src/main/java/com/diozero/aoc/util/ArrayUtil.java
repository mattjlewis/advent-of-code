package com.diozero.aoc.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ArrayUtil {
	private ArrayUtil() {
	}

	public static int[] shuffle(int startInclusive, int endExcusive) {
		final List<Integer> random_int_list = IntStream.range(startInclusive, endExcusive).boxed()
				.collect(Collectors.toList());
		Collections.shuffle(random_int_list);
		return random_int_list.stream().mapToInt(Integer::intValue).toArray();
	}

	public static void shuffle(int[] array) {
		Collections.shuffle(new AbstractList<Integer>() {
			@Override
			public Integer get(int index) {
				return Integer.valueOf(array[index]);
			}

			@Override
			public int size() {
				return array.length;
			}

			@Override
			public Integer set(int index, Integer element) {
				int result = array[index];
				array[index] = element.intValue();
				return Integer.valueOf(result);
			}
		});
	}

	public static <T> Stream<List<T>> permutations(Collection<T> values) {
		return permutations(values, values.size());
	}

	public static <T> Stream<List<T>> permutations(Collection<T> values, int r) {
		if (r <= 0) {
			return Stream.empty();
		}

		if (r == 1) {
			return values.stream().map(Arrays::asList);
		}

		if (r == 2) {
			return values.stream().flatMap(e1 -> values.stream() // e1: refers to an element of c
					.filter(e2 -> !e1.equals(e2)) // e2: refers to an element of c
					.map(e2 -> Arrays.asList(e1, e2)));
		}

		return permutations(values, r - 1).flatMap(l -> values.stream().filter(e -> l.contains(e) == false).map(e -> {
			List<T> out = new ArrayList<>();
			out.addAll(l);
			out.add(e);
			return out;
		}));
	}

	public static int[] repeat(int[] array, int repetitions) {
		final int length = array.length;
		final int new_length = repetitions * length;
		final int[] new_array = new int[new_length];

		for (int i = 0; i < repetitions; i++) {
			System.arraycopy(array, 0, new_array, i * length, length);
		}

		return new_array;
	}

	public static void reverse(int[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			int tmp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = tmp;
		}
	}
}
