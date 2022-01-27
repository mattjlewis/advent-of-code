package com.diozero.aoc.geometry;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class Line2DTest {
	@Test
	public void intersects() {
		Point2D l1_p1 = new Point2D(0, 8);
		Point2D l1_p2 = new Point2D(10, 8);
		Line2D l1 = Line2D.create(l1_p1, l1_p2);
		Assertions.assertEquals(Line2D.Direction.HORIZONTAL, l1.direction());
		Assertions.assertFalse(l1.intersection(l1).isPresent());

		Point2D l2_p1 = new Point2D(l1.x1() + 2, 0);
		Point2D l2_p2 = new Point2D(l1.x1() + 2, 10);
		Line2D l2 = Line2D.create(l2_p1, l2_p2);
		Assertions.assertEquals(Line2D.Direction.VERTICAL, l2.direction());
		Assertions.assertFalse(l2.intersection(l2).isPresent());

		Optional<Point2D> intersection = l1.intersection(l2);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(new Point2D(l2.x1(), l1.y1()), intersection.get());

		Assertions.assertTrue(l1.intersection(l2).equals(l2.intersection(l1)));

		Point2D l3_p1 = new Point2D(l1.x1(), 0);
		Point2D l3_p2 = new Point2D(l1.x1(), 10);
		Line2D l3 = Line2D.create(l3_p1, l3_p2);
		Assertions.assertEquals(Line2D.Direction.VERTICAL, l3.direction());
		Assertions.assertFalse(l3.intersection(l3).isPresent());

		intersection = l1.intersection(l3);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(new Point2D(l3.x1(), l1.y1()), intersection.get());

		Assertions.assertTrue(l1.intersection(l3).equals(l3.intersection(l1)));
	}

	@Test
	public void intersectsBackwards() {
		Point2D l1_p1 = new Point2D(10, 8);
		Point2D l1_p2 = new Point2D(0, 8);
		Line2D l1 = Line2D.create(l1_p1, l1_p2);
		Assertions.assertEquals(Line2D.Direction.HORIZONTAL, l1.direction());
		Assertions.assertFalse(l1.intersection(l1).isPresent());

		Point2D l2_p1 = new Point2D(l1.x2() + 2, 10);
		Point2D l2_p2 = new Point2D(l1.x2() + 2, 0);
		Line2D l2 = Line2D.create(l2_p1, l2_p2);
		Assertions.assertEquals(Line2D.Direction.VERTICAL, l2.direction());
		Assertions.assertFalse(l2.intersection(l2).isPresent());

		Optional<Point2D> intersection = l1.intersection(l2);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(new Point2D(l2.x1(), l1.y1()), intersection.get());

		Assertions.assertTrue(l1.intersection(l2).equals(l2.intersection(l1)));

		Point2D l3_p1 = new Point2D(l1.x2(), 10);
		Point2D l3_p2 = new Point2D(l1.x2(), 0);
		Line2D l3 = Line2D.create(l3_p1, l3_p2);
		Assertions.assertEquals(Line2D.Direction.VERTICAL, l3.direction());
		Assertions.assertFalse(l3.intersection(l3).isPresent());

		intersection = l1.intersection(l3);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(new Point2D(l3.x1(), l1.y1()), intersection.get());

		Assertions.assertTrue(l1.intersection(l3).equals(l3.intersection(l1)));
	}

	@Test
	public void noInterection() {
		Point2D l1_p1 = new Point2D(0, 8);
		Point2D l1_p2 = new Point2D(10, 8);
		Line2D l1 = Line2D.create(l1_p1, l1_p2);
		Assertions.assertEquals(Line2D.Direction.HORIZONTAL, l1.direction());

		Point2D l2_p1 = new Point2D(l1.x2() + 2, 0);
		Point2D l2_p2 = new Point2D(l1.x2() + 2, 10);
		Line2D l2 = Line2D.create(l2_p1, l2_p2);
		Assertions.assertEquals(Line2D.Direction.VERTICAL, l2.direction());

		Optional<Point2D> intersection = l1.intersection(l2);
		Assertions.assertFalse(intersection.isPresent());

		Assertions.assertTrue(l1.intersection(l2).equals(l2.intersection(l1)));

		l2_p1 = new Point2D(l1.x1() - 2, 0);
		l2_p2 = new Point2D(l1.x1() - 2, 10);
		l2 = Line2D.create(l2_p1, l2_p2);
		Assertions.assertEquals(Line2D.Direction.VERTICAL, l2.direction());

		intersection = l1.intersection(l2);
		Assertions.assertFalse(intersection.isPresent());

		Assertions.assertTrue(l1.intersection(l2).equals(l2.intersection(l1)));
	}
}
