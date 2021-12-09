import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day9 {
	public static void main(String[] args) {
		// String input_file = "day9sample.txt";
		String input_file = "day9.txt";
		Path input_path = Path.of(input_file);
		try {
			part1(input_path);
			System.out.println();
			part2(input_path);
		} catch (IOException e) {
			System.out.println("Error unable to read input file '" + input_file + "'");
		}
	}

	private static void part1(Path input) throws IOException {
		int[][] heights = Files.lines(input).map(line -> line.chars().map(c -> c - 48).toArray())
				.collect(Collectors.toList()).toArray(int[][]::new);
		for (int[] height : heights) {
			System.out.println(Arrays.toString(height));
		}

		List<Point> low_points = getLowPoints(heights);
		int risk_level = low_points.stream().mapToInt(point -> 1 + heights[point.y][point.x]).sum();
		System.out.println("risk level: " + risk_level);
	}

	private static List<Point> getLowPoints(int[][] heights) {
		List<Point> low_points = new ArrayList<>();

		for (int y = 0; y < heights.length; y++) {
			for (int x = 0; x < heights[y].length; x++) {
				int height = heights[y][x];

				int up = (y - 1) < 0 ? Integer.MAX_VALUE : heights[y - 1][x];
				int down = (y + 1) < heights.length ? heights[y + 1][x] : Integer.MAX_VALUE;
				int left = (x - 1) < 0 ? Integer.MAX_VALUE : heights[y][x - 1];
				int right = (x + 1) < heights[y].length ? heights[y][x + 1] : Integer.MAX_VALUE;

				if (height < up && height < down && height < left && height < right) {
					low_points.add(new Point(x, y));
				}
			}
		}

		return low_points;
	}

	private static void part2(Path input) throws IOException {
		int[][] heights = Files.lines(input).map(line -> line.chars().map(c -> c - 48).toArray())
				.collect(Collectors.toList()).toArray(int[][]::new);

		// First find the low points
		List<Point> low_points = getLowPoints(heights);

		// Radiate out from each of the low points to determine the basin size
		int[] basin_sizes = low_points.stream().mapToInt(point -> -1 * getBasinSize(point, heights)).sorted().limit(3)
				.map(size -> size * -1).toArray();
		System.out.println(Arrays.toString(basin_sizes));
		int result = low_points.stream().mapToInt(point -> -1 * getBasinSize(point, heights)).sorted().limit(3)
				.map(size -> size * -1).reduce(1, (a, b) -> a * b);
		System.out.println(result);
	}

	public static int getBasinSize(Point point, int[][] heights) {
		return getBasinSize(point.x, point.y, heights, new boolean[heights.length][heights[0].length], 1);
	}

	public static int getBasinSize(int x, int y, int[][] heights, boolean[][] checked, int basinSize) {
		checked[y][x] = true;

		// Recurse outward in all directions from this low point until we hit an edge or
		// a 9
		int up = y - 1;
		if (up >= 0 && !checked[up][x] && heights[up][x] != 9) {
			basinSize = getBasinSize(x, up, heights, checked, basinSize + 1);
		}

		int down = y + 1;
		if (down < heights.length && !checked[down][x] && heights[down][x] != 9) {
			basinSize = getBasinSize(x, down, heights, checked, basinSize + 1);
		}

		int left = x - 1;
		if (left >= 0 && !checked[y][left] && heights[y][left] != 9) {
			basinSize = getBasinSize(left, y, heights, checked, basinSize + 1);
		}

		int right = x + 1;
		if (right < heights[0].length && !checked[y][right] && heights[y][right] != 9) {
			basinSize = getBasinSize(right, y, heights, checked, basinSize + 1);
		}

		return basinSize;
	}
}
