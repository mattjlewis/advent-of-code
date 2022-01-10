package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;

public class Day6 extends AocBase {
	public static void main(String[] args) {
		new Day6().run();
	}

	@Override
	public String part1(Path input) throws IOException {
		// Can't use Stream.toList() as that returns an immutable list
		final List<AtomicInteger> ages = Arrays.stream(Files.lines(input).findFirst().orElseThrow().split(","))
				.map(line -> new AtomicInteger(Integer.parseInt(line))).collect(Collectors.toList());

		final int days = 80;
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
			Logger.debug("Day: {}", day);
			// System.out.format("After %3d day(s): %s%n", day, ages);
		}

		return Integer.toString(ages.size());
	}

	@Override
	public String part2(Path input) throws IOException {
		final long[] count_at_age = new long[9];
		Arrays.stream(Files.lines(input).findFirst().orElseThrow().split(",")).mapToInt(Integer::parseInt)
				.forEach(age -> count_at_age[age]++);

		final int days = 256;
		for (int day = 1; day <= days; day++) {
			final long prev_count_at_age_0 = count_at_age[0];
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

		return Long.toString(total);
	}
}
