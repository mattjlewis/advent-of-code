package com.diozero.aoc.geometry;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class CuboidTest {
	private static final Cuboid TEST_CUBOID = new Cuboid(true, 0, 10, 0, 10, 0, 10);

	@Test
	public void cubeDimensions() {
		int pos = 10;
		int size = 3;
		Cuboid cube = new Cuboid(true, pos, pos + size - 1, pos, pos + size - 1, pos, pos + size - 1);
		Assertions.assertEquals(size, cube.width());
		Assertions.assertEquals(size, cube.height());
		Assertions.assertEquals(size, cube.depth());
		Assertions.assertEquals(size * size * size, cube.volume());
	}

	@Test
	public void identityTest() {
		Cuboid other = TEST_CUBOID;

		Assertions.assertTrue(TEST_CUBOID.contains(other));
		Assertions.assertTrue(other.contains(TEST_CUBOID));

		Assertions.assertTrue(TEST_CUBOID.intersects(other));
		Assertions.assertTrue(other.intersects(TEST_CUBOID));

		Optional<Cuboid> intersection = TEST_CUBOID.intersection(other);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(other, intersection.get());

		Assertions.assertTrue(TEST_CUBOID.intersects(other) == other.intersects(TEST_CUBOID));
		Assertions.assertTrue(TEST_CUBOID.intersection(other).equals(other.intersection(TEST_CUBOID)));
	}

	@Test
	public void overlappingX2() {
		// Overlapping TEST_CUBOID on the x axis
		Cuboid other = new Cuboid(true, TEST_CUBOID.x2(), TEST_CUBOID.x2() + 10, TEST_CUBOID.y1(), TEST_CUBOID.y2(),
				TEST_CUBOID.z1(), TEST_CUBOID.z2());

		Assertions.assertFalse(TEST_CUBOID.contains(other));
		Assertions.assertFalse(other.contains(TEST_CUBOID));

		Assertions.assertTrue(TEST_CUBOID.intersects(other));
		Assertions.assertTrue(other.intersects(TEST_CUBOID));

		Optional<Cuboid> intersection = TEST_CUBOID.intersection(other);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(new Cuboid(true, other.x1(), TEST_CUBOID.x2(), TEST_CUBOID.y1(), TEST_CUBOID.y2(),
				TEST_CUBOID.z1(), TEST_CUBOID.z2()), intersection.get());

		Assertions.assertTrue(TEST_CUBOID.intersects(other) == other.intersects(TEST_CUBOID));
		Assertions.assertTrue(TEST_CUBOID.intersection(other).equals(other.intersection(TEST_CUBOID)));
	}

	@Test
	public void adjacentTest() {
		// Next to TEST_CUBOID on the x axis
		Cuboid other = new Cuboid(true, TEST_CUBOID.x2() + 1, TEST_CUBOID.x2() + 10, TEST_CUBOID.y1(), TEST_CUBOID.y2(),
				TEST_CUBOID.z1(), TEST_CUBOID.z2());

		Assertions.assertFalse(TEST_CUBOID.contains(other));
		Assertions.assertFalse(other.contains(TEST_CUBOID));

		Assertions.assertFalse(TEST_CUBOID.intersects(other));
		Assertions.assertFalse(other.intersects(TEST_CUBOID));

		Optional<Cuboid> intersection = TEST_CUBOID.intersection(other);
		Assertions.assertFalse(intersection.isPresent());

		Assertions.assertTrue(TEST_CUBOID.intersects(other) == other.intersects(TEST_CUBOID));
		Assertions.assertTrue(TEST_CUBOID.intersection(other).equals(other.intersection(TEST_CUBOID)));
	}

	@Test
	public void containsNotTouching() {
		Cuboid other = new Cuboid(true, TEST_CUBOID.x1() + 2, TEST_CUBOID.x2() - 2, TEST_CUBOID.y1() + 2,
				TEST_CUBOID.y2() - 2, TEST_CUBOID.z1() + 2, TEST_CUBOID.z2() - 2);

		Assertions.assertTrue(TEST_CUBOID.contains(other));
		Assertions.assertFalse(other.contains(TEST_CUBOID));

		Assertions.assertTrue(TEST_CUBOID.intersects(other));
		Assertions.assertTrue(other.intersects(TEST_CUBOID));

		Optional<Cuboid> intersection = TEST_CUBOID.intersection(other);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(other, intersection.get());

		Assertions.assertTrue(TEST_CUBOID.intersects(other) == other.intersects(TEST_CUBOID));
		Assertions.assertTrue(TEST_CUBOID.intersection(other).equals(other.intersection(TEST_CUBOID)));
	}

	@Test
	public void containsTouching() {
		Cuboid other = new Cuboid(true, TEST_CUBOID.x1(), TEST_CUBOID.x2() - 2, TEST_CUBOID.y1(), TEST_CUBOID.y2() - 2,
				TEST_CUBOID.z1(), TEST_CUBOID.z2() - 2);

		Assertions.assertTrue(TEST_CUBOID.contains(other));
		Assertions.assertFalse(other.contains(TEST_CUBOID));

		Assertions.assertTrue(TEST_CUBOID.intersects(other));
		Assertions.assertTrue(other.intersects(TEST_CUBOID));

		Optional<Cuboid> intersection = TEST_CUBOID.intersection(other);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(other, intersection.get());

		Assertions.assertTrue(TEST_CUBOID.intersects(other) == other.intersects(TEST_CUBOID));
		Assertions.assertTrue(TEST_CUBOID.intersection(other).equals(other.intersection(TEST_CUBOID)));
	}

	@Test
	public void overlappingX() {
		Cuboid other = new Cuboid(true, TEST_CUBOID.x1() - 1, TEST_CUBOID.x1() + 4, TEST_CUBOID.y1(),
				TEST_CUBOID.y1() + 4, TEST_CUBOID.z1(), TEST_CUBOID.z1() + 4);

		Assertions.assertFalse(TEST_CUBOID.contains(other));
		Assertions.assertFalse(other.contains(TEST_CUBOID));

		Assertions.assertTrue(TEST_CUBOID.intersects(other));
		Assertions.assertTrue(other.intersects(TEST_CUBOID));

		Optional<Cuboid> intersection = TEST_CUBOID.intersection(other);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(new Cuboid(true, TEST_CUBOID.x1(), other.x2(), TEST_CUBOID.y1(), other.y2(),
				TEST_CUBOID.z1(), other.z2()), intersection.get());

		Assertions.assertTrue(TEST_CUBOID.intersects(other) == other.intersects(TEST_CUBOID));
		Assertions.assertTrue(TEST_CUBOID.intersection(other).equals(other.intersection(TEST_CUBOID)));
	}

	@Test
	public void distinct() {
		Cuboid other = new Cuboid(true, TEST_CUBOID.x1() + 20, TEST_CUBOID.x2() + 20, TEST_CUBOID.y1() + 20,
				TEST_CUBOID.y2() + 20, TEST_CUBOID.z1() + 20, TEST_CUBOID.z2() + 20);

		Assertions.assertFalse(TEST_CUBOID.contains(other));
		Assertions.assertFalse(other.contains(TEST_CUBOID));

		Assertions.assertFalse(TEST_CUBOID.intersects(other));
		Assertions.assertFalse(other.intersects(TEST_CUBOID));

		Optional<Cuboid> intersection = TEST_CUBOID.intersection(other);
		Assertions.assertFalse(intersection.isPresent());

		Assertions.assertTrue(TEST_CUBOID.intersects(other) == other.intersects(TEST_CUBOID));
		Assertions.assertTrue(TEST_CUBOID.intersection(other).equals(other.intersection(TEST_CUBOID)));
	}

	@Test
	public void test() {
		Cuboid test_cuboid = new Cuboid(true, 0, 2, 0, 2, 0, 2);
		Assertions.assertEquals(27, test_cuboid.volume());

		Cuboid other = new Cuboid(true, 1, 1, 1, 1, -1, 0);
		Assertions.assertEquals(other.z2() - other.z1() + 1, other.volume());

		Assertions.assertTrue(test_cuboid.intersects(other));
		Assertions.assertTrue(other.intersects(test_cuboid));

		other = new Cuboid(true, 1, 1, 1, 1, -1, 1);
		Assertions.assertEquals(other.z2() - other.z1() + 1, other.volume());
		Assertions.assertTrue(test_cuboid.intersects(other));
		Assertions.assertTrue(other.intersects(test_cuboid));

		List<Cuboid> sub_cuboids = other.remove(test_cuboid);
		Assertions.assertEquals(1, sub_cuboids.size());
		Assertions.assertEquals(new Cuboid(true, 1, 1, 1, 1, -1, -1), sub_cuboids.get(0));
		Assertions.assertEquals(1, sub_cuboids.get(0).volume());

		other = new Cuboid(true, 1, 1, 1, 1, -2, 4);
		Assertions.assertEquals(other.z2() - other.z1() + 1, other.volume());
		Assertions.assertTrue(test_cuboid.intersects(other));
		Assertions.assertTrue(other.intersects(test_cuboid));

		sub_cuboids = other.remove(test_cuboid);
		Assertions.assertEquals(2, sub_cuboids.size());
		Assertions.assertEquals(new Cuboid(true, 1, 1, 1, 1, -2, -1), sub_cuboids.get(0));
		Assertions.assertEquals(2, sub_cuboids.get(0).volume());
		Assertions.assertEquals(new Cuboid(true, 1, 1, 1, 1, 3, 4), sub_cuboids.get(1));
		Assertions.assertEquals(2, sub_cuboids.get(1).volume());
	}
}
