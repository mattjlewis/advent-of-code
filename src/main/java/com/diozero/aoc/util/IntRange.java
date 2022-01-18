package com.diozero.aoc.util;

import java.util.stream.Stream;

/**
 * Integer range where start <= n <= end. The numbers start and end are both
 * inclusive.
 */
public record IntRange(int start, int end) {
	public static IntRange of(int start, int end) {
		return new IntRange(Math.min(start, end), Math.max(start, end));
	}

	// Return all different ranges that can be built from these two ranges
	public Stream<IntRange> split(int otherStart, int otherEnd) {
		return Stream.of(new IntRange(start, Math.min(otherStart - 1, end)),
				new IntRange(Math.max(start, otherStart), Math.min(end, otherEnd)),
				new IntRange(Math.max(otherEnd + 1, start), end)).filter(r -> r.size() > 0);
	}

	public long size() {
		return end - start + 1;
	}

	public boolean contains(int ticketValue) {
		return ticketValue >= start && ticketValue <= end;
	}

	public boolean equals(int i1, int i2) {
		return start == i1 && end == i2;
	}

	@Override
	public String toString() {
		return start + "<= n <=" + end;
	}
}
