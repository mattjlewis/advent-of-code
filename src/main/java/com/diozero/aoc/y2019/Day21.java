package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.LongConsumer;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.FunctionUtil;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

/**
 * Credit: https://work.njae.me.uk/2020/01/07/advent-of-code-2019-day-21/
 */
public class Day21 extends Day {
	public static void main(String[] args) {
		new Day21().run();
	}

	@Override
	public String name() {
		return "Springdroid Adventure";
	}

	@Override
	public String part1(Path input) throws IOException {
		final BlockingQueue<Long> input_queue = new LinkedBlockingQueue<>();
		final ProgramOutput output = new ProgramOutput();

		/*-
		 * "jump if you can see a hole, so long as there is ground at your landing space":
		 * (!A || !B || !C) && D =>
		 * !(A && B && C) && D
		 */
		List.of("OR A T", "AND B T", "AND C T", "NOT T J", "AND D J", "WALK").forEach(s -> sendCommand(s, input_queue));

		final IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input,
				FunctionUtil.blockingLongSupplier(input_queue), output);
		vm.run();

		return Long.toString(output.answer());
	}

	@Override
	public String part2(Path input) throws IOException {
		final BlockingQueue<Long> input_queue = new LinkedBlockingQueue<>();
		final ProgramOutput output = new ProgramOutput();

		/*-
		 * !(A && B && C) && (E || H) && D
		 */
		List.of("OR A T", "AND B T", "AND C T", "NOT T J", "OR E T", "OR H T", "AND T J", "AND D J", "RUN")
				.forEach(s -> sendCommand(s, input_queue));

		final IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input,
				FunctionUtil.blockingLongSupplier(input_queue), output);
		vm.run();

		return Long.toString(output.answer());
	}

	private static void sendCommand(String line, BlockingQueue<Long> inputQueue) {
		line.chars().forEach(ch -> inputQueue.offer(Long.valueOf(ch)));
		inputQueue.offer(Long.valueOf('\n'));
	}

	private static class ProgramOutput implements LongConsumer {
		private long answer;

		@Override
		public void accept(long value) {
			char ch = (char) value;
			if (ch < '\n' || ch > 'z') {
				answer = value;
			} else if (Logger.isDebugEnabled()) {
				System.out.print((char) value);
			}
		}

		public long answer() {
			return answer;
		}
	}
}
