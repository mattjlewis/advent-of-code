package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.function.IntBiFunction;
import com.diozero.aoc.util.OcrUtil;

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
		return Integer.toString(solve(input, Day10::getSignalStrength).mapToInt(Integer::intValue).sum());
	}

	private static Integer getSignalStrength(final int x, final int cycle) {
		return Integer.valueOf(((cycle - SCREEN_WIDTH / 2) % SCREEN_WIDTH == 0) ? cycle * x : 0);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final boolean[][] pixels = new boolean[OcrUtil.CHAR_HEIGHT][SCREEN_WIDTH];
		final Iterator<Boolean> it = solve(input, Day10::getPixel).iterator();
		int i = 0;
		while (it.hasNext()) {
			pixels[i / SCREEN_WIDTH][i % SCREEN_WIDTH] = it.next().booleanValue();
			i++;
		}

		return OcrUtil.decode(pixels);
	}

	private static Boolean getPixel(final int x, final int cycle) {
		int pixel_pos = (cycle - 1) % SCREEN_WIDTH;
		return Boolean.valueOf(Math.abs(pixel_pos - x) <= 1);
	}
}
