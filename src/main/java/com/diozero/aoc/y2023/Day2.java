package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;

public class Day2 extends Day {
	private static enum Colour {
		RED, GREEN, BLUE;
	}

	private static final EnumMap<Colour, Integer> BAG;
	static {
		BAG = new EnumMap<>(Colour.class);
		BAG.put(Colour.RED, Integer.valueOf(12));
		BAG.put(Colour.GREEN, Integer.valueOf(13));
		BAG.put(Colour.BLUE, Integer.valueOf(14));
	}

	public static void main(String[] args) {
		new Day2().run();
	}

	@Override
	public String name() {
		return "Cube Conundrum";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer
				.toString(Files.lines(input).map(Game::parse).filter(g -> g.isValid(BAG)).mapToInt(Game::id).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(Files.lines(input).map(Game::parse).map(Game::min)
				.mapToInt(m -> m.values().stream().mapToInt(Integer::intValue).reduce(1, (a, b) -> a * b)).sum());
	}

	private static record Game(int id, List<EnumMap<Colour, Integer>> rounds) {

		// Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
		// Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
		private static final Pattern GAME_PATTERN = Pattern.compile("^Game (\\d+): (.*)$");
		private static Pattern COLOUR_CHOICE_PATTERN = Pattern.compile("(\\d+) (red|green|blue)");

		public static Game parse(String line) {
			final Matcher m = GAME_PATTERN.matcher(line);
			if (!m.matches()) {
				throw new IllegalArgumentException("Invalid line '" + line + "'");
			}

			return new Game(Integer.parseInt(m.group(1)),
					Arrays.stream(m.group(2).split("; ")).map(Game::parseRound).toList());
		}

		private static EnumMap<Colour, Integer> parseRound(String round) {
			return COLOUR_CHOICE_PATTERN.matcher(round).results().collect(Collectors.toMap(
					mr -> Colour.valueOf(mr.group(2).toUpperCase()), mr -> Integer.valueOf(mr.group(1)), (i1, i2) -> {
						throw new IllegalArgumentException("Duplicate colour entry with values " + i1 + " and " + i2);
					}, () -> new EnumMap<>(Colour.class)));
		}

		public boolean isValid(EnumMap<Colour, Integer> bag) {
			return bag.entrySet().stream().allMatch(entry -> rounds.stream().allMatch(round -> round
					.getOrDefault(entry.getKey(), Integer.valueOf(0)).intValue() <= entry.getValue().intValue()));
		}

		public EnumMap<Colour, Integer> min() {
			return Arrays.stream(Colour.values())
					.collect(Collectors.toMap(Function.identity(),
							colour -> Integer.valueOf(rounds.stream()
									.mapToInt(round -> round.getOrDefault(colour, Integer.valueOf(0)).intValue()).max()
									.orElse(0)),
							(i1, i2) -> {
								throw new IllegalArgumentException(
										"Duplicate colour entry with values " + i1 + " and " + i2);
							}, () -> new EnumMap<>(Colour.class)));
		}
	}
}
