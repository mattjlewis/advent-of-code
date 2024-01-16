package com.diozero.aoc.geometry;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/*-
 *  012345
 * 0h a b
 * 1
 * 2g O c
 * 3
 * 4f e d
 * 5
 *
 * Angles to O (2,2):
 * . a (2, 0) -> 0
 * . b (4, 0) -> 45
 * . c (4, 2) -> 90
 * . d (4, 4) -> 135
 * . e (2, 4) -> 180
 * . f (0, 4) -> 225
 * . g (0, 2) -> 270
 * . h (0, 0) -> 315
 */
@SuppressWarnings("static-method")
public class Point2DTest {
	@Test
	public void angles() {
		Point2D origin = new Point2D(2, 2);

		Assertions.assertEquals(0, origin.angleTo(new Point2D(origin.x() + 2, origin.y())));
		Assertions.assertEquals(45, origin.angleTo(new Point2D(origin.x() + 2, origin.y() + 2)));
		Assertions.assertEquals(90, origin.angleTo(new Point2D(origin.x(), origin.y() + 2)));
		Assertions.assertEquals(135, origin.angleTo(new Point2D(origin.x() - 2, origin.y() + 2)));
		Assertions.assertEquals(180, origin.angleTo(new Point2D(origin.x() - 2, origin.y())));
		Assertions.assertEquals(225, origin.angleTo(new Point2D(origin.x() - 2, origin.y() - 2)));
		Assertions.assertEquals(270, origin.angleTo(new Point2D(origin.x(), origin.y() - 2)));
		Assertions.assertEquals(315, origin.angleTo(new Point2D(origin.x() + 2, origin.y() - 2)));

		Assertions.assertEquals(0, origin.angleTo(new Point2D(origin.x(), origin.y() - 2), 90));
		Assertions.assertEquals(45, origin.angleTo(new Point2D(origin.x() + 2, origin.y() - 2), 90));
		Assertions.assertEquals(90, origin.angleTo(new Point2D(origin.x() + 2, origin.y()), 90));
		Assertions.assertEquals(180, origin.angleTo(new Point2D(origin.x(), origin.y() + 2), 90));
		Assertions.assertEquals(270, origin.angleTo(new Point2D(origin.x() - 2, origin.y()), 90));
	}

	@Test
	public void testDistance() {
		Point2D origin = Point2D.ORIGIN;
		Assertions.assertEquals(0, origin.distance(origin));

		Point2D other = new Point2D(10, 0);
		Assertions.assertEquals(10, origin.distance(other));
		Assertions.assertEquals(10, other.distance(origin));

		other = new Point2D(10, 0);
		Assertions.assertEquals(10, origin.distance(other));
		Assertions.assertEquals(10, other.distance(origin));

		other = new Point2D(-10, 0);
		Assertions.assertEquals(10, origin.distance(other));
		Assertions.assertEquals(10, other.distance(origin));

		other = new Point2D(0, -10);
		Assertions.assertEquals(10, origin.distance(other));
		Assertions.assertEquals(10, other.distance(origin));

		other = new Point2D(10, 10);
		Assertions.assertEquals(Math.sqrt(10 * 10 + 10 * 10), other.distance(origin));
		Assertions.assertEquals(Math.sqrt(10 * 10 + 10 * 10), origin.distance(other));

		other = new Point2D(3, 4);
		Assertions.assertEquals(5, other.distance(origin));
		Assertions.assertEquals(5, origin.distance(other));
	}

	@Test
	public void areaTest() {
		System.out.println(Point2D.area(List.of(new Point2D(1, 6), new Point2D(3, 1), new Point2D(7, 2),
				new Point2D(4, 4), new Point2D(8, 5))));
		System.out.println(Point2D.area(
				List.of(new Point2D(0, 0), new Point2D(6, 0), new Point2D(6, 5), new Point2D(4, 5), new Point2D(4, 7),
						new Point2D(6, 7), new Point2D(6, 9), new Point2D(1, 9), new Point2D(1, 7), new Point2D(0, 7),
						new Point2D(0, 5), new Point2D(2, 5), new Point2D(2, 2), new Point2D(0, 2))));
	}
}
