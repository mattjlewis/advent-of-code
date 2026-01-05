package com.diozero.aoc.util;

import java.util.List;
import java.util.Optional;

public record LongRange(long start, long end, boolean endInclusive) {
	public static LongRange parse(String s) {
		// Default to end exclusive
		return parse(s, false);
	}

	public static LongRange parse(String s, boolean endInclusive) {
		final String[] parts = s.split("-");
		return new LongRange(Long.parseLong(parts[0]), Long.parseLong(parts[1]), endInclusive);
	}

	public long length() {
		return end - start + (endInclusive ? 1 : 0);
	}

	public boolean overlaps(LongRange other) {
		if (endInclusive) {
			return start <= other.end() && other.start() <= end();
		}
		return start < other.end() && other.start() < end();
	}

	public boolean touches(final LongRange other) {
		// 27..29, 4..26
		// -2..3, 4..26
		// 1..5, 7..10
		/*-
		if (start <= other.start) {
			return other.start <= (end + 1);
		}
		return start <= (other.end + 1);
		*/
		return start <= (other.end + 1) && (end + 1) >= other.start;
	}

	public static void merge(final List<LongRange> ranges) {
		// Avoiding ConcurrentModificationExeption ...
		int i = 0;
		a: while (i < ranges.size() - 1) {
			final LongRange range = ranges.get(i);
			for (int j = i + 1; j < ranges.size(); j++) {
				final Optional<LongRange> merged = range.merge(ranges.get(j));
				if (merged.isPresent()) {
					ranges.set(i, merged.get());
					ranges.remove(j);
					continue a;
				}
			}
			i++;
		}
	}

	public Optional<LongRange> merge(final LongRange other) {
		// XXX There cannot be LongRanges with exact same start and end values
		// if (this.equals(other) || !touches(other)) {
		if (!touches(other)) {
			return Optional.empty();
		}
		return Optional.of(new LongRange(Math.min(start, other.start), Math.max(end, other.end), endInclusive));
	}

	public boolean contains(long l) {
		return start <= l && (endInclusive ? l <= end : l < end);
	}
}
