package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point2D;

public class Day14 extends Day {
	private static final Pattern PATTERN = Pattern.compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");

	public static void main(String[] args) {
		new Day14().run();
	}

	@Override
	public String name() {
		return "Restroom Redoubt";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Robot> robots = PATTERN.matcher(Files.readString(input)).results().map(Robot::create).toList();

		final int width = isSample() ? 11 : 101;
		final int height = isSample() ? 7 : 103;

		final int seconds = 100;
		final List<Point2D> positions = robots.stream()
				.map(r -> r.position.translate(r.velocity.scale(seconds)).wrap(width, height)).toList();
		final int mid_x = width / 2;
		final int mid_y = height / 2;

		// Quadrants:
		// 01
		// 23
		return Long.toString(positions.stream().filter(p -> p.x() != mid_x && p.y() != mid_y)
				.collect(Collectors.groupingBy(p -> Integer.valueOf((p.y() > mid_y ? 2 : 0) + (p.x() > mid_x ? 1 : 0)),
						Collectors.counting()))
				.values().stream().mapToLong(Long::longValue).reduce(1L, (a, b) -> a * b));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Robot> robots = PATTERN.matcher(Files.readString(input)).results().map(Robot::create).toList();

		final int width = isSample() ? 11 : 101;
		final int height = isSample() ? 7 : 103;

		int seconds = 1;
		while (true) {
			final int seconds_final = seconds;
			final Set<Point2D> positions = robots.stream()
					.map(r -> r.position.translate(r.velocity.scale(seconds_final)).wrap(width, height))
					.collect(Collectors.toSet());

			// The assumption is that the Christmas tree is visible when all robots are in distinct
			// positions, and it worked \o/
			if (positions.size() == robots.size()) {
				// PrintUtil.print(positions);
				break;
			}

			seconds++;
		}

		return Integer.toString(seconds);
	}

	private static record Robot(Point2D position, Point2D velocity) {
		public static Robot create(MatchResult mr) {
			return new Robot(new Point2D(Integer.parseInt(mr.group(1)), Integer.parseInt(mr.group(2))),
					new Point2D(Integer.parseInt(mr.group(3)), Integer.parseInt(mr.group(4))));
		}
	}
}
