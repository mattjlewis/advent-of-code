package com.diozero.aoc.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Integer range where start <= n <= end. The numbers start and end are both
 * inclusive.
 */
public record IntRange(int startInclusive, int endInclusive) {
	public static IntRange of(final int startInclusive, final int endInclusive) {
		return new IntRange(Math.min(startInclusive, endInclusive), Math.max(startInclusive, endInclusive));
	}

	public static IntRange parseDashSeparated(final String s) {
		final String[] parts = s.split("-");
		return new IntRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}

	public static void merge(final List<IntRange> ranges) {
		int i = 0;
		a: while (i < ranges.size() - 1) {
			final IntRange range = ranges.get(i);
			for (int j = i + 1; j < ranges.size(); j++) {
				final Optional<IntRange> merged = range.merge(ranges.get(j));
				if (merged.isPresent()) {
					ranges.set(i, merged.get());
					ranges.remove(j);
					continue a;
				}
			}
			i++;
		}
	}

	// Return all different ranges that can be built from these two ranges
	public Stream<IntRange> split(final int otherStart, final int otherEnd) {
		return Stream.of(new IntRange(startInclusive, Math.min(otherStart - 1, endInclusive)),
				new IntRange(Math.max(startInclusive, otherStart), Math.min(endInclusive, otherEnd)),
				new IntRange(Math.max(otherEnd + 1, startInclusive), endInclusive)).filter(r -> r.size() > 0);
	}

	public long size() {
		return endInclusive - startInclusive + 1;
	}

	public boolean contains(final int i) {
		return i >= startInclusive && i <= endInclusive;
	}

	public boolean contains(final IntRange other) {
		return startInclusive <= other.startInclusive && endInclusive >= other.endInclusive;
	}

	public boolean overlaps(final IntRange other) {
		return startInclusive <= other.endInclusive && endInclusive >= other.startInclusive;
	}

	public boolean touches(final IntRange other) {
		// 27..29, 4..26
		// -2..3, 4..26
		// 1..5, 7..10
		return startInclusive <= (other.endInclusive + 1) && (endInclusive + 1) >= other.startInclusive;
	}

	public Optional<IntRange> merge(final IntRange other) {
		if (!touches(other)) {
			return Optional.empty();
		}
		return Optional.of(new IntRange(Math.min(startInclusive, other.startInclusive),
				Math.max(endInclusive, other.endInclusive)));
	}

	public boolean equals(final int i1, final int i2) {
		return startInclusive == i1 && endInclusive == i2;
	}

	public int start() {
		return startInclusive;
	}

	public int end() {
		return endInclusive;
	}

	@Override
	public String toString() {
		return startInclusive + ".." + endInclusive;
	}
}
