package com.diozero.aoc.util;

import java.util.stream.Stream;

public record LongRange(long startInclusive, long endExclusive) {
	public Stream<LongRange> split(LongRange other) {
		if (!overlaps(other)) {
			return Stream.empty();
		}

		return Stream
				.of(new LongRange(startInclusive, Math.min(other.startInclusive - 1, endExclusive)),
						new LongRange(Math.max(startInclusive, other.startInclusive),
								Math.min(endExclusive, other.endExclusive)),
						new LongRange(Math.max(other.endExclusive + 1, startInclusive), endExclusive))
				.filter(r -> r.length() > 0);
	}

	public long length() {
		return endExclusive - startInclusive;
	}

	public boolean overlaps(LongRange other) {
		return startInclusive < other.endExclusive() && other.startInclusive() < endExclusive();
	}
}
