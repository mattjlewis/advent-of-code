package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.TextParser;

public class Day13 extends Day {
	public static void main(String[] args) {
		new Day13().run();
	}

	@Override
	public String name() {
		return "Point of Incidence";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<MirrorPattern> mirrors = load(input);

		return Integer.toString(mirrors.stream().map(m -> m.findReflection(false, false)).filter(OptionalInt::isPresent)
				.mapToInt(OptionalInt::getAsInt).sum()
				+ 100 * mirrors.stream().map(m -> m.findReflection(true, false)).filter(OptionalInt::isPresent)
						.mapToInt(OptionalInt::getAsInt).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<MirrorPattern> mirrors = load(input);

		return Integer.toString(mirrors.stream().map(m -> m.findReflection(false, true)).filter(OptionalInt::isPresent)
				.mapToInt(OptionalInt::getAsInt).sum()
				+ 100 * mirrors.stream().map(m -> m.findReflection(true, true)).filter(OptionalInt::isPresent)
						.mapToInt(OptionalInt::getAsInt).sum());
	}

	private static List<MirrorPattern> load(Path input) throws IOException {
		final List<MirrorPattern> mirrors = new ArrayList<>();

		final List<String> lines = new ArrayList<>();
		for (String line : Files.readAllLines(input)) {
			if (line.isBlank()) {
				mirrors.add(MirrorPattern.parse(lines));
				lines.clear();
			} else {
				lines.add(line);
			}
		}
		mirrors.add(MirrorPattern.parse(lines));

		return mirrors;
	}

	private static record MirrorPattern(int[] rows, int[] columns) {
		public static MirrorPattern parse(List<String> lines) {
			final boolean[][] data = TextParser.loadBooleanArray(lines);

			final int[] rows = new int[data.length];
			for (int y = 0; y < data.length; y++) {
				int row = 0;
				for (int x = 0; x < data[y].length; x++) {
					if (data[y][x]) {
						row |= (1 << (data[y].length - x - 1));
					}
				}

				rows[y] = row;
			}

			final int[] cols = new int[data[0].length];
			for (int x = 0; x < data[0].length; x++) {
				int col = 0;
				for (int y = 0; y < data.length; y++) {
					if (data[y][x]) {
						col |= (1 << (data.length - y - 1));
					}
				}

				cols[x] = col;
			}

			return new MirrorPattern(rows, cols);
		}

		public OptionalInt findReflection(boolean horizontal, boolean smudge) {
			final int[] values = horizontal ? rows : columns;

			for (int i = 1; i < values.length; i++) {
				boolean found = true;
				boolean found_smudge = false;
				for (int delta = 0; found && delta < Math.min(i, values.length - i); delta++) {
					found = values[i - delta - 1] == values[i + delta];
					if (smudge && !found && !found_smudge) {
						int xor = values[i - delta - 1] ^ values[i + delta];
						// Is there a single bit difference?
						found_smudge = xor != 0 && (xor & (xor - 1)) == 0;
						if (found_smudge) {
							found = true;
						}
					}
				}
				if ((!smudge && found) || (smudge && found && found_smudge)) {
					return OptionalInt.of(i);
				}
			}

			return OptionalInt.empty();
		}
	}
}
