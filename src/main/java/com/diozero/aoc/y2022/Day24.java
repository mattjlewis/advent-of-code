package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.MutablePoint2D;
import com.diozero.aoc.geometry.Point2D;

public class Day24 extends Day {
	public static void main(String[] args) {
		new Day24().run();
	}

	@Override
	public String name() {
		return "Blizzard Basin";
	}

	@Override
	public String part1(Path input) throws IOException {
		return Integer.toString(Puzzle.load(input).countSteps());
	}

	@Override
	public String part2(Path input) throws IOException {
		final Puzzle puzzle = Puzzle.load(input);

		return Integer
				.toString(puzzle.countSteps() + puzzle.countSteps(puzzle.end, puzzle.start) + puzzle.countSteps());
	}

	private static record Puzzle(List<Blizzard> blizzards, int width, int height, Point2D start, Point2D end) {
		public static Puzzle load(Path input) throws IOException {
			final List<Blizzard> blizzards = new ArrayList<>();

			int y = 0;
			int x = 0;
			for (String line : Files.readAllLines(input)) {
				for (x = 0; x < line.length(); x++) {
					final char ch = line.charAt(x);
					if (ch == '<' || ch == '>' || ch == '^' || ch == 'v') {
						blizzards.add(new Blizzard(new MutablePoint2D(x, y), CompassDirection.fromArrow(ch)));
					}
				}
				y++;
			}

			final int width = x;
			final int height = y;

			final Point2D start = new Point2D(1, 0);
			final Point2D end = new Point2D(width - 2, height - 1);

			return new Puzzle(blizzards, width, height, start, end);
		}

		public boolean inBounds(Point2D p) {
			return p.x() == 1 && p.y() == 0 || p.x() == width - 2 && p.y() == height - 1
					|| p.inBounds(1, 1, width - 1, height - 1);
		}

		public int countSteps() {
			return countSteps(start, end);
		}

		public int countSteps(Point2D startPos, Point2D endPos) {
			int steps = 0;
			Set<Point2D> positions = new HashSet<>();
			positions.add(startPos);

			do {
				blizzards.forEach(b -> b.move(width, height));
				final Set<Point2D> blizzard_positions = blizzards.stream().map(Blizzard::position)
						.map(MutablePoint2D::immutable).collect(Collectors.toSet());

				final Set<Point2D> next = new HashSet<>();
				for (Point2D pos : positions) {
					CompassDirection.NESW.stream().map(pos::move).filter(this::inBounds)
							.filter(p -> !blizzard_positions.contains(p)).forEach(next::add);

					// We can stay where we are if not in a blizzard
					if (!blizzard_positions.contains(pos)) {
						next.add(pos);
					}
				}

				positions = next;
				steps++;
			} while (!positions.contains(endPos));

			return steps;
		}
	}

	private static record Blizzard(MutablePoint2D position, CompassDirection direction) {
		public void move(int width, int height) {
			position.translate(direction);
			if (position.x() > width - 2) {
				position.setX(1);
			} else if (position.x() < 1) {
				position.setX(width - 2);
			}
			if (position.y() > height - 2) {
				position.setY(1);
			} else if (position.y() < 1) {
				position.setY(height - 2);
			}
		}
	}
}
