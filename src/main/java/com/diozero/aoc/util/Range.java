package com.diozero.aoc.util;

import java.util.stream.Stream;

/**
 * Integer range where start <= n <= end. The numbers start and end are both
 * inclusive.
 */
public record Range(int start, int end) {
	public static Range of(int start, int end) {
		return new Range(Math.min(start, end), Math.max(start, end));
	}

	// Return all different ranges that can be built from these two ranges
	public Stream<Range> split(int otherStart, int otherEnd) {
		return Stream.of(new Range(start, Math.min(otherStart - 1, end)),
				new Range(Math.max(start, otherStart), Math.min(end, otherEnd)),
				new Range(Math.max(otherEnd + 1, start), end)).filter(r -> r.size() > 0);
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
