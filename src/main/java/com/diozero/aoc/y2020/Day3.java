package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;

public class Day3 extends AocBase {
	public static void main(String[] args) {
		new Day3().run();
	}

	@Override
	public String part1(Path input) throws IOException {
		return Long.toString(getHits(loadData(input), 3, 1));
	}

	@Override
	public String part2(Path input) throws IOException {
		Boolean[][] trees = loadData(input);

		return Long.toString(getHits(trees, 1, 1) * getHits(trees, 3, 1) * getHits(trees, 5, 1) * getHits(trees, 7, 1)
				* getHits(trees, 1, 2));
	}

	private static void print(Boolean[][] trees, int sledX, int sledY) {
		for (int y = 0; y < trees.length; y++) {
			for (int x = 0; x < trees[y].length; x++) {
				if (sledX == x && sledY == y) {
					System.out.print(trees[y][x].booleanValue() ? 'X' : 'O');
				} else {
					System.out.print(trees[y][x].booleanValue() ? '#' : '.');
				}
			}
			System.out.println();
		}
	}

	private static long getHits(Boolean[][] trees, int deltaX, int deltaY) {
		int width = trees[0].length;

		int x = 0;
		int y = 0;
		int hits = 0;

		do {
			if (trees[y][x].booleanValue()) {
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

	private static Boolean[][] loadData(Path input) throws IOException {
		return Files.lines(input)
				.map(line -> line.chars().mapToObj(ch -> Boolean.valueOf(ch == '#')).toArray(Boolean[]::new))
				.toArray(Boolean[][]::new);
	}

}
