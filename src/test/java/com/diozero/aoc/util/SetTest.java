package com.diozero.aoc.util;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class SetTest {
	@Test
	public void intersectTest() {
		final Set<String> abc = Set.of("a", "b", "c");
		final Set<String> cde = Set.of("c", "d", "e");
		final Set<String> xyz = Set.of("x", "y", "z");

		Assertions.assertTrue(SetUtil.intersects(abc, cde));
		Assertions.assertTrue(SetUtil.intersects(cde, abc));
		Assertions.assertFalse(SetUtil.intersects(abc, xyz));

		Assertions.assertEquals(SetUtil.intersectionCount(abc, cde), 1);
		Assertions.assertEquals(SetUtil.intersectionCount(cde, abc), 1);
		Assertions.assertEquals(SetUtil.intersectionCount(abc, xyz), 0);
	}
}
