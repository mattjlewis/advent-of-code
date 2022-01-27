package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import com.diozero.aoc.Day;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

public class Day5 extends Day {
	public static void main(String[] args) {
		new Day5().run();
	}

	@Override
	public String name() {
		return "Sunny with a Chance of Asteroids";
	}

	@Override
	public String part1(Path input) throws IOException {
		AtomicLong output = new AtomicLong();
		final IntcodeVirtualMachine program = IntcodeVirtualMachine.load(input, () -> 1, output::set);

		program.run();

		return Long.toString(output.get());
	}

	@Override
	public String part2(Path input) throws IOException {
		AtomicLong output = new AtomicLong();
		/*-
		IntcodeProgram.parse(input, () -> 8, output::set).run();
		System.out.println(output);
		IntcodeProgram.parse(input, () -> 1, output::set).run();
		System.out.println(output);
		IntcodeProgram.parse(input, () -> 7, output::set).run();
		System.out.println(output);
		IntcodeProgram.parse(input, () -> 9, output::set).run();
		System.out.println(output);
		*/

		IntcodeVirtualMachine.load(input, () -> 5, output::set).run();
		return Long.toString(output.get());
	}
}
