import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Count: 1359
public class Day1 {
	// Solution 1
	private static Integer lastDepth = null;

	private static int count = 0;

	public static void main(String[] args) {
		String input_file = "day1.txt";
		Path input_path = Path.of(input_file);
		try {
			part1(input_path);
			part2(input_path);
		} catch (IOException e) {
			System.out.println("Error unable to read input file '" + input_file + "'");
		}
	}

	private static void part1(Path input) throws IOException {
		count = 0;
		Files.lines(input).mapToInt(Integer::valueOf).forEach(Day1::update);

		System.out.println("Part 1, solution 1: " + count);
	}

	private static void update(int depth) {
		if (lastDepth != null && depth > lastDepth.intValue()) {
			count++;
		}

		lastDepth = Integer.valueOf(depth);
	}

	private static void part2(Path input) throws IOException {
		int[] numbers = Files.lines(input).mapToInt(Integer::valueOf).toArray();
		int part1 = 0;
		int part2 = 0;
		for (int i = 1; i < numbers.length; i++) {
			if (numbers[i] > numbers[i - 1]) {
				part1++;
			}
			// (t[0] + t[1] + t[2]) < (t[1] + t[2] + t[3]) = t[0] < t[3]
			if (i > 2 && numbers[i] > numbers[i - 3]) {
				part2++;
			}
		}
		System.out.println("Part 1, solution 2: " + part1);
		System.out.println("Part 2: " + part2);
	}
}
