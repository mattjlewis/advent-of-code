package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;
import com.diozero.aoc.util.Point2D;

/**
 * Credit:
 * https://github.com/prendradjaja/advent-of-code-2021/blob/main/17--trick-shot/a.py
 */
public class Day17 extends AocBase {
	private static final int DRAG_X = 1;
	private static final int ACCEL_Y = -1;
	private static final int START_X = 0;
	private static final int START_Y = 0;

	public static void main(String[] args) {
		new Day17().run();
	}

	private static Area2D loadData(final Path input) throws IOException {
		return Files.lines(input).findFirst().map(Area2D::create).orElseThrow();
	}

	@Override
	public long part1(Path input) throws IOException {
		Area2D target = loadData(input);
		Logger.debug("target: {}", target);

		/*
		 * First, calculate the maximum y velocity that would pass through the target
		 * area, completely ignoring x. For any yVel > 0, y' = -yVel - 1. We need to
		 * maximise yVel without y' moving past yMin. This maximum is reached at: yVel =
		 * -yMin - 1
		 */
		int max_y_vel = -target.bottomRight().y() - 1;
		Logger.debug("max_y_vel: {}", max_y_vel);

		// Now calculate how high the projectile would go; don't even need to consider x
		int max_height = 0;
		for (int i = 1; i <= max_y_vel; i++) {
			max_height += i;
		}

		return max_height;
	}

	@Override
	public long part2(Path input) throws IOException {
		Area2D target = loadData(input);
		Logger.debug("target: {}", target);

		int max_x_vel = target.bottomRight().x();
		int min_y_vel = target.bottomRight().y();
		int max_y_vel = -target.bottomRight().y() - 1;
		Logger.debug("max_x_vel: {}, min_y_vel: {}, max_y_vel: {}", max_x_vel, min_y_vel, max_y_vel);

		Projectile p = new Projectile(START_X, START_Y, 0, 0, DRAG_X, ACCEL_Y);
		int hits = 0;
		for (int y_vel = min_y_vel; y_vel <= max_y_vel; y_vel++) {
			// Now iterate xVel to see if we hit the target
			for (int x_vel = 1; x_vel <= max_x_vel; x_vel++) {
				p.reset(START_X, START_Y, x_vel, y_vel);
				Point2D impact = p.hits(target);
				if (impact != null) {
					hits++;
					Logger.trace("impact: {},{}; x_vel: {}, y_vel: {}", impact.x(), impact.y(), x_vel, y_vel);
				}
			}
		}

		return hits;
	}

	public static record Area2D(Point2D topLeft, Point2D bottomRight) {
		// target area: x=20..30, y=-10..-5
		private static final String TARGET_REGEXP = "target area: x=(-?\\d+)\\.+(-?\\d+), y=(-?\\d+)\\.+(-?\\d+)";
		private static final Pattern TARGET_PATTERN = Pattern.compile(TARGET_REGEXP);

		public static Area2D create(String line) {
			Matcher m = TARGET_PATTERN.matcher(line);
			if (!m.matches()) {
				Logger.error("line '{}' does not match pattern '{}'!", line, TARGET_PATTERN.pattern());
				throw new IllegalArgumentException(
						String.format("line '{}' does not match pattern '{}'!", line, TARGET_PATTERN.pattern()));
			}

			int x1 = Integer.parseInt(m.group(1));
			int x2 = Integer.parseInt(m.group(2));
			if (x1 > x2) {
				int tmp = x1;
				x1 = x2;
				x2 = tmp;
			}

			int y1 = Integer.parseInt(m.group(3));
			int y2 = Integer.parseInt(m.group(4));
			if (y1 < y2) {
				int tmp = y1;
				y1 = y2;
				y2 = tmp;
			}

			return new Area2D(new Point2D(x1, y1), new Point2D(x2, y2));
		}

		public boolean contains(int x, int y) {
			return x >= topLeft.x() && x <= bottomRight.x() && y <= topLeft.y() && y >= bottomRight.y();
		}
	}

	public static class Projectile {
		private int posX;
		private int posY;
		private int velX;
		private int velY;
		private int dragX;
		private int accelY;

		public Projectile(int posX, int posY, int velX, int velY, int dragX, int accelY) {
			this.posX = posX;
			this.posY = posY;
			this.velX = velX;
			this.velY = velY;
			this.dragX = Math.abs(dragX);
			this.accelY = accelY;
		}

		public void reset(int posX, int posY, int velX, int velY) {
			this.posX = posX;
			this.posY = posY;
			this.velX = velX;
			this.velY = velY;
		}

		/**
		 * Return the point at which this projectile will hit the specified target
		 *
		 * @param target the target area
		 * @return the point at which this project hits the target; null if it misses
		 */
		public Point2D hits(Area2D target) {
			// Iterate until posY < target.bottomRight.posY or posX >
			// target.bottomRight().x()
			int prev_x;
			int prev_y;
			while (true) {
				prev_x = posX;
				prev_y = posY;
				step();
				if (posY < target.bottomRight().y() || posX > target.bottomRight().x()) {
					break;
				}
			}

			// Did we actually hit the target?
			if (prev_x < target.topLeft().x() || prev_x > target.bottomRight().x() || prev_y > target.topLeft().y()
					|| prev_y < target.bottomRight().y()) {
				return null;
			}

			return new Point2D(prev_x, prev_y);
		}

		public void step() {
			posX += velX;
			posY += velY;

			velX += (velX < 0 ? dragX : velX == 0 ? 0 : -dragX);
			velY += accelY;
		}

		public int getPosX() {
			return posX;
		}

		public int getPosY() {
			return posY;
		}

		@Override
		public String toString() {
			return "Projectile [posX=" + posX + ", posY=" + posY + ", velX=" + velX + ", velY=" + velY + ", dragX="
					+ dragX + ", accelY=" + accelY + "]";
		}
	}
}
