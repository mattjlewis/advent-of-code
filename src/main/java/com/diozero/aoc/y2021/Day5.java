package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Line2D;
import com.diozero.aoc.geometry.Point2D;

public class Day5 extends Day {
	public static void main(String[] args) {
		new Day5().run();
	}

	@Override
	public String name() {
		return "Hydrothermal Venture";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Line2D> lines = loadData(input);
		lines.removeIf(Line2D::isDiagonal);

		final Map<Point2D, AtomicInteger> counts = new HashMap<>();
		for (Line2D line : lines) {
			switch (line.direction()) {
			case VERTICAL:
				for (int y = line.minY(); y <= line.maxY(); y++) {
					incrementCount(counts, line.minX(), y);
				}
				break;
			case HORIZONTAL:
				for (int x = line.minX(); x <= line.maxX(); x++) {
					incrementCount(counts, x, line.minY());
				}
				break;
			default:
			}
		}

		if (Logger.isDebugEnabled()) {
			print(counts);
		}

		return Long.toString(counts.values().stream().filter(count -> count.get() >= 2).count());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Line2D> lines = loadData(input);

		final Map<Point2D, AtomicInteger> counts = new HashMap<>();
		for (Line2D line : lines) {
			switch (line.direction()) {
			case DIAGONAL:
				int y = line.y1();
				// Lines can only be at 45 degrees
				for (int x = line.x1();;) {
					incrementCount(counts, x, y);

					if (line.goesBackwards()) {
						if (--x < line.x2()) {
							break;
						}
					} else {
						if (++x > line.x2()) {
							break;
						}
					}
					if (line.goesDown()) {
						y--;
					} else {
						y++;
					}
				}
				break;
			case VERTICAL:
				for (y = line.minY(); y <= line.maxY(); y++) {
					incrementCount(counts, line.minX(), y);
				}
				break;
			case HORIZONTAL:
				for (int x = line.minX(); x <= line.maxX(); x++) {
					incrementCount(counts, x, line.minY());
				}
				break;
			default:
			}
		}

		if (Logger.isDebugEnabled()) {
			print(counts);
		}

		return Long.toString(counts.values().stream().filter(count -> count.get() > 1).count());
	}

	private static List<Line2D> loadData(final Path input) throws IOException {
		// Can't use Stream.toList() as that returns an immutable list
		return Files.lines(input).map(Line2D::create).collect(Collectors.toList());
	}

	private static void print(final Map<Point2D, AtomicInteger> counts) {
		final int max_x = counts.keySet().stream().mapToInt(point -> point.x()).max().getAsInt();
		final int max_y = counts.keySet().stream().mapToInt(point -> point.y()).max().getAsInt();
		Logger.debug("max_x: {}, max_y: {}", max_x, max_y);
		if (max_x < 20 && max_y < 20) {
			for (int y = 0; y <= max_y; y++) {
				for (int x = 0; x <= max_x; x++) {
					final Point2D p = new Point2D(x, y);
					AtomicInteger count = counts.get(p);
					if (count == null) {
						System.out.print(".");
					} else {
						System.out.print(count.get());
					}
				}
				System.out.println();
			}
		}
	}

	private static void incrementCount(final Map<Point2D, AtomicInteger> counts, final int x, final int y) {
		counts.computeIfAbsent(new Point2D(x, y), p -> new AtomicInteger()).incrementAndGet();
	}
}
