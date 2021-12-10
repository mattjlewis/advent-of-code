package com.diozero.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc2021.util.AocBase;
import com.diozero.aoc2021.util.Line2D;
import com.diozero.aoc2021.util.Point2D;

public class Day5 extends AocBase {
	public static void main(String[] args) {
		new Day5().run();
	}

	private static void print(Map<Point2D, AtomicInteger> counts) {
		int max_x = counts.keySet().stream().mapToInt(point -> point.getX()).max().getAsInt();
		int max_y = counts.keySet().stream().mapToInt(point -> point.getY()).max().getAsInt();
		Logger.debug("max_x: {}, max_y: {}", max_x, max_y);
		if (max_x < 20 && max_y < 20) {
			for (int y = 0; y <= max_y; y++) {
				for (int x = 0; x <= max_x; x++) {
					Point2D p = new Point2D(x, y);
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

	private static List<Line2D> loadData(Path input) throws IOException {
		return Files.lines(input).map(Line2D::new).collect(Collectors.toList());
	}

	@Override
	public long part1(Path input) throws IOException {
		List<Line2D> lines = loadData(input);
		lines.removeIf(Line2D::isDiagonal);

		Map<Point2D, AtomicInteger> counts = new HashMap<>();
		for (Line2D line : lines) {
			switch (line.getDirection()) {
			case VERTICAL:
				for (int y = line.getY1(); y <= line.getY2(); y++) {
					incrementCount(counts, line.getX1(), y);
				}
				break;
			case HORIZONTAL:
				for (int x = line.getX1(); x <= line.getX2(); x++) {
					incrementCount(counts, x, line.getY1());
				}
				break;
			default:
			}
		}

		if (Logger.isDebugEnabled()) {
			print(counts);
		}

		long num = counts.values().stream().filter(count -> count.get() >= 2).count();
		return num;
	}

	@Override
	public long part2(Path input) throws IOException {
		List<Line2D> lines = loadData(input);

		Map<Point2D, AtomicInteger> counts = new HashMap<>();
		for (Line2D line : lines) {
			switch (line.getDirection()) {
			case DIAGONAL:
				int y = line.getY1();
				// Lines can only be at 45 degrees
				for (int x = line.getX1();;) {
					incrementCount(counts, x, y);

					if (line.goesBackwards()) {
						if (--x < line.getX2()) {
							break;
						}
					} else {
						if (++x > line.getX2()) {
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
				for (y = line.getY1(); y <= line.getY2(); y++) {
					incrementCount(counts, line.getX1(), y);
				}
				break;
			case HORIZONTAL:
				for (int x = line.getX1(); x <= line.getX2(); x++) {
					incrementCount(counts, x, line.getY1());
				}
			default:
			}
		}

		if (Logger.isDebugEnabled()) {
			print(counts);
		}

		long num = counts.values().stream().filter(count -> count.get() > 1).count();
		return num;
	}

	private static void incrementCount(Map<Point2D, AtomicInteger> counts, int x, int y) {
		Point2D p = new Point2D(x, y);
		AtomicInteger count = counts.get(p);
		if (count == null) {
			count = new AtomicInteger();
			counts.put(p, count);
		}
		count.incrementAndGet();
	}
}
