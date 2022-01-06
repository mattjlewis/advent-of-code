package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;

public class Day11 extends AocBase {
	public static void main(String[] args) {
		new Day11().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		return getNumOccupied(loadData(input), Day11::part1OccupancyLogic);
	}

	@Override
	public long part2(Path input) throws IOException {
		return getNumOccupied(loadData(input), Day11::part2OccupancyLogic);
	}

	private static long getNumOccupied(Boolean[][] seats, OccupancyLogic occupancyLogic) {
		int width = seats[0].length;
		int height = seats.length;

		// After step 1 all seats become occupied
		Boolean[][] occupancy = clone(seats);
		int step = 1;
		boolean move = true;

		while (move) {
			Boolean[][] new_occupancy = new Boolean[height][width];
			move = false;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (!seats[y][x].booleanValue()) {
						new_occupancy[y][x] = Boolean.FALSE;
						continue;
					}

					Optional<Boolean> result = occupancyLogic.seatChange(seats, occupancy, x, y);
					if (result.isPresent()) {
						new_occupancy[y][x] = result.get();
						move = true;
					} else {
						new_occupancy[y][x] = occupancy[y][x];
					}
				}
			}

			occupancy = new_occupancy;
			step++;
			if (Logger.isDebugEnabled()) {
				print(seats, occupancy, step);
			}
		}

		Logger.debug("No more seat changes after {} step(s)", Integer.valueOf(step));

		int num_occupied = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (occupancy[y][x].booleanValue()) {
					num_occupied++;
				}
			}
		}

		return num_occupied;
	}

	/*
	 * Works on the number of immediately adjacent occupied seats.
	 */
	private static Optional<Boolean> part1OccupancyLogic(Boolean[][] seats, Boolean[][] occupancy, int x, int y) {
		int num_occupied = 0;
		for (int yy = Math.max(0, y - 1); yy <= Math.min(seats.length - 1, y + 1); yy++) {
			for (int xx = Math.max(0, x - 1); xx <= Math.min(seats[0].length - 1, x + 1); xx++) {
				if (yy == y && xx == x) {
					continue;
				}
				if (seats[yy][xx].booleanValue() && occupancy[yy][xx].booleanValue()) {
					num_occupied++;
				}
			}
		}

		// Empty seats that have no adjacent occupied seats become occupied.
		if (!occupancy[y][x].booleanValue() && num_occupied == 0) {
			return Optional.of(Boolean.TRUE);
		}

		// Occupied seats that have four or more adjacent occupied seats become empty.
		if (occupancy[y][x].booleanValue() && num_occupied >= 4) {
			return Optional.of(Boolean.FALSE);
		}

		// Otherwise, the seat's state does not change.
		return Optional.empty();
	}

	/*
	 * Works on the number of visible occupied seats.
	 */
	private static Optional<Boolean> part2OccupancyLogic(Boolean[][] seats, Boolean[][] occupancy, int x, int y) {
		int num_occupied = 0;

		// Go in each direction
		for (Direction direction : Direction.values()) {
			num_occupied += hasVisibleOccupiedSeats(seats, occupancy, x, y, direction) ? 1 : 0;
		}

		// Empty seats that have no visible occupied seats become occupied.
		if (!occupancy[y][x].booleanValue() && num_occupied == 0) {
			return Optional.of(Boolean.TRUE);
		}

		// Occupied seats that see at least five visible occupied seats become empty.
		if (occupancy[y][x].booleanValue() && num_occupied >= 5) {
			return Optional.of(Boolean.FALSE);
		}

		// Otherwise, the seat's state does not change.
		return Optional.empty();
	}

	private static boolean hasVisibleOccupiedSeats(Boolean[][] seats, Boolean[][] occupancy, int x, int y,
			Direction direction) {
		int xx = x + direction.dx();
		int yy = y + direction.dy();
		while (xx >= 0 && xx < seats[0].length && yy >= 0 && yy < seats.length) {
			if (seats[yy][xx].booleanValue()) {
				return occupancy[yy][xx].booleanValue();
			}

			xx += direction.dx();
			yy += direction.dy();
		}

		return false;
	}

	private static Boolean[][] loadData(Path input) throws IOException {
		return Files.lines(input)
				.map(line -> line.chars().mapToObj(ch -> Boolean.valueOf(ch == 'L')).toArray(Boolean[]::new))
				.toArray(Boolean[][]::new);
	}

	private static Boolean[][] clone(Boolean[][] array) {
		Boolean[][] new_array = new Boolean[array.length][array[0].length];

		for (int y = 0; y < array.length; y++) {
			System.arraycopy(array[y], 0, new_array[y], 0, new_array[y].length);
		}

		return new_array;
	}

	private static void print(Boolean[][] seats, Boolean[][] occupied, int step) {
		System.out.println("After step " + step + ":");
		for (int y = 0; y < occupied.length; y++) {
			for (int x = 0; x < occupied[y].length; x++) {
				System.out.print(occupied[y][x].booleanValue() ? '#' : seats[y][x].booleanValue() ? 'L' : '.');
			}
			System.out.println();
		}
		System.out.println();
	}

	@FunctionalInterface
	private interface OccupancyLogic {
		/**
		 * Determine if a seat's occupancy should change.
		 *
		 * @param seats     Seat locations
		 * @param occupancy Previous occupancy locations
		 * @param x         Current seat x position
		 * @param y         Current seat y position
		 * @return True if the seat should become occupied, false if it should become
		 *         vacant, empty if no change
		 */
		Optional<Boolean> seatChange(Boolean[][] seats, Boolean[][] occupancy, int x, int y);
	}

	private enum Direction {
		UP(0, -1), UP_RIGHT(1, -1), RIGHT(1, 0), DOWN_RIGHT(1, 1), DOWN(0, 1), DOWN_LEFT(-1, 1), LEFT(-1, 0),
		UP_LEFT(-1, -1);

		private int dx;
		private int dy;

		Direction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		public int dx() {
			return dx;
		}

		public int dy() {
			return dy;
		}
	}
}
