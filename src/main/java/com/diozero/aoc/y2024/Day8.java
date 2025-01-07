package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.ArrayUtil;
import com.diozero.aoc.util.TextParser;

public class Day8 extends Day {
	public static void main(String[] args) {
		new Day8().run();
	}

	@Override
	public String name() {
		return "Resonant Collinearity";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(Puzzle.load(input).numAntinodes(false));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(Puzzle.load(input).numAntinodes(true));
	}

	private static final record Puzzle(Map<Character, List<Point2D>> antennas, int width, int height) {
		public static Puzzle load(Path input) throws IOException {
			final Map<Character, List<Point2D>> antennas = new HashMap<>();

			final Iterator<String> it = Files.lines(input).iterator();
			int y;
			int x = 0;
			for (y = 0; it.hasNext(); y++) {
				PrimitiveIterator.OfInt char_it = it.next().chars().iterator();
				for (x = 0; char_it.hasNext(); x++) {
					final Character ch = Character.valueOf((char) char_it.nextInt());
					if (ch.charValue() != TextParser.UNSET_CHAR) {
						final Point2D location = new Point2D(x, y);
						antennas.computeIfAbsent(ch, key -> new ArrayList<>()).add(location);
					}
				}
			}

			return new Puzzle(antennas, x, y);
		}

		public int numAntinodes(boolean part2) {
			return antennas.values().stream().flatMap(antenna_locations -> antinodes(antenna_locations, part2))
					.collect(Collectors.toSet()).size();
		}

		private Stream<Point2D> antinodes(List<Point2D> locations, boolean part2) {
			return ArrayUtil.pairCombinations(locations)
					.flatMap(combination -> antinodes(combination.first(), combination.second(), part2));
		}

		private Stream<Point2D> antinodes(Point2D p1, Point2D p2, boolean part2) {
			final Point2D delta = p2.delta(p1);
			final Point2D inv_delta = delta.scale(-1);

			final AtomicBoolean take1 = new AtomicBoolean(true);
			final AtomicBoolean take2 = new AtomicBoolean(true);
			return Stream.concat(
					Stream.iterate(part2 ? p1 : p1.translate(delta),
							p -> p.inBounds(width, height) && (part2 ? take1.get() : take1.getAndSet(false)),
							p -> p.translate(delta)),
					Stream.iterate(part2 ? p2 : p2.translate(inv_delta),
							p -> p.inBounds(width, height) && (part2 ? take2.get() : take2.getAndSet(false)),
							p -> p.translate(inv_delta)));
		}
	}
}
