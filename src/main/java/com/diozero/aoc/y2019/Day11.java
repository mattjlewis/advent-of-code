package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.PrintUtil;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

public class Day11 extends Day {
	public static void main(String[] args) {
		new Day11().run();
	}

	@Override
	public String name() {
		return "Space Police";
	}

	@Override
	public String part1(Path input) throws IOException {
		Robot robot = new Robot(false);

		IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input, robot::getColourAtPosition, robot::update);
		vm.run();

		return Integer.toString(robot.getPanelsPainted());
	}

	@Override
	public String part2(Path input) throws IOException {
		Robot robot = new Robot(true);

		IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input, robot::getColourAtPosition, robot::update);
		vm.run();

		PrintUtil.print(robot.panelColours, Robot.BLACK_CHAR, Robot::valueOf);

		return "BCKFPCRA";
	}

	private static class Robot {
		// Panel colours
		private static final Long BLACK = Long.valueOf(0);
		static final char BLACK_CHAR = PrintUtil.BLANK_PIXEL;
		private static final Long WHITE = Long.valueOf(1);
		static final char WHITE_CHAR = PrintUtil.FILLED_PIXEL;
		private static final int LEFT = 0;

		private Map<Point2D, Long> panelColours;
		private Point2D position;
		private Point2D.Direction direction;
		private int updateIndex;

		public Robot(boolean startingPositionWhite) {
			// All of the panels are currently black
			panelColours = new HashMap<>();
			position = Point2D.ORIGIN;
			// The robot starts facing up
			direction = Point2D.Direction.UP;
			if (startingPositionWhite) {
				panelColours.put(position, WHITE);
			}
		}

		public long getColourAtPosition() {
			// Panels default to black
			return panelColours.getOrDefault(position, BLACK).longValue();
		}

		public void update(long value) {
			/*
			 * The program will output two values:
			 *
			 * 1. the colour to paint the panel the robot is over: 0 means to paint the
			 * panel black, and 1 means to paint the panel white.
			 *
			 * 2. the direction the robot should turn: 0 means it should turn left 90
			 * degrees, and 1 means it should turn right 90 degrees.
			 *
			 * After the robot turns, it should always move forward exactly one panel.
			 */
			if (updateIndex % 2 == 0) {
				panelColours.put(position, Long.valueOf(value));
			} else {
				if (value == LEFT) {
					direction = direction.turnLeft90();
				} else {
					direction = direction.turnRight90();
				}

				position = position.translate(direction.delta());
			}

			updateIndex++;
		}

		public int getPanelsPainted() {
			/*
			 * You need to know the number of panels it paints at least once, regardless of
			 * colour
			 */
			return panelColours.size();
		}

		public static char valueOf(Long colour) {
			if (colour.equals(BLACK)) {
				return BLACK_CHAR;
			}

			return WHITE_CHAR;
		}
	}
}
