package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.diozero.aoc.Day;

public class Day12 extends Day {
	public static void main(String[] args) {
		new Day12().run();
	}

	@Override
	public String name() {
		return "Rain Risk";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Instruction> instructions = Files.lines(input).map(Instruction::parse).toList();

		// The ship starts 0, 0 facing east
		int direction = 90;
		int pos_x = 0;
		int pos_y = 0;

		for (Instruction instruction : instructions) {
			switch (instruction.action()) {
			case N:
				pos_y -= instruction.amount();
				break;
			case E:
				pos_x += instruction.amount();
				break;
			case S:
				pos_y += instruction.amount();
				break;
			case W:
				pos_x -= instruction.amount();
				break;
			case F:
				switch (direction) {
				case 0:
					pos_y -= instruction.amount();
					break;
				case 90:
					pos_x += instruction.amount();
					break;
				case 180:
					pos_y += instruction.amount();
					break;
				case 270:
					pos_x -= instruction.amount();
					break;
				default:
					throw new IllegalArgumentException("Invalid direction " + direction);
				}
				break;
			case L:
				direction = (direction + 360 - instruction.amount()) % 360;
				break;
			case R:
				direction = (direction + instruction.amount()) % 360;
				break;
			default:
				throw new IllegalArgumentException("Invalid instruction action in " + instruction);
			}
		}

		return Long.toString(Math.abs(pos_x) + Math.abs(pos_y));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Instruction> instructions = Files.lines(input).map(Instruction::parse).toList();

		// The ship starts at 0, 0
		int ship_x = 0;
		int ship_y = 0;
		// The waypoint starts 10 units east and 1 unit north relative to the ship.
		int waypoint_x = 10;
		int waypoint_y = -1;

		for (Instruction instruction : instructions) {
			switch (instruction.action()) {
			case N:
				waypoint_y -= instruction.amount();
				break;
			case E:
				waypoint_x += instruction.amount();
				break;
			case S:
				waypoint_y += instruction.amount();
				break;
			case W:
				waypoint_x -= instruction.amount();
				break;
			case F:
				/*
				 * Move forward to the waypoint a number of times equal to the given value.
				 *
				 * E.g. From the starting position (10 east, 1 north) F10 moves the ship to the
				 * waypoint 10 times (a total of 100 units east and 10 units north), leaving the
				 * ship at east 100, north 10. The waypoint stays 10 units east and 1 unit north
				 * of the ship.
				 */
				ship_x += instruction.amount() * waypoint_x;
				ship_y += instruction.amount() * waypoint_y;
				break;
			case L, R:
				// Rotate the waypoint around the ship left or right
				int rotation = instruction.amount();
				if (instruction.action() == Action.L) {
					// Convert counter-clockwise to clockwise
					rotation = (360 - rotation) % 360;
				}
				// Rotate the waypoint around the ship clockwise
				switch (rotation) {
				case 90:
					int tmp = waypoint_x;
					waypoint_x = -waypoint_y;
					waypoint_y = tmp;
					break;
				case 180:
					waypoint_x = -waypoint_x;
					waypoint_y = -waypoint_y;
					break;
				case 270:
					tmp = waypoint_x;
					waypoint_x = waypoint_y;
					waypoint_y = -tmp;
					break;
				default:
					System.out.println("Illegal rotation " + rotation);
				}

				break;
			default:
				throw new IllegalArgumentException("Invalid instruction action in " + instruction);
			}
		}

		return Long.toString(Math.abs(ship_x) + Math.abs(ship_y));
	}

	private enum Action {
		N, E, S, W, L, R, F;
	}

	private static record Instruction(Action action, int amount) {
		public static Instruction parse(final String line) {
			return new Instruction(Action.valueOf(line.substring(0, 1)), Integer.parseInt(line.substring(1)));
		}
	}
}
