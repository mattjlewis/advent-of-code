import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day7 {
	public static void main(String[] args) {
		// String input_file = "day7sample.txt";
		String input_file = "day7.txt";
		Path input_path = Path.of(input_file);
		try {
			part1(input_path);
			System.out.println();
			part2(input_path);
		} catch (IOException e) {
			System.out.println("Error unable to read input file '" + input_file + "'");
		}
	}

	private static int[] loadData(Path input) throws IOException {
		return Stream.of(Files.lines(input).findFirst().map(line -> line.split(",")).orElseThrow())
				.mapToInt(Integer::parseInt).sorted().toArray();
	}

	// distance: 333, fuel: 328262
	private static void part1(Path input) throws IOException {
		final int[] positions = loadData(input);

		// Get the median value
		final int median = positions[positions.length / 2];

		int fuel = IntStream.of(positions).map(pos -> Math.abs(pos - median)).sum();
		System.out.println("distance: " + median + ", fuel: " + fuel);
	}

	// distance: 464, min fuel: 90040997
	private static void part2(Path input) throws IOException {
		int[] positions = loadData(input);

		// Old school loop to get min, max, sum and mean of positions
		int sum = 0;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < positions.length; i++) {
			if (positions[i] < min) {
				min = positions[i];
			}
			if (positions[i] > max) {
				max = positions[i];
			}
			sum += positions[i];
		}

		// Use the mean distance as an approximate starting point
		int mean = (int) Math.floor(sum / (double) positions.length);
		System.out.println("mean: " + mean + ", min: " + min + ", max: " + max);

		/*-
		 * Movement cost by distance
		 * 1 = 1  = (1 * (1 + 1)) / 2
		 * 2 = 3  = (2 * (2 + 1)) / 2
		 * 3 = 6  = (3 * (3 + 1)) / 2
		 * 4 = 10 = (4 * (4 + 1)) / 2
		 * 5 = 15
		 * 6 = 21
		 * 7 = 28
		 * 8 = 36
		 * 9 = 45
		 *10 = 55
		 *11 = 66
		 */

		int min_fuel = Integer.MAX_VALUE;
		int distance = -1;
		// Loop until the fuel stops decreasing
		for (int x = Math.max(0, mean - 10); x < Math.min(max, mean + 10); x++) {
			int fuel = 0;
			for (int i = 0; i < positions.length; i++) {
				int dist = Math.abs(positions[i] - x);
				fuel += (dist * (dist + 1)) / 2;
			}
			System.out.println("fuel at " + x + ": " + fuel);
			if (fuel < min_fuel) {
				min_fuel = fuel;
				distance = x;
			} else {
				System.out.println("Fuel is increasing, exiting loop");
				break;
			}
		}

		System.out.println("distance: " + distance + ", min fuel: " + min_fuel);
	}
}
