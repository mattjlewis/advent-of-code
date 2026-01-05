package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Line2D;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.geometry.Rectangle;

public class Day9 extends Day {
	public static void main(String[] args) {
		new Day9().run();
	}

	@Override
	public String name() {
		return "Movie Theater";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Point2D> points = Files.lines(input).map(Point2D::parse).toList();

		return Long.toString(IntStream.range(0, points.size() - 1).boxed()
				.flatMap(i -> IntStream.range(i.intValue() + 1, points.size())
						.mapToObj(j -> Rectangle.create(points.get(i.intValue()), points.get(j))))
				.mapToLong(Rectangle::area).max().getAsLong());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Point2D> points = Files.lines(input).map(Point2D::parse).toList();

		final List<Line2D> lines = IntStream.range(0, points.size())
				.mapToObj(i -> Line2D.create(points.get(i), points.get((i + 1) % points.size()))).toList();
		final Stream<Rectangle> rectangles = IntStream.range(0, points.size() - 1).boxed()
				.flatMap(i -> IntStream.range(i.intValue() + 1, points.size())
						.mapToObj(j -> Rectangle.create(points.get(i.intValue()), points.get(j))));

		return Long.toString(rectangles.filter(r -> lines.stream().noneMatch(r::overlaps)).mapToLong(Rectangle::area)
				.max().getAsLong());
	}
}
