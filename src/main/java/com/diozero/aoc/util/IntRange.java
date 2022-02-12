package com.diozero.aoc.util;

import java.util.stream.Stream;

/**
 * Integer range where start <= n <= end. The numbers start and end are both
 * inclusive.
 */
public record IntRange(int startInclusive, int endInclusive) {
	public static IntRange of(int startInclusive, int endInclusive) {
		return new IntRange(Math.min(startInclusive, endInclusive), Math.max(startInclusive, endInclusive));
	}

	// Return all different ranges that can be built from these two ranges
	public Stream<IntRange> split(int otherStart, int otherEnd) {
		return Stream.of(new IntRange(startInclusive, Math.min(otherStart - 1, endInclusive)),
				new IntRange(Math.max(startInclusive, otherStart), Math.min(endInclusive, otherEnd)),
				new IntRange(Math.max(otherEnd + 1, startInclusive), endInclusive)).filter(r -> r.size() > 0);
	}

	public long size() {
		return endInclusive - startInclusive + 1;
	}

	public boolean contains(int ticketValue) {
		return ticketValue >= startInclusive && ticketValue <= endInclusive;
	}

	public boolean equals(int i1, int i2) {
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
