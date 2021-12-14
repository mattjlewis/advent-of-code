package com.diozero.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tinylog.Logger;

import com.diozero.aoc2021.util.AocBase;
import com.diozero.aoc2021.util.Point2D;

public class Day9 extends AocBase {
	public static void main(String[] args) {
		new Day9().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		final int[][] heights = loadData(input);
		return getLowPoints(heights).stream().mapToInt(point -> 1 + heights[point.y()][point.x()]).sum();
	}

	@Override
	public long part2(Path input) throws IOException {
		final int[][] heights = loadData(input);

		// Radiate out from each of the low points to determine the basin size
		// Note that mapToInt negates the numbers to sort in reverse order
		return getLowPoints(heights).stream()
				.mapToInt(point -> -1 * getBasinSize(point.x(), point.y(), heights,
						new boolean[heights.length][heights[0].length], 1))
				.sorted().limit(3).map(size -> size * -1).reduce(1, (a, b) -> a * b);
	}

	private static int[][] loadData(Path input) throws IOException {
		// Note the lazy conversion from ASCII character code to integer
		final int[][] heights = Files.lines(input).map(line -> line.chars().map(c -> c - 48).toArray())
				.toArray(int[][]::new);

		// Print the height grid if not too big
		if (heights.length < 20) {
			for (int[] height : heights) {
				Logger.debug("heights: {}", Arrays.toString(height));
			}
		}

		return heights;
	}

	private static List<Point2D> getLowPoints(int[][] heights) {
		final List<Point2D> low_points = new ArrayList<>();

		for (int y = 0; y < heights.length; y++) {
			for (int x = 0; x < heights[y].length; x++) {
				int height = heights[y][x];

				// Avoid worrying about edges by setting to max integer value
				int up = (y - 1) < 0 ? Integer.MAX_VALUE : heights[y - 1][x];
				int down = (y + 1) < heights.length ? heights[y + 1][x] : Integer.MAX_VALUE;
				int left = (x - 1) < 0 ? Integer.MAX_VALUE : heights[y][x - 1];
				int right = (x + 1) < heights[y].length ? heights[y][x + 1] : Integer.MAX_VALUE;

				if (height < up && height < down && height < left && height < right) {
					low_points.add(new Point2D(x, y));
				}
			}
		}

		return low_points;
	}

	private static int getBasinSize(final int x, final int y, final int[][] heights, final boolean[][] checked,
			final int basinSize) {
		checked[y][x] = true;

		int basin_size = basinSize;

		// Recurse outward in all directions until we hit an edge or a 9
		int up = y - 1;
		if (up >= 0 && !checked[up][x] && heights[up][x] != 9) {
			basin_size = getBasinSize(x, up, heights, checked, basin_size + 1);
		}

		int down = y + 1;
		if (down < heights.length && !checked[down][x] && heights[down][x] != 9) {
			basin_size = getBasinSize(x, down, heights, checked, basin_size + 1);
		}

		int left = x - 1;
		if (left >= 0 && !checked[y][left] && heights[y][left] != 9) {
			basin_size = getBasinSize(left, y, heights, checked, basin_size + 1);
		}

		int right = x + 1;
		if (right < heights[0].length && !checked[y][right] && heights[y][right] != 9) {
			basin_size = getBasinSize(right, y, heights, checked, basin_size + 1);
		}

		return basin_size;
	}
}
