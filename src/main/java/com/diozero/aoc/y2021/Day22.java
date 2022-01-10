package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;
import com.diozero.aoc.util.Point3D;

public class Day22 extends AocBase {
	// Example: "on x=-34984..-26543,y=51648..78707,z=2562..28760"
	private static final Pattern LINE_PATTERN = Pattern
			.compile("(on|off) x=(-?\\d+)\\.\\.(-?\\d+),y=(-?\\d+)\\.\\.(-?\\d+),z=(-?\\d+)\\.\\.(-?\\d+)");

	public static void main(String[] args) {
		new Day22().run();
	}

	@Override
	public String part1(Path input) throws IOException {
		List<Cuboid> cuboids = Files.lines(input).map(Day22::parseLine).toList();
		Logger.debug("cuboids: {}", cuboids);

		final BoundedReactorCore core = new BoundedReactorCore(new Cuboid(true, -50, 50, -50, 50, -50, 50));
		cuboids.forEach(cuboid -> core.addOrRemove(cuboid));

		return Long.toString(core.size());
	}

	@Override
	public String part2(Path input) throws IOException {
		List<Cuboid> cuboids = Files.lines(input).map(Day22::parseLine).toList();
		Logger.debug("cuboids: {}", cuboids);

		final UnboundedReactorCore core = new UnboundedReactorCore();
		cuboids.forEach(core::addOrRemove);

		return Long.toString(core.size());
	}

	private static Cuboid parseLine(String line) {
		Matcher m = LINE_PATTERN.matcher(line);
		if (!m.matches()) {
			throw new IllegalArgumentException(
					"Line '" + line + "' does not match the pattern '" + LINE_PATTERN.pattern() + "'");
		}

		int index = 1;
		boolean on = m.group(index++).equals("on");
		int x1 = Integer.parseInt(m.group(index++));
		int x2 = Integer.parseInt(m.group(index++));
		int y1 = Integer.parseInt(m.group(index++));
		int y2 = Integer.parseInt(m.group(index++));
		int z1 = Integer.parseInt(m.group(index++));
		int z2 = Integer.parseInt(m.group(index++));

		return new Cuboid(on, Math.min(x1, x2), Math.max(x1, x2), Math.min(y1, y2), Math.max(y1, y2), Math.min(z1, z2),
				Math.max(z1, z2));
	}

	private static class BoundedReactorCore {
		private Cuboid bounds;
		private Set<Point3D> unitCubes = new HashSet<>();

		public BoundedReactorCore(Cuboid bounds) {
			this.bounds = bounds;
		}

		public void addOrRemove(Cuboid cuboid) {
			// Ignore if cuboid is out of bounds
			if (!bounds.intersects(cuboid)) {
				Logger.debug("No intersection, ignoring");
				return;
			}

			Logger.debug("{} cuboid {}", cuboid.on() ? "Adding" : "Removing", cuboid);
			for (int x = Math.max(bounds.x1, cuboid.x1); x <= Math.min(bounds.x2, cuboid.x2); x++) {
				for (int y = Math.max(bounds.y1, cuboid.y1); y <= Math.min(bounds.y2, cuboid.y2); y++) {
					for (int z = Math.max(bounds.z1, cuboid.z1); z <= Math.min(bounds.z2, cuboid.z2); z++) {
						if (cuboid.on()) {
							Logger.trace("Adding point {}, {}, {}", x, y, z);
							unitCubes.add(new Point3D(x, y, z));
						} else {
							Logger.trace("Removing point {}, {}, {}", x, y, z);
							unitCubes.remove(new Point3D(x, y, z));
						}
					}
				}
			}

			Logger.debug("unitCubes.size(): {}", unitCubes.size());
		}

		public long size() {
			return unitCubes.size();
		}
	}

	private static class UnboundedReactorCore {
		// List of distinct cuboids that do not intersect
		private Set<Cuboid> cuboids = new HashSet<>();

		public void addOrRemove(final Cuboid cuboid) {
			// If off, remove any cuboids that are entirely contained within this cuboid
			// Must continue as the cuboid may still intersect with other cuboids
			if (cuboid.off()) {
				cuboids.removeIf(c -> cuboid.contains(c));
			}

			// Does cuboid intersect with any existing cubes?
			if (!cuboids.stream().anyMatch(c -> c.intersects(cuboid))) {
				// No intersection - can simply add if on or ignore if off
				if (cuboid.on()) {
					cuboids.add(cuboid);
				}

				return;
			}

			// If on, discard cuboid if it is already contained within an existing cuboid
			// If true, can return since the cuboids list cannot overlap
			if (cuboid.on() && cuboids.stream().anyMatch(c -> c.contains(cuboid))) {
				return;
			}

			// Now it gets tricky - cuboid intersects with one or more existing cuboids...
			if (cuboid.on()) {
				// Split cuboid into a number of smaller cuboids depending on the intersection
				// Find the first intersecting cuboid
				// Note that there could be more than one intersecting cuboid at this point
				final Cuboid intersecting_cuboid = cuboids.stream().filter(c -> c.intersects(cuboid)).findFirst()
						.orElseThrow();
				final List<Cuboid> sub_cuboids = cuboid.remove(intersecting_cuboid);
				// Recurse to add each sub-cuboid to handle any additional intersections
				sub_cuboids.forEach(c -> addOrRemove(c));
			} else {
				// Find the first intersecting cuboid
				List<Cuboid> intersecting_cuboids = cuboids.stream().filter(c -> c.intersects(cuboid)).toList();
				// Remove this cuboid from each of the intersecting cuboids
				// Note that it is safe to do add all here as we have only removed
				intersecting_cuboids.forEach(intersecting_cuboid -> {
					cuboids.remove(intersecting_cuboid);
					cuboids.addAll(intersecting_cuboid.remove(cuboid));
				});
			}
		}

		public long size() {
			return cuboids.stream().mapToLong(Cuboid::volume).sum();
		}
	}

	private static record Cuboid(boolean on, int x1, int x2, int y1, int y2, int z1, int z2) {
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
			return x1 <= other.x1 && x2 >= other.x2 && y1 <= other.y1 && y2 >= other.y2 && z1 <= other.z1
					&& z2 >= other.z2;
		}

		public boolean intersects(Cuboid other) {
			return x1 <= other.x2 && x2 >= other.x1 && y1 <= other.y2 && y2 >= other.y1 && z1 <= other.z2
					&& z2 >= other.z1;
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
									.map(iz -> new Cuboid(on, ix.start, ix.end, iy.start, iy.end, iz.start, iz.end))))
					.toList();
		}
	}

	private static record Range(int start, int end) {
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

		public boolean equals(int i1, int i2) {
			return start == i1 && end == i2;
		}
	}

	@Override
	public void selfTest() {
		Cuboid c1 = new Cuboid(true, 10, 12, 10, 12, 10, 12);
		assert (c1.width() == 3);
		assert (c1.height() == 3);
		assert (c1.depth() == 3);
		assert (c1.volume() == 3 * 3 * 3);

		c1 = new Cuboid(true, 0, 10, 0, 10, 0, 10);
		assert (c1.width() == 11);
		assert (c1.height() == 11);
		assert (c1.depth() == 11);
		assert (c1.volume() == 11 * 11 * 11);

		Cuboid other = c1;
		boolean expected = true;
		boolean actual = c1.contains(other);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		expected = true;
		actual = other.contains(c1);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		expected = true;
		actual = c1.intersects(other);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		actual = other.intersects(c1);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		Optional<Cuboid> intersection = c1.intersection(other);
		expected = true;
		Cuboid expected_intersection = other;
		if (intersection.isPresent() && expected) {
			actual = intersection.get().equals(expected_intersection);
			System.out.format("%s - %s.intersection(%s): %s%n", actual == expected ? "Correct" : "Error", other, c1,
					intersection.get());
		} else {
			System.out.format("%s - %s.intersection(%s): empty%n", actual == expected ? "Correct" : "Error", other, c1);
		}
		assert (c1.intersects(other) == other.intersects(c1));
		assert (c1.intersection(other).equals(other.intersection(c1)));

		// Overlapping on the x axis
		other = new Cuboid(true, 10, 20, 0, 10, 0, 10);
		expected = false;
		actual = c1.contains(other);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		expected = false;
		actual = other.contains(c1);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		expected = true;
		actual = c1.intersects(other);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		actual = other.intersects(c1);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		intersection = c1.intersection(other);
		expected = true;
		if (intersection.isPresent() && expected) {
			actual = intersection.get().equals(new Cuboid(true, 10, 10, 0, 10, 0, 10));
			System.out.format("%s - %s.intersection(%s): %s%n", actual == expected ? "Correct" : "Error", other, c1,
					intersection.get());
		} else {
			System.out.format("%s - %s.intersection(%s): empty%n", actual == expected ? "Correct" : "Error", other, c1);
		}
		assert (c1.intersects(other) == other.intersects(c1));
		assert (c1.intersection(other).equals(other.intersection(c1)));

		// Next to c1 on the x axis
		other = new Cuboid(true, 11, 20, 0, 10, 0, 10);
		expected = false;
		actual = c1.contains(other);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		expected = false;
		actual = other.contains(c1);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		expected = false;
		actual = c1.intersects(other);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		actual = other.intersects(c1);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		intersection = c1.intersection(other);
		expected = false;
		if (intersection.isPresent() && expected) {
			actual = intersection.get().equals(other);
			System.out.format("%s - %s.intersection(%s): %s%n", actual == expected ? "Correct" : "Error", other, c1,
					intersection.get());
		} else {
			System.out.format("%s - %s.intersection(%s): empty%n", actual == expected ? "Correct" : "Error", other, c1);
		}
		assert (c1.intersects(other) == other.intersects(c1));
		assert (c1.intersection(other).equals(other.intersection(c1)));

		other = new Cuboid(true, 2, 4, 2, 4, 2, 4);
		expected = true;
		actual = c1.contains(other);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		expected = false;
		actual = other.contains(c1);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		expected = true;
		actual = c1.intersects(other);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		actual = other.intersects(c1);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		intersection = c1.intersection(other);
		expected = true;
		if (intersection.isPresent() && expected) {
			actual = intersection.get().equals(other);
			System.out.format("%s - %s.intersection(%s): %s%n", actual == expected ? "Correct" : "Error", other, c1,
					intersection.get());
		} else {
			System.out.format("%s - %s.intersection(%s): empty%n", actual == expected ? "Correct" : "Error", other, c1);
		}
		assert (c1.intersects(other) == other.intersects(c1));
		assert (c1.intersection(other).equals(other.intersection(c1)));

		other = new Cuboid(true, 0, 4, 0, 4, 0, 4);
		expected = true;
		actual = c1.contains(other);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		expected = false;
		actual = other.contains(c1);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		expected = true;
		actual = c1.intersects(other);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		actual = other.intersects(c1);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		intersection = c1.intersection(other);
		expected = true;
		expected_intersection = other;
		if (intersection.isPresent() && expected) {
			actual = intersection.get().equals(expected_intersection);
			System.out.format("%s - %s.intersection(%s): %s%n", actual == expected ? "Correct" : "Error", other, c1,
					intersection.get());
		} else {
			System.out.format("%s - %s.intersection(%s): empty%n", actual == expected ? "Correct" : "Error", other, c1);
		}
		assert (c1.intersects(other) == other.intersects(c1));
		assert (c1.intersection(other).equals(other.intersection(c1)));

		other = new Cuboid(true, -1, 4, 0, 4, 0, 4);
		expected = false;
		actual = c1.contains(other);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		expected = false;
		actual = other.contains(c1);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		expected = true;
		actual = c1.intersects(other);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		actual = other.intersects(c1);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		intersection = c1.intersection(other);
		expected = true;
		expected_intersection = new Cuboid(true, 0, 4, 0, 4, 0, 4);
		if (intersection.isPresent() && expected) {
			actual = intersection.get().equals(expected_intersection);
			System.out.format("%s - %s.intersection(%s): %s%n", actual == expected ? "Correct" : "Error", other, c1,
					intersection.get());
		} else {
			System.out.format("%s - %s.intersection(%s): empty%n", actual == expected ? "Correct" : "Error", other, c1);
		}
		assert (c1.intersects(other) == other.intersects(c1));
		assert (c1.intersection(other).equals(other.intersection(c1)));

		other = new Cuboid(true, 20, 30, 20, 30, 20, 30);
		expected = false;
		actual = c1.contains(other);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		expected = false;
		actual = other.contains(c1);
		System.out.format("%s - %s.contains(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		expected = false;
		actual = c1.intersects(other);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", c1, other, actual);
		actual = other.intersects(c1);
		System.out.format("%s - %s.intersects(%s): %b%n", actual == expected ? "Correct" : "Error", other, c1, actual);
		intersection = c1.intersection(other);
		expected = false;
		expected_intersection = new Cuboid(true, 0, 4, 0, 4, 0, 4);
		if (intersection.isPresent() && expected) {
			actual = intersection.get().equals(expected_intersection);
			System.out.format("%s - %s.intersection(%s): %s%n", actual == expected ? "Correct" : "Error", other, c1,
					intersection.get());
		} else {
			System.out.format("%s - %s.intersection(%s): empty%n", actual == expected ? "Correct" : "Error", other, c1);
		}
		assert (c1.intersects(other) == other.intersects(c1));
		assert (c1.intersection(other).equals(other.intersection(c1)));

		c1 = new Cuboid(true, 0, 2, 0, 2, 0, 2);
		System.out.println(c1.volume());

		other = new Cuboid(true, 1, 1, 1, 1, -1, 0);
		System.out.println(other.volume());
		System.out.format("%s.intersects(%s): %b, intersection: %s%n", other, c1, c1.intersects(other),
				c1.intersection(other));

		other = new Cuboid(true, 1, 1, 1, 1, -1, 1);
		System.out.println(other.volume());
		System.out.format("%s.intersects(%s): %b, intersection: %s%n", other, c1, c1.intersects(other),
				c1.intersection(other));
		List<Cuboid> sub_cuboids = other.remove(c1);
		System.out.println("sub_cuboids: " + sub_cuboids);
		assert (sub_cuboids.size() == 1);
		Cuboid expected_sub_cuboid = new Cuboid(true, 1, 1, 1, 1, -1, -1);
		assert (sub_cuboids.get(0).equals(expected_sub_cuboid));
		assert (expected_sub_cuboid.volume() == 1);

		other = new Cuboid(true, 1, 1, 1, 1, -2, 4);
		System.out.println(other.volume());
		System.out.format("%s.intersects(%s): %b, intersection: %s%n", other, c1, c1.intersects(other),
				c1.intersection(other));
		sub_cuboids = other.remove(c1);
		System.out.println("sub_cuboids: " + sub_cuboids);
		assert (sub_cuboids.size() == 2);
		expected_sub_cuboid = new Cuboid(true, 1, 1, 1, 1, -2, -1);
		assert (sub_cuboids.get(0).equals(expected_sub_cuboid));
		assert (expected_sub_cuboid.volume() == 2);
		expected_sub_cuboid = new Cuboid(true, 1, 1, 1, 1, 3, 4);
		assert (sub_cuboids.get(1).equals(expected_sub_cuboid));
		assert (expected_sub_cuboid.volume() == 2);
	}
}
