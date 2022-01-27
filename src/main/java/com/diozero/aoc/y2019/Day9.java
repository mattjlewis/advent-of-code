package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

import com.diozero.aoc.Day;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

public class Day9 extends Day {
	public static void main(String[] args) {
		new Day9().run();
	}

	@Override
	public String name() {
		return "Sensor Boost";
	}

	@Override
	public String part1(Path input) throws IOException {
		final Queue<Long> output = new LinkedList<>();
		IntcodeVirtualMachine.load(input, () -> 1, output::offer).run();
		// The sample test programs output more than one value
		return output.size() == 1 ? output.peek().toString() : output.toString();
	}

	@Override
	public String part2(Path input) throws IOException {
		final Queue<Long> output = new LinkedList<>();
		IntcodeVirtualMachine.load(input, () -> 2, output::offer).run();
		// The sample test programs output more than one value
		return output.size() == 1 ? output.peek().toString() : output.toString();
	}
}
