package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.LongRange;

public class Day5 extends Day {
	private static final boolean VALIDATE_NO_OVERLAPS = true;

	private static enum MapType {
		SEED_TO_SOIL, SOIL_TO_FERTILIZER, FERTILIZER_TO_WATER, WATER_TO_LIGHT, LIGHT_TO_TEMPERATURE,
		TEMPERATURE_TO_HUMIDITY, HUMIDITY_TO_LOCATION;

		private String label;

		private MapType() {
			label = name().toLowerCase().replace("_", "-") + " map:";
		}

		public String label() {
			return label;
		}
	}

	public static void main(String[] args) {
		new Day5().run();
	}

	@Override
	public String name() {
		return "If You Give A Seed A Fertilizer";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(Almanac.parse(input).getSeedLocations().min().orElseThrow());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(Almanac.parse(input).getLowestSeedLocation());
	}

	private static record Almanac(long[] seeds, EnumMap<MapType, List<LongRangeMap>> maps) {

		public static Almanac parse(Path input) throws IOException {
			final List<String> lines = Files.readAllLines(input);

			int line_index = 0;
			final long[] seeds = Pattern.compile("(\\d+)").matcher(lines.get(line_index++)).results()
					.mapToLong(mr -> Long.parseLong(mr.group(1))).toArray();

			// Skip a blank line
			line_index++;

			final EnumMap<MapType, List<LongRangeMap>> maps = new EnumMap<>(MapType.class);

			for (MapType map_type : MapType.values()) {
				if (!lines.get(line_index++).equals(map_type.label())) {
					throw new IllegalArgumentException(
							"Excpected '" + map_type.label() + "', got '" + lines.get(line_index - 1) + "'");
				}

				final List<LongRangeMap> map = new ArrayList<>();
				String line = lines.get(line_index++);
				do {
					map.add(LongRangeMap.parse(line));

					line = lines.get(line_index++);
				} while (!line.isBlank() && line_index < lines.size());

				if (VALIDATE_NO_OVERLAPS) {
					for (int i = 0; i < map.size() - 1; i++) {
						for (int j = i + 1; j < map.size(); j++) {
							if (map.get(i).overlaps(map.get(j))) {
								System.out.format("!!! map %d (%s) overlaps with %d (%s) !!!%n", Integer.valueOf(i),
										map.get(i), Integer.valueOf(j), map.get(j));
							}
						}
					}
				}

				maps.put(map_type, map);
			}

			if (maps.size() != MapType.values().length) {
				throw new IllegalArgumentException("Expected " + MapType.values().length + " got " + maps.size());
			}

			return new Almanac(seeds, maps);
		}

		public void mapValue(AtomicLong value, MapType type) {
			for (LongRangeMap map : maps.get(type)) {
				if (map.map(value)) {
					break;
				}
			}
		}

		public LongStream getSeedLocations() {
			final List<AtomicLong> values = Arrays.stream(seeds).mapToObj(AtomicLong::new).toList();

			Arrays.stream(MapType.values()).forEach(type -> values.forEach(v -> mapValue(v, type)));

			return values.stream().mapToLong(AtomicLong::get);
		}

		public long getLowestSeedLocation() {
			final List<LongRange> seed_ranges = new ArrayList<>();
			for (int i = 0; i < seeds.length; i += 2) {
				seed_ranges.add(new LongRange(seeds[i], seeds[i] + seeds[i + 1], false));
			}

			List<LongRange> ranges = new ArrayList<>(seed_ranges);
			for (MapType type : MapType.values()) {
				ranges = map(ranges, maps.get(type));
			}

			return ranges.stream().mapToLong(LongRange::start).min().orElseThrow();
		}

		private static List<LongRange> map(final List<LongRange> ranges, final List<LongRangeMap> maps) {
			final List<LongRange> result = new ArrayList<>();

			Collections.sort(maps);

			for (final LongRange range : ranges) {
				long current_start_inclusive = range.start();

				for (final LongRangeMap map : maps) {
					if (current_start_inclusive >= range.end()) {
						break;
					}

					if (current_start_inclusive < map.startInclusive()) {
						long new_end_exclusive = Math.min(map.startInclusive(), range.end());
						result.add(new LongRange(current_start_inclusive, new_end_exclusive, false));
						current_start_inclusive = new_end_exclusive;
					} else if (current_start_inclusive < map.endExclusive()) {
						long new_end_exclusive = Math.min(map.endExclusive(), range.end());
						result.add(new LongRange(current_start_inclusive + map.delta(), new_end_exclusive + map.delta(),
								false));
						current_start_inclusive = new_end_exclusive;
					}
				}

				if (current_start_inclusive < range.end()) {
					result.add(new LongRange(current_start_inclusive, range.end(), false));
				}
			}

			return result;
		}
	}

	private static record LongRangeMap(long startInclusive, long endExclusive, long delta)
			implements Comparable<LongRangeMap> {
		public static LongRangeMap parse(String line) {
			long[] map = Arrays.stream(line.split(" ")).mapToLong(Long::parseLong).toArray();

			return new LongRangeMap(map[1], map[1] + map[2], map[0] - map[1]);
		}

		public boolean overlaps(LongRangeMap other) {
			return startInclusive < other.endExclusive && other.startInclusive < endExclusive;
		}

		public boolean contains(long value) {
			return value >= startInclusive && value < endExclusive;
		}

		public boolean map(AtomicLong value) {
			if (!contains(value.get())) {
				return false;
			}

			value.addAndGet(delta);

			return true;
		}

		@Override
		public int compareTo(LongRangeMap other) {
			if (this.startInclusive - other.startInclusive < 0) {
				return -1;
			}

			return 1;
		}
	}
}
