import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day6 {
	public static void main(String[] args) {
		// String input_file = "day6sample.txt";
		String input_file = "day6.txt";
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
		List<AtomicInteger> ages = Stream.of(Files.lines(input).findFirst().orElseThrow().split(","))
				.map(line -> new AtomicInteger(Integer.parseInt(line))).collect(Collectors.toList());

		int days = 80;
		for (int day = 1; day <= days; day++) {
			int num_to_add = 0;
			for (int i = 0; i < ages.size(); i++) {
				if (ages.get(i).get() == 0) {
					ages.get(i).set(6);
					num_to_add++;
				} else {
					ages.get(i).decrementAndGet();
				}
			}
			for (int i = 0; i < num_to_add; i++) {
				ages.add(new AtomicInteger(8));
			}
			System.out.println("Day: " + day);
			// System.out.format("After %3d day(s): %s%n", day, ages);
		}

		System.out.println(ages.size());
	}

	private static void part2(Path input) throws IOException {
		final long[] count_at_age = new long[9];
		Stream.of(Files.lines(input).findFirst().orElseThrow().split(",")).mapToInt(Integer::parseInt)
				.forEach(age -> count_at_age[age]++);

		int days = 256;
		for (int day = 1; day <= days; day++) {
			long prev_count_at_age_0 = count_at_age[0];
			count_at_age[0] = count_at_age[1];
			count_at_age[1] = count_at_age[2];
			count_at_age[2] = count_at_age[3];
			count_at_age[3] = count_at_age[4];
			count_at_age[4] = count_at_age[5];
			count_at_age[5] = count_at_age[6];
			count_at_age[6] = count_at_age[7] + prev_count_at_age_0;
			count_at_age[7] = count_at_age[8];
			count_at_age[8] = prev_count_at_age_0;
		}

		long total = 0;
		for (int age = 0; age < count_at_age.length; age++) {
			total += count_at_age[age];
		}
		System.out.println(total);
	}
}
