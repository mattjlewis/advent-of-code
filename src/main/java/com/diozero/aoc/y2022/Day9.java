package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.MutablePoint2D;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.PrintUtil;

public class Day9 extends Day {
	public static void main(String[] args) {
		new Day9().run();
	}

	@Override
	public String name() {
		return "Rope Bridge";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return solve(input, 2);
	}

	@Override
	public String part2(final Path input) throws IOException {
		return solve(input, 10);
	}

	private static String solve(final Path input, final int numKnots) throws IOException {
		final Rope rope = Rope.create(numKnots);
		final Set<Point2D> tail_position_history = new HashSet<>();
		Files.lines(input).map(Move::parse).forEach(move -> rope.move(move, tail_position_history));
		if (Logger.isDebugEnabled()) {
			PrintUtil.print(tail_position_history);
		}
		return Integer.toString(tail_position_history.size());
	}

	private static record Move(CompassDirection dir, int amount) {
		public static Move parse(String line) {
			final String[] parts = line.split(" ");

			return new Move(CompassDirection.fromUdlr(parts[0]), Integer.parseInt(parts[1]));
		}
	}

	private static record Rope(List<MutablePoint2D> knots) {
		public static Rope create(int size) {
			final List<MutablePoint2D> knots = new ArrayList<>(size);
			final MutablePoint2D head = new MutablePoint2D(0, 0);
			knots.add(head);
			for (int i = 1; i < size; i++) {
				knots.add(head.clone());
			}
			return new Rope(knots);
		}

		public void move(Move move, Collection<Point2D> tailPositionHistory) {
			final MutablePoint2D head = knots.get(0);
			for (int i = 0; i < move.amount; i++) {
				// Move the head
				head.translate(move.dir());

				// Move each point in the tail towards the preceding point
				for (int j = 1; j < knots.size(); j++) {
					final MutablePoint2D prev = knots.get(j - 1);
					final MutablePoint2D p = knots.get(j);
					final Point2D delta = p.delta(prev);
					if (Math.abs(delta.x()) <= 1 && Math.abs(delta.y()) <= 1) {
						// Do nothing
					} else if (delta.x() == 0) {
						// Up/down
						p.translate(delta.y() < 0 ? CompassDirection.SOUTH : CompassDirection.NORTH);
					} else if (delta.y() == 0) {
						// Left/right
						p.translate(delta.x() < 0 ? CompassDirection.WEST : CompassDirection.EAST);
					} else {
						// Diagonal
						p.translate(delta.x() < 0 ? -1 : 1, delta.y() < 0 ? -1 : 1);
					}
				}
				tailPositionHistory.add(knots.get(knots.size() - 1).immutable());
			}
		}
	}
}
