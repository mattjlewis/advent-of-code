package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;

import com.diozero.aoc.Day;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

public class Day2 extends Day {
	public static void main(String[] args) {
		new Day2().run();
	}

	@Override
	public String name() {
		return "1202 Program Alarm";
	}

	@Override
	public String part1(Path input) throws IOException {
		final IntcodeVirtualMachine program = IntcodeVirtualMachine.load(input);

		/*
		 * Before running the program, replace position 1 with the value 12 and replace
		 * position 2 with the value 2
		 */
		program.store(1, 12);
		program.store(2, 2);

		program.run();

		return Long.toString(program.get(0));
	}

	@Override
	public String part2(Path input) throws IOException {
		// What pair of inputs produces the output 19,690,720?
		// noun: 54, verb: 85

		for (int noun = 0; noun < 100; noun++) {
			for (int verb = 0; verb < 100; verb++) {
				final IntcodeVirtualMachine program = IntcodeVirtualMachine.load(input);
				program.store(1, noun);
				program.store(2, verb);

				program.run();

				if (program.get(0) == 19_690_720) {
					return Integer.toString(100 * noun + verb);
				}
			}
		}

		throw new IllegalStateException("No solution found");
	}
}
