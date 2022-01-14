package com.diozero.aoc.util;

import java.util.HashSet;
import java.util.Set;

public final class SetUtil {
	private SetUtil() {
		//
	}

	public static <T> Set<T> difference(Set<T> a, Set<T> b) {
		Set<T> diff = new HashSet<>(a);
		diff.removeAll(b);
		return diff;
	}

	public static <T> Set<T> union(Set<T> a, Set<T> b) {
		Set<T> union = new HashSet<>(a);
		union.addAll(b);
		return union;
	}

	public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
		Set<T> intersection = new HashSet<>(a);
		intersection.retainAll(b);
		return intersection;
	}

	public static <T> long intersectionCount(final Set<T> a, final Set<T> b) {
		return a.stream().filter(b::contains).count();
	}

	public static <T> void addOrRemove(Set<T> set, T e) {
		if (set.contains(e)) {
			set.remove(e);
		} else {
			set.add(e);
		}
	}
}
