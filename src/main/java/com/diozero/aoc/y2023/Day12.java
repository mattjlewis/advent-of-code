package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.StringUtil;

public class Day12 extends Day {
	public static void main(String[] args) {
		new Day12().run();
	}

	@Override
	public String name() {
		return "Hot Springs";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(Files.lines(input).map(line -> Springs.parse(line, 1))
				.mapToLong(springs -> recurse(springs, new HashMap<>())).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(Files.lines(input).map(line -> Springs.parse(line, 5))
				.mapToLong(springs -> recurse(springs, new HashMap<>())).sum());
	}

	/*-
	 * https://www.reddit.com/r/adventofcode/comments/18ge41g/comment/kd221yp/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
	 */
	private static long recurse(Springs springs, Map<Springs, Long> cache) {
		final String lava = springs.lava;
		List<Integer> groups = springs.groups;

		final Long cached_result = cache.get(springs);
		if (cached_result != null) {
			return cached_result.longValue();
		}

		if (groups.isEmpty()) {
			return lava.contains("#") ? 0 : 1;
		}

		final int current = groups.get(0).intValue();
		groups = groups.subList(1, groups.size());

		long result = 0;
		for (int i = 0; i < lava.length() - sum(groups) - groups.size() - current + 1; i++) {
			if (lava.substring(0, i).contains("#")) {
				break;
			}

			final int next = i + current;
			if (next <= lava.length() && !lava.substring(i, next).contains(".")
					&& !StringUtil.substring(lava, next, next + 1).equals("#")) {
				result += recurse(new Springs(next < lava.length() ? lava.substring(next + 1) : "", groups), cache);
			}
		}

		cache.put(springs, Long.valueOf(result));

		return result;
	}

	public static int sum(List<Integer> data) {
		return data.stream().mapToInt(Integer::intValue).sum();
	}

	private static record Springs(String lava, List<Integer> groups) {
		public static Springs parse(String line, int repeat) {
			final String[] parts = line.split(" ");

			return new Springs(StringUtil.repeat(parts[0], "?", repeat),
					Arrays.stream(StringUtil.repeat(parts[1], ",", repeat).split(",")).map(Integer::parseInt).toList());
		}

		@Override
		public String toString() {
			return lava + "; " + groups;
		}
	}
}
