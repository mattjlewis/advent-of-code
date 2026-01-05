package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;

public class Day4 extends Day {
	public static void main(String[] args) {
		new Day4().run();
	}

	@Override
	public String name() {
		return "Printing Department";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final PrintRoom print_room = new PrintRoom(TextParser.loadPoints(input, '@'));

		return Long.toString(print_room.stream().filter(print_room::isAccessible).count());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final PrintRoom print_room = new PrintRoom(TextParser.loadPoints(input, '@'));
		final int num_rolls_of_paper = print_room.count();

		while (true) {
			final Set<Point2D> accessible_rolls_of_paper = print_room.stream().filter(print_room::isAccessible)
					.collect(Collectors.toSet());
			if (accessible_rolls_of_paper.isEmpty()) {
				break;
			}
			print_room.removeAll(accessible_rolls_of_paper);
		}

		return Integer.toString(num_rolls_of_paper - print_room.count());
	}

	private static record PrintRoom(Set<Point2D> rollOfPaperCoords) {
		public boolean isAccessible(Point2D p) {
			/*
			 * The forklifts can only access a roll of paper if there are fewer than four rolls of
			 * paper in the eight adjacent positions.
			 */
			return rollOfPaperCoords.contains(p)
					&& CompassDirection.stream().map(p::move).filter(rollOfPaperCoords::contains).count() < 4;
		}

		public int count() {
			return rollOfPaperCoords.size();
		}

		public void removeAll(Set<Point2D> coords) {
			rollOfPaperCoords.removeAll(coords);
		}

		public Stream<Point2D> stream() {
			return rollOfPaperCoords.stream();
		}
	}
}
