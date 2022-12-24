package com.diozero.aoc.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class IntRangeTest {
	@Test
	public void testTouches() {
		// 1..3, 3..26
		Assertions.assertTrue(new IntRange(1, 3).touches(new IntRange(3, 26)));
		// 3..26, 1..3
		Assertions.assertTrue(new IntRange(3, 26).touches(new IntRange(1, 3)));

		// 1..3, 4..26
		Assertions.assertTrue(new IntRange(1, 3).touches(new IntRange(4, 26)));
		// 4..26, 1..3
		Assertions.assertTrue(new IntRange(4, 26).touches(new IntRange(1, 3)));

		// 4..26, 27..29
		Assertions.assertTrue(new IntRange(4, 26).touches(new IntRange(27, 29)));
		// 27..29, 4..26
		Assertions.assertTrue(new IntRange(27, 29).touches(new IntRange(4, 26)));

		// 1..30, 4..26
		Assertions.assertTrue(new IntRange(1, 30).touches(new IntRange(4, 26)));
		// 4..26, 1..30
		Assertions.assertTrue(new IntRange(4, 26).touches(new IntRange(1, 30)));

		// 1..5, 10..100
		Assertions.assertFalse(new IntRange(1, 5).touches(new IntRange(10, 100)));
		// 1..5, 7..10
		Assertions.assertFalse(new IntRange(1, 5).touches(new IntRange(7, 10)));
	}

	@Test
	public void testOverlaps() {
		// 1..3, 3..26
		Assertions.assertTrue(new IntRange(1, 3).overlaps(new IntRange(3, 26)));
		// 3..26, 1..3
		Assertions.assertTrue(new IntRange(3, 26).overlaps(new IntRange(1, 3)));

		// 1..3, 4..26
		Assertions.assertFalse(new IntRange(1, 3).overlaps(new IntRange(4, 26)));
		// 4..26, 1..3
		Assertions.assertFalse(new IntRange(4, 26).overlaps(new IntRange(1, 3)));

		// 1..30, 4..26
		Assertions.assertTrue(new IntRange(1, 30).overlaps(new IntRange(4, 26)));
		// 4..26, 1..30
		Assertions.assertTrue(new IntRange(4, 26).overlaps(new IntRange(1, 30)));

		// 1..5, 10..100
		Assertions.assertFalse(new IntRange(1, 5).overlaps(new IntRange(10, 100)));
		// 1..5, 7..10
		Assertions.assertFalse(new IntRange(1, 5).overlaps(new IntRange(7, 10)));
	}
}
