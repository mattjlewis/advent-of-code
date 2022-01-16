package com.diozero.aoc.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record Cuboid(boolean on, int x1, int x2, int y1, int y2, int z1, int z2) {
	public long width() {
		return (x2 - x1) + 1;
	}

	public long height() {
		return (y2 - y1) + 1;
	}

	public long depth() {
		return (z2 - z1) + 1;
	}

	public long volume() {
		return width() * height() * depth();
	}

	public boolean off() {
		return !on;
	}

	public boolean contains(Cuboid other) {
		return x1 <= other.x1 && x2 >= other.x2 && y1 <= other.y1 && y2 >= other.y2 && z1 <= other.z1 && z2 >= other.z2;
	}

	public boolean intersects(Cuboid other) {
		return x1 <= other.x2 && x2 >= other.x1 && y1 <= other.y2 && y2 >= other.y1 && z1 <= other.z2 && z2 >= other.z1;
	}

	public Optional<Cuboid> intersection(Cuboid other) {
		if (!intersects(other)) {
			return Optional.empty();
		}
		return Optional.of(new Cuboid(on, Math.max(x1, other.x1), Math.min(x2, other.x2), Math.max(y1, other.y1),
				Math.min(y2, other.y2), Math.max(z1, other.z1), Math.min(z2, other.z2)));
	}

	public List<Cuboid> remove(Cuboid other) {
		Optional<Cuboid> opt = intersection(other);
		if (opt.isEmpty()) {
			return Collections.emptyList();
		}

		Cuboid intersection = opt.get();

		return Range.of(x1, x2).split(other.x1, other.x2) //
				.flatMap(ix -> Range.of(y1, y2).split(other.y1, other.y2) // loop through all y splits
						.flatMap(iy -> Range.of(z1, z2).split(other.z1, other.z2) // loop through all z splits
								// skip if this part of the given input cube
								.filter(iz -> !(ix.equals(intersection.x1, intersection.x2)
										&& iy.equals(intersection.y1, intersection.y2)
										&& iz.equals(intersection.z1, intersection.z2)))
								// otherwise add to result
								.map(iz -> new Cuboid(on, ix.start(), ix.end(), iy.start(), iy.end(), iz.start(),
										iz.end()))))
				.toList();
	}
}