package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Path;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.TextParser;

public class Day3 extends Day {
	private static final char TREE = TextParser.SET_CHAR;
	private static final char BLANK = TextParser.UNSET_CHAR;
	private static final char HIT_TREE = 'X';
	private static final char SLED = 'O';

	public static void main(String[] args) {
		new Day3().run();
	}

	@Override
	public String name() {
		return "Toboggan Trajectory";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(getHits(TextParser.loadBooleanArray(input), 3, 1));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final boolean[][] trees = TextParser.loadBooleanArray(input);

		return Long.toString(getHits(trees, 1, 1) * getHits(trees, 3, 1) * getHits(trees, 5, 1) * getHits(trees, 7, 1)
				* getHits(trees, 1, 2));
	}

	private static void print(final boolean[][] trees, final int sledX, final int sledY) {
		for (int y = 0; y < trees.length; y++) {
			for (int x = 0; x < trees[y].length; x++) {
				if (sledX == x && sledY == y) {
					System.out.print(trees[y][x] ? HIT_TREE : SLED);
				} else {
					System.out.print(trees[y][x] ? TREE : BLANK);
				}
			}
			System.out.println();
		}
	}

	private static long getHits(final boolean[][] trees, final int deltaX, final int deltaY) {
		final int width = trees[0].length;

		int x = 0;
		int y = 0;
		int hits = 0;

		do {
			if (trees[y][x]) {
				hits++;
			}
			if (Logger.isDebugEnabled()) {
				print(trees, x, y);
			}

			x = (x + deltaX) % width;
			y += deltaY;
		} while (y < trees.length);

		return hits;
	}
}
