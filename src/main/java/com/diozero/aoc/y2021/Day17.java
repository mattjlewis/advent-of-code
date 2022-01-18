package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.geometry.Projectile;
import com.diozero.aoc.geometry.Rectangle;

/**
 * Credit:
 * https://github.com/prendradjaja/advent-of-code-2021/blob/main/17--trick-shot/a.py
 */
public class Day17 extends Day {
	private static final int DRAG_X = 1;
	private static final int ACCEL_Y = -1;
	private static final int START_X = 0;
	private static final int START_Y = 0;

	public static void main(String[] args) {
		new Day17().run();
	}

	@Override
	public String name() {
		return "Trick Shot";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Rectangle target = loadData(input);
		Logger.debug("target: {}", target);

		/*
		 * First, calculate the maximum y velocity that would pass through the target
		 * area, completely ignoring x. For any yVel > 0, y' = -yVel - 1. We need to
		 * maximise yVel without y' moving past yMin. This maximum is reached at: yVel =
		 * -yMin - 1
		 */
		final int max_y_vel = -target.bottomRight().y() - 1;
		Logger.debug("max_y_vel: {}", max_y_vel);

		// Now calculate how high the projectile would go; don't even need to consider x
		int max_height = 0;
		for (int i = 1; i <= max_y_vel; i++) {
			max_height += i;
		}

		return Integer.toString(max_height);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Rectangle target = loadData(input);
		Logger.debug("target: {}", target);

		final int max_x_vel = target.bottomRight().x();
		final int min_y_vel = target.bottomRight().y();
		final int max_y_vel = -target.bottomRight().y() - 1;
		Logger.debug("max_x_vel: {}, min_y_vel: {}, max_y_vel: {}", max_x_vel, min_y_vel, max_y_vel);

		final Projectile p = new Projectile(START_X, START_Y, 0, 0, DRAG_X, ACCEL_Y);
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

		return Integer.toString(hits);
	}

	private static Rectangle loadData(final Path input) throws IOException {
		return Files.lines(input).findFirst().map(Rectangle::create).orElseThrow();
	}
}
