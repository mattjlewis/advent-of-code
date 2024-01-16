package com.diozero.aoc.util;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class SetUtil {
	private SetUtil() {
	}

	public static <E> Set<E> difference(final Set<E> a, final Set<E> b) {
		final Set<E> diff = new HashSet<>(a);
		diff.removeAll(b);
		return diff;
	}

	public static <E> Set<E> distinct(final Set<E> a, final Set<E> b) {
		return a.stream().filter(e -> !b.contains(e)).collect(Collectors.toUnmodifiableSet());
	}

	public static <E> Set<E> union(final Set<E> a, final Set<E> b) {
		final Set<E> union = new HashSet<>(a);
		union.addAll(b);
		return union;
	}

	public static <E> Set<E> intersection(final Set<E> a, final Set<E> b) {
		final Set<E> intersection = new HashSet<>(a);
		intersection.retainAll(b);
		return intersection;
	}

	public static <E> long intersectionCount(final Set<E> a, final Set<E> b) {
		return a.stream().filter(b::contains).count();
	}

	public static <E> boolean intersects(final Set<E> a, final Set<E> b) {
		return a.stream().anyMatch(b::contains);
	}

	public static <E> void addOrRemove(final Set<E> set, final E e) {
		if (set.contains(e)) {
			set.remove(e);
		} else {
			set.add(e);
		}
	}
}
