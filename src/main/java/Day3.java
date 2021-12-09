import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

public class Day3 {
	private static int numLines;
	private static int[] bits;
	private static int gamma;
	private static int epsilon;

	public static void main(String[] args) {
		String input_file = "day3.txt";
		Path input_path = Path.of(input_file);
		try {
			part1(input_path);
			for (int i = 0; i < bits.length; i++) {
				System.out.format("bits[%d]: %d (%d)%n", i, bits[i], (bits[i] > numLines / 2) ? 1 : 0);
				gamma |= (bits[i] > numLines / 2) ? (1 << i) : 0;
			}
			epsilon = ~gamma & ((1 << bits.length) - 1);
			System.out.format("numLines: %d, gamma: %d, epsilon: %d, g*s=%d%n", numLines, gamma, epsilon,
					gamma * epsilon);

			part2(input_path);
		} catch (IOException e) {
			System.out.println("Error unable to read input file '" + input_file + "'");
		}
	}

	private static void part1(Path input) throws IOException {
		bits = Files.lines(input).findFirst().map(line -> new int[line.length()]).orElseThrow();
		Files.lines(input).mapToInt(line -> Integer.parseInt(line, 2)).forEach(value -> {
			for (int i = 0; i < bits.length; i++) {
				bits[i] += ((value & (1 << i)) != 0) ? 1 : 0;
			}
			numLines++;
		});
	}

	private static void part2(Path input) throws IOException {
		int[] init_values = Files.lines(input).mapToInt(line -> Integer.parseInt(line, 2)).toArray();

		int[] values = init_values;
		for (int bit = bits.length - 1; bit >= 0 && values.length > 1; bit--) {
			// Count the number of 1s at position i
			int count = 0;
			for (int x = 0; x < values.length; x++) {
				if ((values[x] & (1 << bit)) != 0) {
					count++;
				}
			}
			final boolean value = count >= (values.length / 2.0);

			// Filter values to only those with bit[i] equal to value
			final int b = bit;
			values = IntStream.of(values).filter(n -> ((n & (1 << b)) != 0) == value).toArray();
		}
		int og_rating = values[0];

		values = init_values;
		for (int bit = bits.length - 1; bit >= 0 && values.length > 1; bit--) {
			// Count the number of 0s at position i
			int count = 0;
			for (int x = 0; x < values.length; x++) {
				if ((values[x] & (1 << bit)) != 0) {
					count++;
				}
			}
			final boolean value = count < (values.length / 2.0);
			System.out.format("length: %d, count: %d, value: %b%n", values.length, count, value);

			// Filter values to only those with bit[i] equal to value
			final int b = bit;
			values = IntStream.of(values).filter(n -> ((n & (1 << b)) != 0) == value).toArray();
		}
		int co2s_rating = values[0];

		System.out.format("oxygen generator rating: %d, CO2 scrubber rating: %d, product: %d%n", og_rating, co2s_rating,
				og_rating * co2s_rating);
	}
}
