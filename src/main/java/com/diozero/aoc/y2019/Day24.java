package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.MatrixUtil;
import com.diozero.aoc.util.PrintUtil;
import com.diozero.aoc.util.TextParser;

public class Day24 extends Day {
	private static final Integer ZERO = Integer.valueOf(0);

	private static final int WIDTH = 5;
	private static final int HEIGHT = 5;

	private static final int TOP_CELLS_BIT_MASK;
	private static final int LEFT_HAND_CELLS_BIT_MASK;
	private static final int RIGHT_HAND_CELLS_BIT_MASK;
	private static final int BOTTOM_CELLS_BIT_MASK;
	static {
		int x, y, mask;

		y = 0;
		mask = 0;
		for (x = 0; x < WIDTH; x++) {
			mask |= MatrixUtil.mask(x, y, WIDTH);
		}
		// 0x1f
		TOP_CELLS_BIT_MASK = mask;

		x = 0;
		mask = 0;
		for (y = 0; y < HEIGHT; y++) {
			mask |= MatrixUtil.mask(x, y, WIDTH);
		}
		// 0x108421
		LEFT_HAND_CELLS_BIT_MASK = mask;

		x = WIDTH - 1;
		mask = 0;
		for (y = 0; y < HEIGHT; y++) {
			mask |= MatrixUtil.mask(x, y, WIDTH);
		}
		// 0x1084210
		RIGHT_HAND_CELLS_BIT_MASK = mask;

		y = HEIGHT - 1;
		mask = 0;
		for (x = 0; x < WIDTH; x++) {
			mask |= MatrixUtil.mask(x, y, WIDTH);
		}
		// 0x1f00000
		BOTTOM_CELLS_BIT_MASK = mask;
	}

	public static void main(String[] args) {
		new Day24().run();
	}

	@Override
	public String name() {
		return "Planet of Discord";
	}

	@Override
	public String part1(Path input) throws IOException {
		final boolean[][] bugs_matrix = TextParser.loadBooleanArray(input);
		final int height = bugs_matrix.length;
		final int width = bugs_matrix[0].length;
		if (width * height > 31) {
			throw new IllegalArgumentException("Bug matrix is too big");
		}

		// Convert the boolean matrix to a bit mask
		int bugs = MatrixUtil.convertToBitMask(bugs_matrix);
		final Set<Integer> history = new HashSet<>();

		// Evolve until there is a duplicate
		while (history.add(Integer.valueOf(bugs))) {
			bugs = evolve(bugs, width, height);
		}

		return Integer.toString(bugs);
	}

	private static int evolve(int bugs, int width, int height) {
		int new_bugs = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int adjacent_bugs = 0;
				for (int dy = Math.max(0, y - 1); dy <= Math.min(height - 1, y + 1); dy++) {
					for (int dx = Math.max(0, x - 1); dx <= Math.min(width - 1, x + 1); dx++) {
						if (dy == y && dx == x || dy != y && dx != x) {
							continue;
						}

						if (MatrixUtil.isSet(bugs, dx, dy, width)) {
							adjacent_bugs++;
						}
					}
				}

				if (MatrixUtil.isSet(bugs, x, y, width)) {
					// A bug dies (becoming an empty space) unless there is exactly one bug adjacent
					// to it.
					if (adjacent_bugs == 1) {
						new_bugs |= MatrixUtil.mask(x, y, width);
					}
				} else {
					// An empty space becomes infested with a bug if exactly one or two bugs are
					// adjacent to it.
					if (adjacent_bugs == 1 || adjacent_bugs == 2) {
						new_bugs |= MatrixUtil.mask(x, y, width);
					}
				}
			}
		}

		return new_bugs;
	}

	@Override
	public String part2(Path input) throws IOException {
		final boolean[][] bugs_matrix = TextParser.loadBooleanArray(input);
		final int height = bugs_matrix.length;
		final int width = bugs_matrix[0].length;
		if (height != HEIGHT || width != WIDTH) {
			throw new IllegalArgumentException("A " + WIDTH + "x" + HEIGHT + " grid is required");
		}

		// The recursive layers
		Map<Integer, Integer> bugs = new HashMap<>();
		// Convert the boolean matrix to a bit mask
		bugs.put(ZERO, Integer.valueOf(MatrixUtil.convertToBitMask(bugs_matrix)));

		if (Logger.isDebugEnabled()) {
			Logger.debug("Initial state:");
			printBugs(bugs, width, height);
		}

		final int num_minutes = input.toString().contains("_samples") ? 10 : 200;
		for (int minutes = 1; minutes <= num_minutes; minutes++) {
			bugs = evolve(bugs, width, height);

			if (Logger.isDebugEnabled()) {
				Logger.debug("After {} minutes ({} bugs):", minutes,
						bugs.values().stream().mapToInt(Integer::bitCount).sum());
				printBugs(bugs, width, height);
			}
		}

		return Integer.toString(bugs.values().stream().mapToInt(Integer::bitCount).sum());
	}

	private static Map<Integer, Integer> evolve(Map<Integer, Integer> bugs, int width, int height) {
		final Integer bottom_layer = bugs.keySet().stream().min(Integer::compare).orElseThrow();
		final Integer top_layer = bugs.keySet().stream().max(Integer::compare).orElseThrow();

		// Ensure there are sufficient layers, i.e. the top and bottom layers always = 0
		if (bugs.get(bottom_layer).intValue() != 0) {
			bugs.put(Integer.valueOf(bottom_layer.intValue() - 1), ZERO);
		}
		if (bugs.get(top_layer).intValue() != 0) {
			bugs.put(Integer.valueOf(top_layer.intValue() + 1), ZERO);
		}

		return bugs.entrySet().stream().map(e -> Map.entry(e.getKey(),
				Integer.valueOf(evolve(e.getValue().intValue(), bugs.get(Integer.valueOf(e.getKey().intValue() - 1)),
						bugs.get(Integer.valueOf(e.getKey().intValue() + 1)), width, height))))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private static int evolve(int bugs, Integer bugsBelow, Integer bugsAbove, int width, int height) {
		final int mid_x = (width + 1) / 2 - 1;
		final int mid_y = (height + 1) / 2 - 1;

		int new_bugs = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// The central cell cannot be a bug as it recurses to the layer below
				if (x == 2 && y == 2) {
					continue;
				}

				int adjacent_bugs = 0;

				for (int dy = Math.max(0, y - 1); dy <= Math.min(height - 1, y + 1); dy++) {
					for (int dx = Math.max(0, x - 1); dx <= Math.min(width - 1, x + 1); dx++) {
						if (dy == y && dx == x || dy != y && dx != x) {
							continue;
						}

						if (MatrixUtil.isSet(bugs, dx, dy, width)) {
							adjacent_bugs++;
						}
					}
				}

				if (bugsAbove != null) {
					int bugs_above = bugsAbove.intValue();

					if (y == 0) {
						// Compare the top row of bugs against cell 8 of bugs above
						if (MatrixUtil.isSet(bugs_above, mid_x, mid_y - 1, width)) {
							adjacent_bugs++;
						}
					} else if (y == height - 1) {
						// Compare the bottom row of bugs against cell 18 of bugs above
						if (MatrixUtil.isSet(bugs_above, mid_x, mid_y + 1, width)) {
							adjacent_bugs++;
						}
					}

					if (x == 0) {
						// Compare the left row of bugs against cell 12 of bugs above
						if (MatrixUtil.isSet(bugs_above, mid_x - 1, mid_y, width)) {
							adjacent_bugs++;
						}
					} else if (x == width - 1) {
						// Compare the right row of bugs against cell 14 of bugs above
						if (MatrixUtil.isSet(bugs_above, mid_x + 1, mid_y, width)) {
							adjacent_bugs++;
						}
					}
				}

				if (bugsBelow != null) {
					int bugs_below = bugsBelow.intValue();

					if (x == mid_x && y == mid_y - 1) {
						// Compare this bug against the top row (y=0) of bugs below
						adjacent_bugs += Integer.bitCount(bugs_below & TOP_CELLS_BIT_MASK);
					} else if (x == mid_x - 1 && y == mid_y) {
						// Compare this bug against the left-hand column (x=0) of bugs below
						adjacent_bugs += Integer.bitCount(bugs_below & LEFT_HAND_CELLS_BIT_MASK);
					} else if (x == mid_x + 1 && y == mid_y) {
						// Compare this bug against the right-hand column (x=4) of bugs below
						adjacent_bugs += Integer.bitCount(bugs_below & RIGHT_HAND_CELLS_BIT_MASK);
					} else if (x == mid_x && y == mid_y + 1) {
						// Compare this bug against the bottom row (y=4) of bugs below
						adjacent_bugs += Integer.bitCount(bugs_below & BOTTOM_CELLS_BIT_MASK);
					}
				}

				if (MatrixUtil.isSet(bugs, x, y, width)) {
					// A bug dies unless there is exactly one bug adjacent to it.
					if (adjacent_bugs == 1) {
						new_bugs |= MatrixUtil.mask(x, y, width);
					}
				} else {
					// An empty space becomes infested with a bug if exactly one or two bugs are
					// adjacent to it.
					if (adjacent_bugs == 1 || adjacent_bugs == 2) {
						new_bugs |= MatrixUtil.mask(x, y, width);
					}
				}
			}
		}

		return new_bugs;
	}

	private static void printBugs(Map<Integer, Integer> bugs, int width, int height) {
		bugs.entrySet().stream().filter(e -> e.getValue().intValue() != 0)
				.sorted(Comparator.comparingInt(Map.Entry::getKey)).forEach(e -> {
					System.out.println("Depth " + e.getKey() + ":");
					PrintUtil.print(MatrixUtil.convertToMatrix(e.getValue().intValue(), width, height),
							TextParser.SET_CHAR, TextParser.UNSET_CHAR);
				});
	}
}
