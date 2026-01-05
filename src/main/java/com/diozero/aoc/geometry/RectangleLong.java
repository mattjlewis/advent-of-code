package com.diozero.aoc.geometry;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.tinylog.Logger;

public record RectangleLong(Point2DLong topLeft, Point2DLong bottomRight) {
	// target area: x=20..30, y=-10..-5
	private static final String TARGET_REGEXP = "target area: x=(-?\\d+)\\.\\.(-?\\d+), y=(-?\\d+)\\.\\.(-?\\d+)";
	private static final Pattern TARGET_PATTERN = Pattern.compile(TARGET_REGEXP);

	public static RectangleLong create(String line) {
		Matcher m = TARGET_PATTERN.matcher(line);
		if (!m.matches()) {
			Logger.error("line '{}' does not match pattern '{}'!", line, TARGET_PATTERN.pattern());
			throw new IllegalArgumentException(
					String.format("line '{}' does not match pattern '{}'!", line, TARGET_PATTERN.pattern()));
		}

		long x1 = Long.parseLong(m.group(1));
		long y1 = Long.parseLong(m.group(3));
		long x2 = Long.parseLong(m.group(2));
		long y2 = Long.parseLong(m.group(4));

		if (x1 > x2) {
			long tmp = x1;
			x1 = x2;
			x2 = tmp;
		}

		// FIXME y is inverted for 2021 day 17!
		if (y1 < y2) {
			long tmp = y1;
			y1 = y2;
			y2 = tmp;
		}

		return new RectangleLong(new Point2DLong(x1, y1), new Point2DLong(x2, y2));
	}

	public static RectangleLong create(Point2DLong p1, Point2DLong p2) {
		return create(p1.x(), p1.y(), p2.x(), p2.y());
	}

	public static RectangleLong create(long x1, long y1, long x2, long y2) {
		if (x1 > x2) {
			long tmp = x1;
			x1 = x2;
			x2 = tmp;
		}

		// FIXME y is not inverted - works for PrintUtil
		if (y1 > y2) {
			long tmp = y1;
			y1 = y2;
			y2 = tmp;
		}

		return new RectangleLong(new Point2DLong(x1, y1), new Point2DLong(x2, y2));
	}

	public static RectangleLong getBounds(final Stream<RectangleLong> rectangles) {
		long min_x = Long.MAX_VALUE;
		long max_x = Long.MIN_VALUE;
		long min_y = Long.MAX_VALUE;
		long max_y = Long.MIN_VALUE;
		final Iterator<RectangleLong> it = rectangles.iterator();
		while (it.hasNext()) {
			final RectangleLong r = it.next();
			min_x = Math.min(min_x, r.x1());
			max_x = Math.max(max_x, r.x2());
			min_y = Math.min(min_y, r.y1());
			max_y = Math.max(max_y, r.y2());
		}

		return RectangleLong.create(min_x, min_y, max_x, max_y);
	}

	public boolean contains(long x, long y) {
		return x >= topLeft.x() && x <= bottomRight.x() && y >= topLeft.y() && y <= bottomRight.y();
	}

	public boolean contains(Point2DLong p) {
		return contains(p.x(), p.y());
	}

	public long width() {
		return 1 + bottomRight.x() - topLeft.x();
	}

	public long height() {
		return 1 + bottomRight.y() - topLeft.y();
	}

	public long x1() {
		return topLeft.x();
	}

	public long y1() {
		return topLeft.y();
	}

	public long x2() {
		return bottomRight.x();
	}

	public long y2() {
		return bottomRight.y();
	}

	public long area() {
		return (bottomRight.x() - topLeft.x() + 1) * (bottomRight.y() - topLeft.y() + 1);
	}
}
