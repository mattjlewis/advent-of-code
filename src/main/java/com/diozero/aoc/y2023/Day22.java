package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point3D;

public class Day22 extends Day {
	public static void main(String[] args) {
		new Day22().run();
	}

	@Override
	public String name() {
		return "Sand Slabs";
	}

	@Override
	public String part1(Path input) throws IOException {
		final List<Brick> bricks = load(input);
		final int[] dependencies = new int[bricks.size()];

		bricks.stream().filter(b -> b.isSupportedBy.size() == 1).forEach(
				brick -> dependencies[bricks.indexOf(brick.isSupportedBy.stream().findFirst().orElseThrow())]++);

		return Long.toString(Arrays.stream(dependencies).filter(i -> i == 0).count());
	}

	@Override
	public String part2(Path input) throws IOException {
		final List<Brick> bricks = load(input);

		int result = 0;
		for (Brick brick : bricks) {
			final Set<Brick> removed = new HashSet<>();
			removed.add(brick);
			Set<Brick> supported = brick.supports;
			while (!supported.isEmpty()) {
				supported = supported.stream()
						.filter(supportedBrick -> removed.containsAll(supportedBrick.isSupportedBy)).peek(removed::add)
						.flatMap(supportedBrick -> supportedBrick.supports.stream()).collect(Collectors.toSet());
			}
			result += removed.size() - 1;
		}

		return Integer.toString(result);
	}

	private static List<Brick> load(Path input) throws IOException {
		final List<Brick> bricks = Files.lines(input).map(Brick::parse).sorted().toList();
		final Map<Point3D, Integer> occupied = new HashMap<>();

		for (int i = 0; i < bricks.size(); i++) {
			occupied.putAll(bricks.get(i).getOccupied(i));
		}

		for (int i = 0; i < bricks.size(); i++) {
			final Brick brick = bricks.get(i);
			boolean supported = brick.start.z() == 1;
			while (!supported) {
				supported = brick.start.z() == 1;
				if (brick.start.z() > 1) {
					final List<Point3D> below = brick.pointsBelow().stream().filter(occupied::containsKey).toList();
					if (!below.isEmpty()) {
						supported = true;
						for (Point3D point : below) {
							final Brick brick_below = bricks.get(occupied.get(point).intValue());
							brick_below.supports.add(brick);
							brick.isSupportedBy.add(brick_below);
						}
					}
					if (!supported) {
						occupied.keySet().removeAll(brick.getOccupied(i).keySet());
						brick.drop();
						occupied.putAll(brick.getOccupied(i));
					}
				}
			}
		}

		return bricks;
	}

	private static class Brick implements Comparable<Brick> {
		public static Brick parse(String line) {
			final List<Point3D> brick_points = Arrays.stream(line.split("~")).map(Point3D::parse).toList();
			final Point3D start = brick_points.get(0);
			final Point3D end = brick_points.get(1);

			Point3D.Axis orientation;
			int length;
			if (start.x() != end.x()) {
				orientation = Point3D.Axis.X;
				length = 1 + end.x() - start.x();
			} else if (start.y() != end.y()) {
				orientation = Point3D.Axis.Y;
				length = 1 + end.y() - start.y();
			} else {
				orientation = Point3D.Axis.Z;
				length = 1 + end.z() - start.z();
			}
			// Fortunately start is always less than end in the input dataset
			if (length <= 0) {
				throw new IllegalArgumentException("Negative length for '" + line + "'");
			}

			return new Brick(start, length, orientation);
		}

		private Point3D start;
		private int length;
		private Point3D.Axis orientation;
		private final Set<Brick> supports;
		private final Set<Brick> isSupportedBy;

		public Brick(Point3D start, int size, Point3D.Axis direction) {
			this.start = start;
			this.length = size;
			this.orientation = direction;
			supports = new HashSet<>();
			isSupportedBy = new HashSet<>();
		}

		@Override
		public int compareTo(Brick other) {
			return Integer.compare(start.z(), other.start.z());
		}

		public Map<Point3D, Integer> getOccupied(int id) {
			final Map<Point3D, Integer> occupied = new HashMap<>();
			for (int i = 0; i < length; i++) {
				occupied.put(start.translate(orientation, i), Integer.valueOf(id));
			}
			return occupied;
		}

		public List<Point3D> pointsBelow() {
			if (orientation == Point3D.Axis.Z) {
				return List.of(start.translate(Point3D.Axis.Z, -1));
			}

			final List<Point3D> points = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				int x = start.x() + i * (orientation == Point3D.Axis.X ? 1 : 0);
				int y = start.y() + i * (orientation == Point3D.Axis.Y ? 1 : 0);
				int z = start.z() - 1;
				points.add(new Point3D(x, y, z));
			}
			return points;
		}

		public void drop() {
			start = start.translate(new Point3D(0, 0, -1));
		}
	}
}