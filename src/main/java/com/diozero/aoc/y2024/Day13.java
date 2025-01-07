package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import com.diozero.aoc.Day;

public class Day13 extends Day {
	private static final Pattern MACHINE_PATTERN = Pattern
			.compile("Button A: X\\+(\\d+), Y\\+(\\d+)\\nButton B: X\\+(\\d+), Y\\+(\\d+)\\nPrize: X=(\\d+), Y=(\\d+)");

	public static void main(String[] args) {
		new Day13().run();
	}

	@Override
	public String name() {
		return "Claw Contraption";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(MACHINE_PATTERN.matcher(Files.readString(input)).results()
				.map(mr -> Machine.create(mr, 0L)).mapToLong(Machine::fewestTokens).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(MACHINE_PATTERN.matcher(Files.readString(input)).results()
				.map(mr -> Machine.create(mr, 10_000_000_000_000L)).mapToLong(Machine::fewestTokens).sum());
	}

	private static record Machine(int aX, int aY, int bX, int bY, long prizeX, long prizeY) {
		public static Machine create(MatchResult mr, long offset) {
			int index = 1;
			return new Machine(Integer.parseInt(mr.group(index++)), Integer.parseInt(mr.group(index++)),
					Integer.parseInt(mr.group(index++)), Integer.parseInt(mr.group(index++)),
					offset + Integer.parseInt(mr.group(index++)), offset + Integer.parseInt(mr.group(index++)));
		}

		public long fewestTokens() {
			// This problem can be transformed into a system of linear equations
			// prizeX = aX * a_presses + bX * b_presses
			// prizeY = aY * a_presses + bY * b_presses

			final long b_presses = (aX * prizeY - aY * prizeX) / (aX * bY - aY * bX);
			final long a_presses = (prizeX - bX * b_presses) / aX;
			return aY * a_presses + bY * b_presses == prizeY && aX * a_presses + bX * b_presses == prizeX
					? 3 * a_presses + b_presses
					: 0;
		}
	}
}
