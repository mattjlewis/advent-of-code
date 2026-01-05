package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.MatrixUtil;
import com.diozero.aoc.util.TextParser;

public class Day7 extends Day {
	public static void main(String[] args) {
		new Day7().run();
	}

	@Override
	public String name() {
		return "Laboratories";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final char[][] grid = TextParser.loadCharMatrix(input);
		final Point2D start = MatrixUtil.find(grid, 'S').get();
		final Set<Point2D> splitters = MatrixUtil.toPoints(grid, '^');

		Set<Point2D> beams = new HashSet<>();
		beams.add(start);
		int splits = 0;
		for (int row = start.y(); row < grid.length - start.y(); row++) {
			final Set<Point2D> new_beams = new HashSet<>();
			for (Point2D beam : beams) {
				final Point2D new_pos = beam.translate(0, 1);
				if (splitters.contains(new_pos)) {
					new_beams.add(new_pos.translate(-1, 0));
					new_beams.add(new_pos.translate(1, 0));
					splits++;
				} else {
					new_beams.add(new_pos);
				}
			}
			beams = new_beams;
		}

		return Integer.toString(splits);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final char[][] grid = TextParser.loadCharMatrix(input);

		int row = 0;
		char[] line = grid[row++];
		final long[] timelines = new long[line.length];
		for (int col = 0; col < line.length; col++) {
			timelines[col] = line[col] == 'S' ? 1L : 0L;
		}

		int splits = 0;
		do {
			line = grid[row++];
			for (int col = 0; col < line.length; col++) {
				if (line[col] == '^' && timelines[col] > 0L) {
					timelines[col - 1] += timelines[col];
					timelines[col + 1] += timelines[col];
					timelines[col] = 0L;
					splits++;
				}
			}
		} while (row < grid.length);

		return Long.toString(Arrays.stream(timelines).sum());
	}
}
