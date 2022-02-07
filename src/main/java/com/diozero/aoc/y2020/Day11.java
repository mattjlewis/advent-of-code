package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.util.ArrayUtil;
import com.diozero.aoc.util.TextParser;

public class Day11 extends Day {
	private static final char SEAT = 'L';
	private static final char BLANK = '.';
	private static final char OCCUPIED = '#';

	public static void main(String[] args) {
		new Day11().run();
	}

	@Override
	public String name() {
		return "Seating System";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(getNumOccupied(TextParser.loadBooleanArray(input, SEAT), Day11::part1OccupancyLogic));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(getNumOccupied(TextParser.loadBooleanArray(input, SEAT), Day11::part2OccupancyLogic));
	}

	private static long getNumOccupied(final boolean[][] seats, final OccupancyLogic occupancyLogic) {
		final int width = seats[0].length;
		final int height = seats.length;

		// After step 1 all seats become occupied
		boolean[][] occupancy = ArrayUtil.clone(seats);
		int step = 1;
		boolean move = true;

		while (move) {
			boolean[][] new_occupancy = new boolean[height][width];
			move = false;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (!seats[y][x]) {
						new_occupancy[y][x] = false;
						continue;
					}

					Optional<Boolean> result = occupancyLogic.seatChange(seats, occupancy, x, y);
					if (result.isPresent()) {
						new_occupancy[y][x] = result.get().booleanValue();
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
				if (occupancy[y][x]) {
					num_occupied++;
				}
			}
		}

		return num_occupied;
	}

	/*
	 * Works on the number of immediately adjacent occupied seats.
	 */
	private static Optional<Boolean> part1OccupancyLogic(final boolean[][] seats, final boolean[][] occupancy,
			final int x, final int y) {
		int num_occupied = 0;
		for (int yy = Math.max(0, y - 1); yy <= Math.min(seats.length - 1, y + 1); yy++) {
			for (int xx = Math.max(0, x - 1); xx <= Math.min(seats[0].length - 1, x + 1); xx++) {
				if (yy == y && xx == x) {
					continue;
				}
				if (seats[yy][xx] && occupancy[yy][xx]) {
					num_occupied++;
				}
			}
		}

		// Empty seats that have no adjacent occupied seats become occupied.
		if (!occupancy[y][x] && num_occupied == 0) {
			return Optional.of(Boolean.TRUE);
		}

		// Occupied seats that have four or more adjacent occupied seats become empty.
		if (occupancy[y][x] && num_occupied >= 4) {
			return Optional.of(Boolean.FALSE);
		}

		// Otherwise, the seat's state does not change.
		return Optional.empty();
	}

	/*
	 * Works on the number of visible occupied seats.
	 */
	private static Optional<Boolean> part2OccupancyLogic(final boolean[][] seats, final boolean[][] occupancy,
			final int x, final int y) {
		int num_occupied = 0;

		// Go in each direction
		for (CompassDirection direction : CompassDirection.values()) {
			num_occupied += hasVisibleOccupiedSeats(seats, occupancy, x, y, direction) ? 1 : 0;
		}

		// Empty seats that have no visible occupied seats become occupied.
		if (!occupancy[y][x] && num_occupied == 0) {
			return Optional.of(Boolean.TRUE);
		}

		// Occupied seats that see at least five visible occupied seats become empty.
		if (occupancy[y][x] && num_occupied >= 5) {
			return Optional.of(Boolean.FALSE);
		}

		// Otherwise, the seat's state does not change.
		return Optional.empty();
	}

	private static boolean hasVisibleOccupiedSeats(final boolean[][] seats, final boolean[][] occupancy, final int x,
			final int y, final CompassDirection direction) {
		int xx = x + direction.dx();
		int yy = y + direction.dy();
		while (xx >= 0 && xx < seats[0].length && yy >= 0 && yy < seats.length) {
			if (seats[yy][xx]) {
				return occupancy[yy][xx];
			}

			xx += direction.dx();
			yy += direction.dy();
		}

		return false;
	}

	private static void print(final boolean[][] seats, final boolean[][] occupied, final int step) {
		System.out.println("After step " + step + ":");
		for (int y = 0; y < occupied.length; y++) {
			for (int x = 0; x < occupied[y].length; x++) {
				System.out.print(occupied[y][x] ? OCCUPIED : seats[y][x] ? SEAT : BLANK);
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
		Optional<Boolean> seatChange(final boolean[][] seats, final boolean[][] occupancy, final int x, final int y);
	}
}
