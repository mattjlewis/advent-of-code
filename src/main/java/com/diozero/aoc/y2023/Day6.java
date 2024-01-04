package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

import com.diozero.aoc.Day;

public class Day6 extends Day {
	private static final Pattern NUMBERS_PATTERN = Pattern.compile("(\\d+)");

	public static void main(String[] args) {
		new Day6().run();
	}

	@Override
	public String name() {
		return "Wait For It";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final int[][] data = Files.lines(input).map(
				line -> NUMBERS_PATTERN.matcher(line).results().mapToInt(mr -> Integer.parseInt(mr.group(1))).toArray())
				.toArray(int[][]::new);
		final List<Race> races = new ArrayList<>();
		for (int i = 0; i < data[0].length; i++) {
			races.add(new Race(data[0][i], data[1][i]));
		}

		return Long.toString(
				races.stream().mapToLong(race -> race.bruteForceWinOptions().count()).reduce(1, (a, b) -> a * b));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final long[] data = Files.lines(input).map(
				line -> NUMBERS_PATTERN.matcher(line.replace(" ", "")).results().findFirst().orElseThrow().group(1))
				.mapToLong(Long::parseLong).toArray();

		return Long.toString(new Race(data[0], data[1]).calculateWinOptions());
	}

	private static record Race(long raceTime, long distance) {
		public LongStream bruteForceWinOptions() {
			return LongStream.range(0, raceTime).filter(this::isWin);
		}

		public boolean isWin(long pressedTime) {
			return (raceTime - pressedTime) * pressedTime > distance;
		}

		public long calculateWinOptions() {
			/*-
			 * pressedTime: B
			 * raceTime: T
			 * travelled distance = D
			 *
			 * distance = (raceTime - pressedTime) * pressedTime
			 * D = (T - B) * B 
			 * D = T*B - B^2
			 * B^2 - T*B + D = 0
			 * 
			 * Solve where:
			 * pressedTime^2 - pressedTime * raceTime + distance = 0
			 * B^2 - T*B + D = 0 
			 * 
			 * B1 = (T + SQRT(T*T - 4 * D))/2
			 * B2 = (T - SQRT(T*T - 4 * D))/2
			 */
			double sqrt = Math.sqrt(raceTime * raceTime - 4 * distance);
			long b1 = (long) Math.floor((raceTime + sqrt) / 2);
			long b2 = (long) Math.ceil((raceTime - sqrt) / 2);

			return b1 - b2 + 1;
		}
	}
}
