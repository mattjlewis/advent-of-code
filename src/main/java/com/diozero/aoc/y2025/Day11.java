package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;

public class Day11 extends Day {
	public static void main(String[] args) {
		new Day11().run();
	}

	@Override
	public String name() {
		return "Reactor";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(countPaths(parseDevices(input), new HashMap<>(), "you", "out"));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Map<String, Collection<String>> devices = parseDevices(input);

		final Map<String, Integer> cache = new HashMap<>();
		long path2 = countPaths(devices, cache, "fft", "dac");
		long path1, path3;
		if (path2 > 0) {
			path1 = countPaths(devices, cache, "svr", "fft");
			path3 = countPaths(devices, cache, "dac", "out");
		} else {
			path1 = countPaths(devices, cache, "svr", "dac");
			path2 = countPaths(devices, cache, "dac", "fft");
			path3 = countPaths(devices, cache, "fft", "out");
		}

		return Long.toString(path1 * path2 * path3);
	}

	private static Map<String, Collection<String>> parseDevices(Path input) throws IOException {
		return Files.lines(input).map(line -> line.split("[:\s]+"))
				.collect(Collectors.toMap(parts -> parts[0], parts -> Arrays.stream(parts).skip(1).toList()));
	}

	private static int countPaths(Map<String, Collection<String>> devices, Map<String, Integer> cache, String start,
			String end) {
		if (start.equals(end)) {
			return 1;
		}

		final String cache_key = start + end;
		if (cache.containsKey(cache_key)) {
			return cache.get(cache_key).intValue();
		}

		final int paths = devices.getOrDefault(start, Collections.emptyList()).stream()
				.mapToInt(d -> countPaths(devices, cache, d, end)).sum();
		cache.put(cache_key, Integer.valueOf(paths));

		return paths;
	}
}
