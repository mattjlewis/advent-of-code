package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.FunctionUtil;
import com.diozero.aoc.util.IntRange;
import com.diozero.aoc.util.PrintUtil;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

public class Day19 extends Day {
	public static void main(String[] args) throws IOException {
		new Day19().run();
	}

	@Override
	public String name() {
		return "Tractor Beam";
	}

	@Override
	public String part1(Path input) throws IOException {
		final BlockingQueue<Long> input_queue = new LinkedBlockingDeque<>();
		final Queue<Long> output = new ArrayDeque<>();

		final IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input,
				FunctionUtil.blockingLongSupplier(input_queue), output::offer);

		int range = 50;
		int x_start = 0;
		int x_end = 0;
		final Set<Point2D> points = new HashSet<>();
		final List<IntRange> ranges = new ArrayList<>();
		a: for (int y = 0; y < range; y++) {
			boolean started = false;
			for (int x = x_start; x < range; x++) {
				if (inTractorBeam(vm, input_queue, output, x, y)) {
					if (!started) {
						x_start = x;
						started = true;
					}
					points.add(new Point2D(x, y));
				} else if (started) {
					x_end = x - 1;
					if (x != 0 && y != 0) {
						ranges.add(new IntRange(x_start, x_end));
					}
					continue a;
				}
			}
		}

		if (Logger.isDebugEnabled()) {
			PrintUtil.print(points);
		}

		return Integer.toString(points.size());
	}

	@Override
	public String part2(Path input) throws IOException {
		final BlockingQueue<Long> input_queue = new LinkedBlockingDeque<>();
		final Queue<Long> output = new ArrayDeque<>();

		IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input, FunctionUtil.blockingLongSupplier(input_queue),
				output::offer);

		// The tractor beam starts at (0, 0)
		int x_start = 0;
		int y_start = 0;

		// Now get an approximation of the line formulas (y=mx+c)
		int y = y_start + 1_000;
		int x1_start = -1;
		int x2_end = -1;
		// The search start value might need to be tweaked based on the input program
		for (int x = 700;; x++) {
			if (inTractorBeam(vm, input_queue, output, x, y)) {
				if (x1_start == -1) {
					x1_start = x;
				} else {
					x2_end = x;
				}
			} else if (x2_end != -1) {
				break;
			}
		}
		Logger.debug("x1_start: {}, x2_end: {} @ y {}", x1_start, x2_end, y);

		// Get m and c for both lines
		// m = (y2 - y1) / (x2 - x1)
		// y = mx + c
		// c = y - mx
		double m1 = (y - y_start) / (double) (x1_start - x_start);
		double c1 = y - m1 * x1_start;
		double m2 = (y - y_start) / (double) (x2_end - x_start);
		double c2 = y - x2_end * m2;
		Logger.debug("Using y=mx + c: y1 = {} * x1 + {}, y2 = {} * x2 + {}", m1, c1, m2, c2);

		int grid_size = 100 - 1;

		/*-
		 * Predict where the 100x100 square will first occur (will be approximate due to rounding errors).
		 *
		 * Calculate x such that f1(x) - f2(x+99) = 99; where f(x)=mx+c => fn(x)=mn * x + cn
		 *
		 * That gives:
		 * (m1 * x + c1) - (m2 * (x + 99) + c2) = 99
		 *
		 * Simplifying to get x:
		 * m1 * x + c1 - m2 * x - m2 * 99 - c2 = 99
		 * m1 * x - m2 * x = 99 + m2 * 99 + c2 - c1
		 * x * (m1  - m2) = 99 + m2 * 99 + c2 - c1
		 * x = (99 + m2 * 99 + c2 - c1) / (m1  - m2)
		 */
		double x1 = (grid_size + m2 * grid_size + c2 - c1) / (m1 - m2);
		double x2 = x1 + grid_size;
		double y1 = m1 * x1 + c1;
		double y2 = m2 * x2 + c2;
		Logger.debug("Predicted values: x1: {}, y1: {}, x2: {}, y2: {}", x1, y1, x2, y2);

		// Iterate to find the actual first positions where a 100x100 grid is possible
		int int_x1 = (int) Math.round(x1);
		int int_y1 = (int) Math.round(y1);
		int int_y2 = (int) Math.round(y2);
		int dx = 0;
		int dy = 0;
		int search_delta = 2;
		a: for (dy = -search_delta; dy <= search_delta; dy++) {
			for (dx = -search_delta; dx <= search_delta; dx++) {
				if (inTractorBeam(vm, input_queue, output, int_x1 + dx, int_y1 + dy)
						&& inTractorBeam(vm, input_queue, output, int_x1 + dx + grid_size, int_y1 + dy - grid_size)) {
					break a;
				}
			}
		}
		int_x1 += dx;
		int_y2 += dy;
		Logger.debug("dx: {}, dy: {} => x1: {}, y2: {}", dx, dy, int_x1, int_y2);

		return Integer.toString(int_x1 * 10_000 + int_y2);
	}

	private static boolean inTractorBeam(IntcodeVirtualMachine vm, BlockingQueue<Long> inputQueue, Queue<Long> output,
			int x, int y) {
		vm.reset();
		inputQueue.addAll(List.of(Long.valueOf(x), Long.valueOf(y)));
		vm.run();
		return output.poll().intValue() == 1;
	}
}
