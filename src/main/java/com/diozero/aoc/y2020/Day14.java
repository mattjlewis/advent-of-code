package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

import com.diozero.aoc.AocBase;

public class Day14 extends AocBase {
	private static final Pattern MASK_PATTERN = Pattern.compile("mask = (.*)");
	private static final Pattern MEM_PATTERN = Pattern.compile("mem\\[(\\w+)\\] = (\\w+)");
	private static final int ADDRESS_LENGTH = 36;
	// 0xFFFFFFFFFL
	private static final long MAX_MEM_ADDR = ((long) Math.pow(2, ADDRESS_LENGTH)) - 1;

	public static void main(String[] args) {
		new Day14().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		final Map<Integer, Long> memory = new HashMap<>();

		long and_mask = MAX_MEM_ADDR;
		long or_mask = 0;
		for (String line : Files.readAllLines(input)) {
			if (line.startsWith("mask")) {
				Matcher m = MASK_PATTERN.matcher(line);
				if (!m.matches()) {
					throw new IllegalArgumentException("Invalid mask line '" + line + "'");
				}
				String mask = m.group(1);
				and_mask = Long.parseLong(mask.replace('X', '1'), 2);
				or_mask = Long.parseLong(mask.replace('X', '0'), 2);
			} else {
				Matcher m = MEM_PATTERN.matcher(line);
				if (!m.matches()) {
					throw new IllegalArgumentException("Invalid memory line '" + line + "'");
				}
				memory.put(Integer.valueOf(m.group(1)),
						Long.valueOf((Long.parseLong(m.group(2)) & and_mask) | or_mask));
			}
		}

		return memory.values().stream().mapToLong(Long::longValue).sum();
	}

	@Override
	public long part2(Path input) throws IOException {
		final Map<Long, Integer> memory = new HashMap<>();

		String mask = "0".repeat(ADDRESS_LENGTH);
		for (String line : Files.readAllLines(input)) {
			if (line.startsWith("mask")) {
				Matcher m = MASK_PATTERN.matcher(line);
				if (!m.matches()) {
					throw new IllegalArgumentException("Invalid mask line '" + line + "'");
				}
				mask = m.group(1);
			} else {
				Matcher m = MEM_PATTERN.matcher(line);
				if (!m.matches()) {
					throw new IllegalArgumentException("Invalid memory line '" + line + "'");
				}

				Integer value = Integer.valueOf(m.group(2));

				// Write the value to all possible memory locations
				memoryLocations(mask, Long.parseLong(m.group(1))).forEach(ml -> memory.put(Long.valueOf(ml), value));
			}
		}

		return memory.values().stream().mapToLong(Integer::longValue).sum();
	}

	private static LongStream memoryLocations(String mask, long memoryLocation) {
		int x_index = mask.indexOf('X');

		if (x_index == -1) {
			return LongStream.of(memoryLocation | Long.parseLong(mask, 2));
		}

		/*
		 * If the mask bit is 0, the corresponding memory address bit is unchanged.
		 *
		 * If the mask bit is 1, the corresponding memory address bit is overwritten
		 * with 1.
		 *
		 * If the mask bit is X, the corresponding memory address bit is both 0 and 1.
		 *
		 * So always OR the memory location with the fully resolved mask and evaluate
		 * the memory location with the bit set to 0 and 1.
		 */
		String new_mask = mask.substring(0, x_index) + "0" + mask.substring(x_index + 1);
		long bit_mask = (1L << (mask.length() - x_index - 1));

		return LongStream.concat(memoryLocations(new_mask, (memoryLocation & ~bit_mask) & MAX_MEM_ADDR),
				memoryLocations(new_mask, memoryLocation | bit_mask));
	}
}
