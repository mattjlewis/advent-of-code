package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point3D;

public class Day18 extends Day {
	final static Collection<Point3D> CUBE_NEIGHBOURS = Set.of(new Point3D(-1, 0, 0), new Point3D(1, 0, 0),
			new Point3D(0, -1, 0), new Point3D(0, 1, 0), new Point3D(0, 0, -1), new Point3D(0, 0, 1));

	public static void main(String[] args) {
		new Day18().run();
	}

	@Override
	public String name() {
		return "Boiling Boulders";
	}

	@Override
	public String part1(Path input) throws IOException {
		final Collection<Point3D> cubes = Files.lines(input).map(Point3D::parse).toList();
		return Integer.toString(cubes.size() * 6 - (int) cubes.stream()
				.flatMap(c -> CUBE_NEIGHBOURS.stream().map(c::translate)).filter(cubes::contains).count());
	}

	@Override
	public String part2(Path input) throws IOException {
		final Collection<Point3D> cubes = Files.lines(input).map(Point3D::parse).toList();

		final Point3D largest = cubes.stream().max(Comparator.comparingInt(Point3D::manhattanDistance)).orElseThrow();
		final Point3D start = CUBE_NEIGHBOURS.stream().map(largest::translate).filter(c -> !cubes.contains(c))
				.findFirst().orElseThrow();

		final Set<Point3D> air = new HashSet<>();

		final Deque<Point3D> queue = new LinkedList<>();
		queue.add(start);

		while (!queue.isEmpty()) {
			final Point3D current = queue.poll();
			air.add(current);

			CUBE_NEIGHBOURS.stream().map(current::translate)
					.filter(n -> !air.contains(n) && !cubes.contains(n) && !queue.contains(n)
							&& cubes.stream().mapToInt(c -> c.manhattanDistance(n)).min().orElseThrow() <= 2)
					.forEach(queue::add);
		}

		return Integer.toString((int) air.stream().flatMap(c -> CUBE_NEIGHBOURS.stream().map(c::translate))
				.filter(cubes::contains).count());
	}
}
