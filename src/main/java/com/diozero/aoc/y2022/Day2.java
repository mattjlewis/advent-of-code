package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.diozero.aoc.Day;

public class Day2 extends Day {
	private static final int ROCK = 1;
	private static final int PAPER = 2;
	private static final int SCISSORS = 3;

	private static final int LOSE = 0;
	private static final int DRAW = 3;
	private static final int WIN = 6;

	// In part 1 X=Rock, Y=Paper, Z=Scissors
	private static final Map<String, Integer> PART1_SCORES = new HashMap<>();
	static {
		PART1_SCORES.put("A X", Integer.valueOf(ROCK + DRAW));
		PART1_SCORES.put("A Y", Integer.valueOf(PAPER + WIN)); // Paper beats rock
		PART1_SCORES.put("A Z", Integer.valueOf(SCISSORS + LOSE)); // Scissors loses to rock

		PART1_SCORES.put("B X", Integer.valueOf(ROCK + LOSE)); // Rock loses to Paper
		PART1_SCORES.put("B Y", Integer.valueOf(PAPER + DRAW));
		PART1_SCORES.put("B Z", Integer.valueOf(SCISSORS + WIN)); // Scissors beats paper

		PART1_SCORES.put("C X", Integer.valueOf(ROCK + WIN)); // Rock beats scissors
		PART1_SCORES.put("C Y", Integer.valueOf(PAPER + LOSE)); // Paper loses to scissors
		PART1_SCORES.put("C Z", Integer.valueOf(SCISSORS + DRAW));
	}
	// In part 2 X means you must lose, Y draw, Z win
	private static final Map<String, Integer> PART2_SCORES = new HashMap<>();
	static {
		PART2_SCORES.put("A X", Integer.valueOf(SCISSORS + LOSE)); // Play scissors to lose to rock
		PART2_SCORES.put("A Y", Integer.valueOf(ROCK + DRAW)); // Play rock to draw
		PART2_SCORES.put("A Z", Integer.valueOf(PAPER + WIN)); // Play paper to beat rock

		PART2_SCORES.put("B X", Integer.valueOf(ROCK + LOSE)); // Play rock to lose to paper
		PART2_SCORES.put("B Y", Integer.valueOf(PAPER + DRAW)); // Play paper to draw
		PART2_SCORES.put("B Z", Integer.valueOf(SCISSORS + WIN)); // Play scissors to beat paper

		PART2_SCORES.put("C X", Integer.valueOf(PAPER + LOSE)); // Play paper to lose to scissors
		PART2_SCORES.put("C Y", Integer.valueOf(SCISSORS + DRAW)); // Play scissors to draw
		PART2_SCORES.put("C Z", Integer.valueOf(ROCK + WIN)); // Play rock to beat scissors
	}

	public static void main(String[] args) {
		new Day2().run();
	}

	@Override
	public String name() {
		return "Rock Paper Scissors";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(Files.lines(input).mapToInt(line -> PART1_SCORES.get(line).intValue()).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(Files.lines(input).mapToInt(line -> PART2_SCORES.get(line).intValue()).sum());
	}
}
