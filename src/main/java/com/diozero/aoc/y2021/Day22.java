package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Cuboid;
import com.diozero.aoc.geometry.Point3D;

public class Day22 extends Day {
	// Example: "on x=-34984..-26543,y=51648..78707,z=2562..28760"
	private static final Pattern LINE_PATTERN = Pattern
			.compile("(on|off) x=(-?\\d+)\\.\\.(-?\\d+),y=(-?\\d+)\\.\\.(-?\\d+),z=(-?\\d+)\\.\\.(-?\\d+)");

	public static void main(String[] args) {
		new Day22().run();
	}

	@Override
	public String name() {
		return "Reactor Reboot";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Cuboid> cuboids = Files.lines(input).map(Day22::parseLine).toList();
		Logger.debug("cuboids: {}", cuboids);

		final BoundedReactorCore core = new BoundedReactorCore(new Cuboid(true, -50, 50, -50, 50, -50, 50));
		cuboids.forEach(cuboid -> core.addOrRemove(cuboid));

		return Long.toString(core.totalVolume());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Cuboid> cuboids = Files.lines(input).map(Day22::parseLine).toList();
		Logger.debug("cuboids: {}", cuboids);

		final UnboundedReactorCore core = new UnboundedReactorCore();
		cuboids.forEach(core::addOrRemove);

		return Long.toString(core.totalVolume());
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
			for (int x = Math.max(bounds.x1(), cuboid.x1()); x <= Math.min(bounds.x2(), cuboid.x2()); x++) {
				for (int y = Math.max(bounds.y1(), cuboid.y1()); y <= Math.min(bounds.y2(), cuboid.y2()); y++) {
					for (int z = Math.max(bounds.z1(), cuboid.z1()); z <= Math.min(bounds.z2(), cuboid.z2()); z++) {
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

		public long totalVolume() {
			return unitCubes.size();
		}
	}

	private static class UnboundedReactorCore {
		// List of distinct cuboids that do not intersect
		private final Set<Cuboid> cuboids = new HashSet<>();

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
				// Note that it is safe to do addAll() here as we have only removed
				intersecting_cuboids.forEach(intersecting_cuboid -> {
					cuboids.remove(intersecting_cuboid);
					// cuboids.addAll(intersecting_cuboid.remove(cuboid));
					intersecting_cuboid.extract(cuboid).forEach(cuboids::add);
				});
			}
		}

		public long totalVolume() {
			return cuboids.stream().mapToLong(Cuboid::volume).sum();
		}
	}
}
