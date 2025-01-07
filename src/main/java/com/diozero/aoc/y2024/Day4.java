package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.MutablePoint2D;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;

public class Day4 extends Day {
	private static final char[] WORD = "XMAS".toCharArray();

	public static void main(String[] args) {
		new Day4().run();
	}

	@Override
	public String name() {
		return "Ceres Search";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final char[][] grid = TextParser.loadCharMatrix(input);
		int count = 0;
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[y].length; x++) {
				if (grid[y][x] == WORD[0]) {
					final Point2D p = new Point2D(x, y);
					count += Arrays.stream(CompassDirection.values())
							.filter(dir -> check(grid, dir, p.mutable().translate(dir), 1)).count();
				}
			}
		}

		return Integer.toString(count);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final char[][] grid = TextParser.loadCharMatrix(input);
		int count = 0;
		for (int y = 1; y < grid.length - 1; y++) {
			for (int x = 1; x < grid[y].length - 1; x++) {
				if (grid[y][x] == WORD[2]) {
					final Point2D p = new Point2D(x, y);
					if (Stream.of(CompassDirection.NORTH_WEST, CompassDirection.NORTH_EAST)
							.filter(dir -> letters(grid, dir, p)).count() == 2) {
						count++;
					}
				}
			}
		}

		return Integer.toString(count);
	}

	private static final boolean letters(char[][] grid, CompassDirection dir, Point2D p) {
		final Point2D p1 = p.move(dir);
		final Point2D p2 = p.move(dir.opposite());
		return grid[p1.y()][p1.x()] == WORD[1] && grid[p2.y()][p2.x()] == WORD[3]
				|| grid[p1.y()][p1.x()] == WORD[3] && grid[p2.y()][p2.x()] == WORD[1];
	}

	private static final boolean check(char[][] grid, CompassDirection dir, MutablePoint2D pos, int index) {
		if (pos.x() < 0 || pos.x() >= grid[0].length || pos.y() < 0 || pos.y() >= grid.length) {
			return false;
		}

		if (grid[pos.y()][pos.x()] != WORD[index]) {
			return false;
		}

		if (index == WORD.length - 1) {
			return true;
		}

		return check(grid, dir, pos.translate(dir), index + 1);
	}
}
