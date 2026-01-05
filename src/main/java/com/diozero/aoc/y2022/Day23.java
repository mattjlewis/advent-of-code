package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.geometry.Rectangle;
import com.diozero.aoc.util.CircularLinkedList;
import com.diozero.aoc.util.TextParser;

public class Day23 extends Day {
	public static void main(String[] args) {
		new Day23().run();
	}

	@Override
	public String name() {
		return "Unstable Diffusion";
	}

	@Override
	public String part1(Path input) throws IOException {
		final Set<Point2D> elves = TextParser.loadPoints(input);
		final CircularLinkedList<List<CompassDirection>> directions = initialiseDirections();

		IntStream.range(0, 10).forEach(i -> processMoves(elves, directions));
		final Rectangle bounds = Point2D.getBounds(elves);

		return Integer.toString(bounds.width() * bounds.height() - elves.size());
	}

	@Override
	public String part2(Path input) throws IOException {
		final Set<Point2D> elves = TextParser.loadPoints(input);
		final CircularLinkedList<List<CompassDirection>> directions = initialiseDirections();

		int index = 0;
		do {
			index++;
		} while (processMoves(elves, directions));

		return Integer.toString(index);
	}

	private static CircularLinkedList<List<CompassDirection>> initialiseDirections() {
		/*-
		 * 1. If there is no Elf in the N, NE, or NW adjacent positions, the Elf proposes moving north one step.
		 * 2. If there is no Elf in the S, SE, or SW adjacent positions, the Elf proposes moving south one step.
		 * 3. If there is no Elf in the W, NW, or SW adjacent positions, the Elf proposes moving west one step.
		 * 4. If there is no Elf in the E, NE, or SE adjacent positions, the Elf proposes moving east one step.
		 * Rotate the order every iteration
		 */
		return new CircularLinkedList<>(
				// XXX Note North and South are swapped
				List.of(List.of(CompassDirection.SOUTH, CompassDirection.SOUTH_EAST, CompassDirection.SOUTH_WEST),
						List.of(CompassDirection.NORTH, CompassDirection.NORTH_EAST, CompassDirection.NORTH_WEST),
						List.of(CompassDirection.WEST, CompassDirection.SOUTH_WEST, CompassDirection.NORTH_WEST),
						List.of(CompassDirection.EAST, CompassDirection.SOUTH_EAST, CompassDirection.NORTH_EAST)));
	}

	private static boolean processMoves(Collection<Point2D> elves,
			CircularLinkedList<List<CompassDirection>> directions) {
		// Map from proposed position to list of elves who want to move there
		final Map<Point2D, List<Point2D>> proposed_moves = new HashMap<>();
		for (Point2D elf : elves) {
			// If there is at least one elf next to this elf, then the elf proposes a move
			if (CompassDirection.stream().map(elf::move).anyMatch(elves::contains)) {
				int x;
				for (x = 0; x < directions.size(); x++) {
					final List<CompassDirection> dirs = directions.poll();
					if (dirs.stream().map(elf::move).noneMatch(elves::contains)) {
						proposed_moves.computeIfAbsent(elf.move(dirs.getFirst()), k -> new ArrayList<>()).add(elf);
						break;
					}
				}
				for (x++; x < directions.size(); x++) {
					directions.poll();
				}
			}
		}

		// Move elves to positions where there is only one elf who wants to move there
		final AtomicBoolean elves_moved = new AtomicBoolean(false);
		proposed_moves.entrySet().stream().filter(entry -> entry.getValue().size() == 1).forEach(entry -> {
			elves.remove(entry.getValue().getFirst());
			elves.add(entry.getKey());
			elves_moved.set(true);
		});

		// Shift the list of directions
		directions.poll();

		return elves_moved.get();
	}
}
