package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.LongConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Line2D;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.FunctionUtil;
import com.diozero.aoc.util.PrintUtil;
import com.diozero.aoc.util.TextParser;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

public class Day17 extends Day {
	private static final Map<Character, CompassDirection> ROBOT_DIRECTIONS;
	static {
		ROBOT_DIRECTIONS = new HashMap<>();
		ROBOT_DIRECTIONS.put(Character.valueOf('^'), CompassDirection.NORTH);
		ROBOT_DIRECTIONS.put(Character.valueOf('>'), CompassDirection.EAST);
		ROBOT_DIRECTIONS.put(Character.valueOf('v'), CompassDirection.SOUTH);
		ROBOT_DIRECTIONS.put(Character.valueOf('<'), CompassDirection.WEST);
	}

	private static final Pattern PARTS_PATTERN = Pattern
			.compile("^(.{1,20})\\1*(.{1,20})(?:\\1|\\2)*(.{1,20})(?:\\1|\\2|\\3)*$");

	public static void main(String[] args) {
		new Day17().run();
	}

	@Override
	public String name() {
		return "Set and Forget";
	}

	@Override
	public String part1(Path input) throws IOException {
		return Integer
				.toString(Line2D.pathIntersections(loadData(input).path()).stream().mapToInt(p -> p.x() * p.y()).sum());
	}

	@Override
	public String part2(Path input) throws IOException {
		// Load the scaffolding structure by running the VM with the robot asleep
		final ShipScaffolding scaffolding = loadData(input);

		final BlockingQueue<Long> program_input_queue = new LinkedBlockingQueue<>();
		final ProgramOutput program_output = new ProgramOutput();
		final IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input,
				FunctionUtil.blockingLongSupplier(program_input_queue), program_output);

		// Get the full set of movement instructions
		final StringBuilder buffer = new StringBuilder();
		CompassDirection robot_dir = scaffolding.robotDirection();
		for (Line2D line : scaffolding.path()) {
			buffer.append(getTurn(robot_dir, line.compassDirection()).value()).append(',').append(line.length())
					.append(',');

			robot_dir = line.compassDirection();
		}
		final String movement_instructions = buffer.toString();
		Logger.debug("movement_instructions: {}", movement_instructions);

		// Extract three unique repeating patterns (A, B, C)
		final Matcher m = PARTS_PATTERN.matcher(movement_instructions);
		if (!m.matches()) {
			throw new IllegalStateException(
					"Cannot extract A, B, C parts from instructions '" + movement_instructions + "'");
		}
		final String a = m.group(1).substring(0, m.group(1).length() - 1);
		final String b = m.group(2).substring(0, m.group(2).length() - 1);
		final String c = m.group(3).substring(0, m.group(3).length() - 1);
		Logger.debug("A: '{}', B: '{}', C: '{}'", a, b, c);

		// Replace all occurrences of the three patterns with A/B/C
		String main_routine = movement_instructions.replaceAll(a, "A").replaceAll(b, "B").replaceAll(c, "C");
		main_routine = main_routine.substring(0, main_routine.length() - 1);
		Logger.debug("Main routine: {}", main_routine);

		sendProgramInput(program_input_queue, main_routine);
		sendProgramInput(program_input_queue, a);
		sendProgramInput(program_input_queue, b);
		sendProgramInput(program_input_queue, c);
		sendProgramInput(program_input_queue, "n");

		// Wake up the vacuum robot by changing the value at address 0 from 1 to 2
		vm.store(0, 2);
		// Run the Intcode VM program until it halts
		vm.run();

		// The last value output is the total amount of space dust collected
		return program_output.tail().toString();
	}

	private static void sendProgramInput(BlockingQueue<Long> programInputQueue, String input) {
		input.chars().forEach(ch -> programInputQueue.offer(Long.valueOf(ch)));
		programInputQueue.offer(Long.valueOf('\n'));
	}

	private static Turn getTurn(CompassDirection robotDirection, CompassDirection lineDirection) {
		if (robotDirection == lineDirection || robotDirection == lineDirection.opposite()) {
			throw new IllegalArgumentException();
		}

		return switch (robotDirection) {
		case NORTH -> switch (lineDirection) {
		case EAST -> Turn.RIGHT;
		case WEST -> Turn.LEFT;
		default -> throw new IllegalArgumentException();
		};
		case EAST -> switch (lineDirection) {
		case SOUTH -> Turn.RIGHT;
		case NORTH -> Turn.LEFT;
		default -> throw new IllegalArgumentException();
		};
		case SOUTH -> switch (lineDirection) {
		case WEST -> Turn.RIGHT;
		case EAST -> Turn.LEFT;
		default -> throw new IllegalArgumentException();
		};
		case WEST -> switch (lineDirection) {
		case NORTH -> Turn.RIGHT;
		case SOUTH -> Turn.LEFT;
		default -> throw new IllegalArgumentException();
		};
		default -> throw new IllegalArgumentException();
		};
	}

	private static ShipScaffolding loadData(Path input) throws IOException {
		final StringBuffer output_buffer = new StringBuffer();
		final IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input, null, l -> output_buffer.append((char) l));
		vm.run();

		// First extract the set of scaffolding points and find the vacuum robot
		final String[] output_lines = output_buffer.toString().split("\\n");
		final Set<Point2D> points = new HashSet<>();
		Point2D robot_pos = null;
		CompassDirection robot_direction = null;
		for (int y = 0; y < output_lines.length; y++) {
			final String line = output_lines[y];

			for (int x = 0; x < line.length(); x++) {
				char ch = line.charAt(x);
				if (ch == TextParser.UNSET_CHAR) {
					continue;
				}

				final Point2D pos = new Point2D(x, y);
				points.add(pos);
				if (ch != TextParser.SET_CHAR) {
					robot_direction = ROBOT_DIRECTIONS.get(Character.valueOf(ch));
					robot_pos = pos;
				}
			}
		}
		Logger.debug("Vacuum robot located, facing {} @ {}", robot_direction, robot_pos);

		if (Logger.isDebugEnabled()) {
			PrintUtil.print(points);
		}

		if (robot_pos == null) {
			throw new IllegalStateException("Unable to find robot starting position");
		}

		// Then convert the individual scaffolding points into a path of lines
		final List<Line2D> path = new ArrayList<>();
		Point2D line_start_pos = robot_pos;
		Point2D current_pos = robot_pos;
		CompassDirection current_dir = null;
		do {
			// Direction not set or changed?
			if (current_dir == null || !points.contains(current_pos.translate(current_dir))) {
				// Find the new direction
				CompassDirection new_direction = null;
				for (CompassDirection dir : ROBOT_DIRECTIONS.values()) {
					if (current_dir != null && (dir == current_dir || dir == current_dir.opposite())) {
						// Ignore as we know the line doesn't continue in this direction
						continue;
					}

					if (points.contains(current_pos.translate(dir))) {
						new_direction = dir;
						break;
					}
				}

				// Add a new line if current_dir was previously set
				if (current_dir != null) {
					path.add(Line2D.create(line_start_pos, current_pos));
					line_start_pos = current_pos;

					if (new_direction == null) {
						Logger.debug("Reached the end of the path @ {}", current_pos);
						break;
					}
				}

				current_dir = new_direction;
			}

			current_pos = current_pos.translate(current_dir);
		} while (true);

		return new ShipScaffolding(path, robot_pos, robot_direction);
	}

	private enum Turn {
		LEFT('L'), RIGHT('R');

		private char value;

		Turn(char value) {
			this.value = value;
		}

		public char value() {
			return value;
		}
	}

	private static record ShipScaffolding(List<Line2D> path, Point2D robotPosition, CompassDirection robotDirection) {
		//
	}

	private static class ProgramOutput implements LongConsumer {
		private Deque<Long> spaceDust = new LinkedList<>();

		@Override
		public void accept(long value) {
			char ch = (char) value;
			if (ch != '\n' && ch != TextParser.UNSET_CHAR && ch != TextParser.SET_CHAR && ch != '<' && ch != '>'
					&& ch != '^' && ch != 'v') {
				spaceDust.add(Long.valueOf(value));
			} else if (Logger.isDebugEnabled()) {
				System.out.print(ch);
			}
		}

		public Long tail() {
			return spaceDust.peekLast();
		}
	}
}
