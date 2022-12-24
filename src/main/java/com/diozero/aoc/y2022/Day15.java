package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.geometry.Rectangle;
import com.diozero.aoc.util.IntRange;

public class Day15 extends Day {
	// Sensor at x=2, y=18: closest beacon is at x=-2, y=15
	private static final Pattern PATTERN = Pattern
			.compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)");

	public static void main(String[] args) {
		new Day15().run();
	}

	@Override
	public String name() {
		return "Beacon Exclusion Zone";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final int row = isSample() ? 10 : 2_000_000;

		// Load only the sensors that would detect a beacon on the specified row
		final List<Sensor> sensors = load(input).toList();
		final List<IntRange> ranges = sensors.stream().map(s -> s.beaconsRange(row)).filter(Optional::isPresent)
				.map(Optional::get).collect(Collectors.toList());
		IntRange.merge(ranges);

		// Get the number of sensors and beacons on this row
		long num_sensors_and_beacons_on_row = sensors.stream().flatMap(Sensor::locations).filter(p -> p.y() == row)
				.distinct().count();

		return Long.toString(ranges.stream().mapToLong(r -> r.size()).sum() - num_sensors_and_beacons_on_row);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final int x_factor = 4_000_000;
		final Rectangle bounds = isSample() ? Rectangle.create(0, 0, 20, 20)
				: Rectangle.create(0, 0, x_factor, x_factor);

		final List<Sensor> sensors = load(input).toList();

		long x = 0, y = 0;
		// TODO Find a more efficient solution that avoids iterating over every row
		for (int row = 0; row <= bounds.y2(); row++) {
			final int f_row = row;
			final List<IntRange> ranges = sensors.stream().map(s -> s.beaconsRange(f_row)).filter(Optional::isPresent)
					.map(Optional::get).collect(Collectors.toList());
			IntRange.merge(ranges);
			if (ranges.size() > 1) {
				y = row;
				if (ranges.get(0).endInclusive() < ranges.get(1).startInclusive()) {
					x = ranges.get(0).endInclusive() + 1;
				} else {
					x = ranges.get(1).endInclusive() + 1;
				}
				break;
			}
		}

		return Long.toString(x * x_factor + y);
	}

	private static Stream<Sensor> load(Path input) throws IOException {
		return Files.lines(input).map(Sensor::parse);
	}

	private static record Sensor(Point2D location, Point2D nearestBeacon, int manhattanDistance) {

		static Sensor parse(String s) {
			Matcher m = PATTERN.matcher(s);
			if (!m.matches()) {
				throw new IllegalArgumentException(
						"Line '" + s + "' does not match the pattern '" + PATTERN.pattern() + "'");
			}

			final Point2D location = new Point2D(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
			final Point2D nearest_beacon = new Point2D(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
			final int md = location.manhattanDistance(nearest_beacon);

			return new Sensor(location, nearest_beacon, md);
		}

		public Optional<IntRange> beaconsRange(int row) {
			final int range = manhattanDistance - (Math.abs(location.y() - row));
			if (range <= 0) {
				return Optional.empty();
			}
			return Optional.of(IntRange.of(location.x() - range, location.x() + range));
		}

		public Stream<Point2D> locations() {
			return Stream.of(location, nearestBeacon);
		}
	}
}
