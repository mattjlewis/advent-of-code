package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Gatherers;

import com.diozero.aoc.Day;

public class Day1 extends Day {
	private static final DialPosition INITIAL_POSITION = new DialPosition(50, 0);

	public static void main(String[] args) {
		new Day1().run();
	}

	@Override
	public String name() {
		return "Secret Entrance";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(Files.lines(input).map(Rotation::create)
				.gather(Gatherers.scan(() -> INITIAL_POSITION, DialPosition::apply)).filter(DialPosition::atZero)
				.count());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(Files.lines(input).map(Rotation::create)
				.gather(Gatherers.scan(() -> INITIAL_POSITION, DialPosition::apply)).mapToInt(DialPosition::clicks)
				.sum());
	}

	public record Rotation(boolean left, int amount) {
		public static Rotation create(String rotation) {
			return new Rotation(rotation.charAt(0) == 'L', Integer.parseInt(rotation.substring(1)));
		}

		@Override
		public String toString() {
			return (left ? "L" : "R") + amount;
		}
	}

	public static record DialPosition(int position, int clicks) {
		public DialPosition apply(Rotation rotation) {
			int next_pos = position + (rotation.left ? -rotation.amount : rotation.amount);
			int num_clicks = (position > 0 && next_pos <= 0) ? 1 : 0;
			num_clicks += Math.abs(next_pos / 100);
			next_pos %= 100;
			if (next_pos < 0) {
				next_pos += 100;
			}

			return new DialPosition(next_pos, num_clicks);
		}

		public boolean atZero() {
			return position == 0;
		}
	}
}
