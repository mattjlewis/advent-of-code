package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hipparchus.util.ArithmeticUtils;
import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.MutablePoint3D;
import com.diozero.aoc.geometry.Point3D;

public class Day12 extends Day {
	public static void main(String[] args) {
		new Day12().run();
	}

	@Override
	public String name() {
		return "The N-Body Problem";
	}

	@Override
	public String part1(Path input) throws IOException {
		final List<Moon> moons = Files.lines(input).map(Moon::create).toList();

		for (int step = 0; step < 1000; step++) {
			Moon.step(moons);
		}

		return Integer.toString(moons.stream().mapToInt(Moon::totalEnergy).sum());
	}

	@Override
	public String part2(Path input) throws IOException {
		final List<Moon> moons = Files.lines(input).map(Moon::create).toList();

		final Point3D repeat = findRepeat(moons);
		Logger.debug("repeat: {}", repeat);

		return Long.toString(ArithmeticUtils.lcm(ArithmeticUtils.lcm((long) repeat.x(), repeat.y()), repeat.z()));
	}

	private static Point3D findRepeat(List<Moon> moons) {
		Integer x_repeat = null;
		Integer y_repeat = null;
		Integer z_repeat = null;
		String x_start = null;
		String y_start = null;
		String z_start = null;

		int step = 0;
		while (x_repeat == null || y_repeat == null || z_repeat == null) {
			Moon.step(moons);

			if (x_repeat == null) {
				String x_repr = moons.stream().map(moon -> moon.axisRepresentation(Point3D.Axis.X)).reduce("",
						(a, b) -> String.join(":", a, b));
				if (x_start == null) {
					x_start = x_repr;
				} else if (x_start.equals(x_repr)) {
					x_repeat = Integer.valueOf(step);
				}
			}

			if (y_repeat == null) {
				String y_repr = moons.stream().map(moon -> moon.axisRepresentation(Point3D.Axis.Y)).reduce("",
						(a, b) -> String.join(":", a, b));
				if (y_start == null) {
					y_start = y_repr;
				} else if (y_start.equals(y_repr)) {
					y_repeat = Integer.valueOf(step);
				}
			}

			if (z_repeat == null) {
				String z_repr = moons.stream().map(moon -> moon.axisRepresentation(Point3D.Axis.Z)).reduce("",
						(a, b) -> String.join(":", a, b));
				if (z_start == null) {
					z_start = z_repr;
				} else if (z_start.equals(z_repr)) {
					z_repeat = Integer.valueOf(step);
				}
			}

			step++;
		}

		return new Point3D(x_repeat.intValue(), y_repeat.intValue(), z_repeat.intValue());
	}

	private static record Moon(MutablePoint3D position, MutablePoint3D velocity) {
		static Moon create(String line) {
			Pattern p = Pattern.compile("^<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)>$");
			Matcher m = p.matcher(line);
			if (!m.matches()) {
				throw new IllegalArgumentException("Line '" + line + "' does not match pattern " + p.pattern());
			}

			// The x, y, and z velocity of each moon starts at 0
			return new Moon(new MutablePoint3D(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
					Integer.parseInt(m.group(3))), new MutablePoint3D(0, 0, 0));
		}

		static void step(List<Moon> moons) {
			for (int i = 0; i < moons.size() - 1; i++) {
				for (int j = i + 1; j < moons.size(); j++) {
					Moon m1 = moons.get(i);
					Moon m2 = moons.get(j);
					m1.applyGravity(m2);
					m2.applyGravity(m1);
				}
			}

			moons.forEach(Moon::updatePosition);
		}

		public void applyGravity(Moon other) {
			if (other.equals(this)) {
				return;
			}

			velocity.translate(Integer.compare(other.position.getX(), position.getX()),
					Integer.compare(other.position.getY(), position.getY()),
					Integer.compare(other.position.getZ(), position.getZ()));
		}

		public void updatePosition() {
			position.translate(velocity);
		}

		public String axisRepresentation(Point3D.Axis axis) {
			return switch (axis) {
			case X -> position().getX() + "," + velocity().getX();
			case Y -> position().getY() + "," + velocity().getY();
			case Z -> position().getZ() + "," + velocity().getZ();
			default -> throw new IllegalArgumentException("Invalid axis " + axis);
			};
		}

		/**
		 * The total energy for a single moon is its potential energy multiplied by its
		 * kinetic energy. A moon's potential energy is the sum of the absolute values
		 * of its x, y, and z position coordinates. A moon's kinetic energy is the sum
		 * of the absolute values of its velocity coordinates.
		 *
		 * @return potential energy * kinetic energy
		 */
		public int totalEnergy() {
			return position.manhattanDistance(Point3D.ORIGIN) * velocity.manhattanDistance(Point3D.ORIGIN);
		}

		@Override
		public String toString() {
			return "pos=" + position.toString() + ", vel=" + velocity.toString();
		}
	}
}
