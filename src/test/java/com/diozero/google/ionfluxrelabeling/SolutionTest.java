package com.diozero.google.ionfluxrelabeling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class SolutionTest {
	@Test
	public void test() {
		/*-
		 * solution(3, [1, 4, 7]) would return the converters above the converters at indexes 1, 4, and 7 in a perfect
		 * binary tree of height 3, which is [3, 6, -1]
		 */
		Assertions.assertArrayEquals(new int[] { 3, 6, -1 }, Solution.solution(3, new int[] { 1, 4, 7 }));

		Assertions.assertArrayEquals(new int[] { 21, 15, 29 }, Solution.solution(5, new int[] { 19, 14, 28 }));
		Assertions.assertArrayEquals(new int[] { -1, 7, 6, 3 }, Solution.solution(3, new int[] { 7, 3, 5, 1 }));
	}
}
