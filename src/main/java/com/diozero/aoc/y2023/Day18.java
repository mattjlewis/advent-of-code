package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;

public class Day18 extends Day {
	public static void main(String[] args) {
		new Day18().run();
	}

	@Override
	public String name() {
		return "Lavaduct Lagoon";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(calculateArea(Files.lines(input).map(Instruction::parsePart1).toList()));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(calculateArea(Files.lines(input).map(Instruction::parsePart2).toList()));
	}

	private static long calculateArea(List<Instruction> instructions) {
		final List<Point2D> trench_vertices = new ArrayList<>();
		trench_vertices.add(Point2D.ORIGIN);

		instructions.forEach(instruction -> trench_vertices
				.add(trench_vertices.getLast().move(instruction.direction, instruction.distance)));

		return Point2D.area(trench_vertices);
	}

	private static record Instruction(CompassDirection direction, int distance) {
		final static Pattern PATTERN = Pattern.compile("([UDLR]) (\\d+) \\(#([0-9a-f]{5})([0-3])\\)");

		public static Instruction parsePart1(String line) {
			final Matcher m = PATTERN.matcher(line);
			if (!m.matches()) {
				throw new IllegalArgumentException("Invalid line '" + line + "'");
			}

			return new Instruction(CompassDirection.fromUdlrSwapped(m.group(1)), Integer.parseInt(m.group(2)));
		}

		public static Instruction parsePart2(String line) {
			final Matcher m = PATTERN.matcher(line);
			if (!m.matches()) {
				throw new IllegalArgumentException("Invalid line '" + line + "'");
			}

			final CompassDirection dir = switch (Integer.parseInt(m.group(4))) {
			case 0 -> CompassDirection.EAST;
			case 1 -> CompassDirection.NORTH;
			case 2 -> CompassDirection.WEST;
			case 3 -> CompassDirection.SOUTH;
			default -> throw new IllegalArgumentException("Invalid direction " + m.group(4));
			};

			return new Instruction(dir, Integer.parseInt(m.group(3), 16));
		}
	}
}
