package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Line2D;
import com.diozero.aoc.geometry.MutablePoint2D;
import com.diozero.aoc.geometry.Point2D;

public class Day3 extends Day {
	public static void main(String[] args) {
		new Day3().run();
	}

	@Override
	public String name() {
		return "Crossed Wires";
	}

	@Override
	public String part1(Path input) throws IOException {
		final List<List<Line2D>> paths = loadData(input);

		// Find the intersection that is closest to ORIGIN
		return Integer.toString(Line2D.pathIntersections(paths.get(0), paths.get(1)).stream()
				.mapToInt(p -> p.manhattanDistance(Point2D.ORIGIN)).min().orElseThrow());
	}

	@Override
	public String part2(Path input) throws IOException {
		final List<List<Line2D>> paths = loadData(input);

		// Find the intersection that has the shortest distance from ORIGIN when
		// considering the sum of the distances for both wires
		return Integer.toString(Line2D.pathIntersections(paths.get(0), paths.get(1)).stream().mapToInt(
				intersection -> paths.stream().mapToInt(path -> distanceToIntersection(path, intersection)).sum()).min()
				.orElseThrow());
	}

	private static List<List<Line2D>> loadData(Path input) throws IOException {
		return Files.lines(input).map(Day3::parse).toList();
	}

	private static List<Line2D> parse(String line) {
		// Convert a stream of relative segments to a list of absolute lines
		// Note there is no need to call Stream.sequential() as Pattern.results()
		// returns a sequential stream
		final MutablePoint2D p = Point2D.ORIGIN.mutable();
		return Segment.parse(line)
				.map(segment -> Line2D.create(p.immutable(), p.translate(segment.delta()).immutable())).toList();
	}

	private static int distanceToIntersection(List<Line2D> path, Point2D intersection) {
		// Calculate the distance to get to this intersection point in path
		// Note can't use takeWhile as that strips the last line that intersects
		int distance = 0;
		for (Line2D line : path) {
			if (line.contains(intersection)) {
				distance += intersection.manhattanDistance(line.x1(), line.y1());
				break;
			}

			distance += line.length();
		}

		return distance;
	}

	private static record Segment(CompassDirection direction, int amount) {
		private static final Map<String, CompassDirection> DIRECTION_MAPPING;
		static {
			DIRECTION_MAPPING = new HashMap<>();
			DIRECTION_MAPPING.put("U", CompassDirection.NORTH);
			DIRECTION_MAPPING.put("R", CompassDirection.EAST);
			DIRECTION_MAPPING.put("D", CompassDirection.SOUTH);
			DIRECTION_MAPPING.put("L", CompassDirection.WEST);
		}

		private static final Pattern PATTERN = Pattern.compile("(U|R|D|L)(\\d+)");

		public static Stream<Segment> parse(String line) {
			return PATTERN.matcher(line).results().map(Segment::create);
		}

		private static Segment create(MatchResult matchResult) {
			return new Segment(DIRECTION_MAPPING.get(matchResult.group(1)), Integer.parseInt(matchResult.group(2)));
		}

		public Point2D delta() {
			return direction.delta().scale(amount);
		}

		@Override
		public String toString() {
			return direction.toString() + amount;
		}
	}
}
