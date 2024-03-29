package com.diozero.aoc.geometry;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.tinylog.Logger;

public record Rectangle(Point2D topLeft, Point2D bottomRight) {
	// target area: x=20..30, y=-10..-5
	private static final String TARGET_REGEXP = "target area: x=(-?\\d+)\\.\\.(-?\\d+), y=(-?\\d+)\\.\\.(-?\\d+)";
	private static final Pattern TARGET_PATTERN = Pattern.compile(TARGET_REGEXP);

	public static Rectangle create(String line) {
		Matcher m = TARGET_PATTERN.matcher(line);
		if (!m.matches()) {
			Logger.error("line '{}' does not match pattern '{}'!", line, TARGET_PATTERN.pattern());
			throw new IllegalArgumentException(
					String.format("line '{}' does not match pattern '{}'!", line, TARGET_PATTERN.pattern()));
		}

		int x1 = Integer.parseInt(m.group(1));
		int y1 = Integer.parseInt(m.group(3));
		int x2 = Integer.parseInt(m.group(2));
		int y2 = Integer.parseInt(m.group(4));

		if (x1 > x2) {
			int tmp = x1;
			x1 = x2;
			x2 = tmp;
		}

		// FIXME y is inverted for 2021 day 17!
		if (y1 < y2) {
			int tmp = y1;
			y1 = y2;
			y2 = tmp;
		}

		return new Rectangle(new Point2D(x1, y1), new Point2D(x2, y2));
	}

	public static Rectangle create(int x1, int y1, int x2, int y2) {
		if (x1 > x2) {
			int tmp = x1;
			x1 = x2;
			x2 = tmp;
		}

		// FIXME y is not inverted - works for PrintUtil
		if (y1 > y2) {
			int tmp = y1;
			y1 = y2;
			y2 = tmp;
		}

		return new Rectangle(new Point2D(x1, y1), new Point2D(x2, y2));
	}

	public static Rectangle getBounds(final Stream<Rectangle> rectangles) {
		int min_x = Integer.MAX_VALUE;
		int max_x = Integer.MIN_VALUE;
		int min_y = Integer.MAX_VALUE;
		int max_y = Integer.MIN_VALUE;
		final Iterator<Rectangle> it = rectangles.iterator();
		while (it.hasNext()) {
			final Rectangle r = it.next();
			min_x = Math.min(min_x, r.x1());
			max_x = Math.max(max_x, r.x2());
			min_y = Math.min(min_y, r.y1());
			max_y = Math.max(max_y, r.y2());
		}

		return Rectangle.create(min_x, min_y, max_x, max_y);
	}

	public boolean contains(int x, int y) {
		return x >= topLeft.x() && x <= bottomRight.x() && y >= topLeft.y() && y <= bottomRight.y();
	}

	public boolean contains(Point2D p) {
		return contains(p.x(), p.y());
	}

	public int width() {
		return 1 + bottomRight.x() - topLeft.x();
	}

	public int height() {
		return 1 + bottomRight.y() - topLeft.y();
	}

	public int x1() {
		return topLeft.x();
	}

	public int y1() {
		return topLeft.y();
	}

	public int x2() {
		return bottomRight.x();
	}

	public int y2() {
		return bottomRight.y();
	}
}
