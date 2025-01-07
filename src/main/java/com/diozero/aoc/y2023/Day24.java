package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point3DLong;

public class Day24 extends Day {
	public static void main(String[] args) {
		new Day24().run();
	}

	@Override
	public String name() {
		return "Never Tell Me The Odds";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Hail> hailstones = Files.lines(input).map(Hail::parse).toList();
		final long test_area_start = isSample() ? 7 : 200000000000000L;
		final long test_area_end = isSample() ? 27 : 400000000000000L;

		int intersections = 0;
		for (int i = 0; i < hailstones.size() - 1; i++) {
			for (int j = i + 1; j < hailstones.size(); j++) {
				if (hailstones.get(i).intersects2d(hailstones.get(j), test_area_start, test_area_end)) {
					intersections++;
				}
			}
		}

		return Integer.toString(intersections);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Hail> hailstones = Files.lines(input).map(Hail::parse).toList();

		double[][] matrix = new double[4][4];
		double[] constants = new double[4];

		for (int i = 0; i < 4; i++) {
			final Hail hail = hailstones.get(i);
			final Hail other = hailstones.get(i + 1);
			matrix[i][0] = other.velocity.y() - hail.velocity.y();
			matrix[i][1] = hail.velocity.x() - other.velocity.x();
			matrix[i][2] = hail.position.y() - other.position.y();
			matrix[i][3] = other.position.x() - hail.position.x();
			constants[i] = hail.position.y() * hail.velocity.x() - hail.position.x() * hail.velocity.y()
					+ other.position.x() * other.velocity.y() - other.position.y() * other.velocity.x();
		}

		gauss(matrix, constants);

		final long rock_x = Math.round(constants[0]);
		final long rock_y = Math.round(constants[1]);
		final long vx = Math.round(constants[2]);

		matrix = new double[2][2];
		constants = new double[2];
		for (int i = 0; i < 2; i++) {
			final Hail hail = hailstones.get(i);
			final Hail other = hailstones.get(i + 1);
			matrix[i][0] = hail.velocity.x() - other.velocity.x();
			matrix[i][1] = other.position.x() - hail.position.x();
			constants[i] = hail.position.z() * hail.velocity.x() - hail.position.x() * hail.velocity.z()
					+ other.position.x() * other.velocity.z() - other.position.z() * other.velocity.x()
					- ((other.velocity.z() - hail.velocity.z()) * rock_x)
					- ((hail.position.z() - other.position.z()) * vx);
		}

		gauss(matrix, constants);

		final long rockZ = Math.round(constants[0]);

		return Long.toString(rock_x + rock_y + rockZ);
	}

	private static void gauss(double[][] matrix, double[] constants) {
		final int n = matrix.length;
		for (int i = 0; i < n; i++) {
			var pivot = matrix[i][i];
			for (int j = 0; j < n; j++) {
				matrix[i][j] = matrix[i][j] / pivot;
			}
			constants[i] = constants[i] / pivot;
			for (int k = 0; k < n; k++) {
				if (k != i) {
					double factor = matrix[k][i];
					for (int j = 0; j < n; j++) {
						matrix[k][j] = matrix[k][j] - factor * matrix[i][j];
					}
					constants[k] = constants[k] - factor * constants[i];
				}
			}
		}
	}

	private static record Hail(Point3DLong position, Point3DLong velocity) {
		public static Hail parse(String line) {
			final List<Point3DLong> points = Arrays.stream(line.split(" @ ")).map(Point3DLong::parse).toList();
			return new Hail(points.get(0), points.get(1));
		}

		public double slope() {
			return (double) velocity.y() / velocity.x();
		}

		public boolean intersects2d(Hail other, long min, long max) {
			final double slope = slope();
			final double other_slope = other.slope();
			if (slope == other_slope) {
				return false;
			}

			final double intersect_x = ((other_slope * other.position.x()) - (slope * position.x()) + position.y()
					- other.position.y()) / (other_slope - slope);
			if (intersect_x < min || intersect_x > max) {
				return false;
			}

			final double intersect_y = (slope * (intersect_x - position.x())) + position.y();
			if (intersect_y < min || intersect_y > max) {
				return false;
			}

			return isFuture(intersect_x, intersect_y) && other.isFuture(intersect_x, intersect_y);
		}

		public boolean isFuture(double x, double y) {
			return (velocity.x() >= 0 || position.x() >= x) && (velocity.x() <= 0 || position.x() <= x)
					&& (velocity.y() >= 0 || position.y() >= y) && (velocity.y() <= 0 || position.y() <= y);
		}
	}
}
