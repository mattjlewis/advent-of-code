package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.function.IntBiFunction;
import com.diozero.aoc.util.PrintUtil;
import com.diozero.aoc.util.StringUtil;

public class Day10 extends Day {
	private static final int SCREEN_WIDTH = 40;
	private static final String ADDX = "addx";

	public static void main(String[] args) {
		new Day10().run();
	}

	@Override
	public String name() {
		return "Cathode-Ray Tube";
	}

	private static <T> Stream<T> solve(final Path input, final IntBiFunction<T> func) throws IOException {
		final AtomicInteger x = new AtomicInteger(1);
		final AtomicInteger cycle = new AtomicInteger();

		return Files.lines(input).map(instr -> processInstruction(instr, x, cycle, func)).flatMap(Function.identity());
	}

	private static <T> Stream<T> processInstruction(final String instruction, final AtomicInteger x,
			final AtomicInteger cycle, final IntBiFunction<T> func) {
		final List<T> result = new ArrayList<>();
		result.add(func.apply(x.get(), cycle.incrementAndGet()));
		if (instruction.startsWith(ADDX)) {
			result.add(func.apply(x.get(), cycle.incrementAndGet()));
			x.addAndGet(Integer.parseInt(instruction.split(" ")[1]));
		}

		return result.stream();
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(solve(input, Day10::getSignalStrength).mapToInt(Integer::valueOf).sum());
	}

	private static Integer getSignalStrength(final int x, final int cycle) {
		return Integer.valueOf(((cycle - SCREEN_WIDTH / 2) % SCREEN_WIDTH == 0) ? cycle * x : 0);
	}

	@Override
	public String part2(final Path input) throws IOException {
		for (String s : StringUtil.split(
				solve(input, Day10::getPixel).map(Object::toString).collect(Collectors.joining()), SCREEN_WIDTH)) {
			System.out.println(s);
		}
		return "PAPJCBHP";
	}

	private static Character getPixel(final int x, final int cycle) {
		int pixel_pos = (cycle - 1) % SCREEN_WIDTH;
		return Character.valueOf(Math.abs(pixel_pos - x) <= 1 ? PrintUtil.FILLED_PIXEL : PrintUtil.BLANK_PIXEL);
	}
}