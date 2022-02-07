package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.ArrayUtil;
import com.diozero.aoc.util.FunctionUtil;
import com.diozero.aoc.util.TextParser;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

public class Day7 extends Day {
	public static void main(String[] args) {
		new Day7().run();
	}

	@Override
	public String name() {
		return "Amplification Circuit";
	}

	@Override
	public String part1(Path input) throws IOException {
		final long[] program_data = TextParser.loadFirstLineAsCsvLongArray(input);
		final List<Long> phase_setting_values = LongStream.rangeClosed(0, 4).boxed().toList();

		return Long.toString(ArrayUtil.permutations(phase_setting_values)
				.mapToLong(phase_setting -> runAmplifiers(program_data, phase_setting)).max().orElseThrow());
	}

	@Override
	public String part2(Path input) throws IOException {
		final long[] program_data = TextParser.loadFirstLineAsCsvLongArray(input);
		final List<Long> phase_setting_values = LongStream.rangeClosed(5, 9).boxed().toList();

		return Long.toString(ArrayUtil.permutations(phase_setting_values)
				.mapToInt(phase_setting -> runAmplifiers(program_data, phase_setting)).max().orElseThrow());
	}

	private static int runAmplifiers(long[] programData, List<Long> phaseSettings) {
		final int num_amplifiers = phaseSettings.size();

		/*
		 * Generate the blocking queues to be used as the input for each amplifier.
		 * Output from one amplifier will be added to the queue of the next amplifier.
		 *
		 * Must be a BlockingQueue as each amplifier needs to block while waiting for
		 * input from the output of the previous amplifier.
		 */
		final List<BlockingQueue<Long>> input_output = IntStream.range(0, num_amplifiers)
				.mapToObj(i -> (BlockingQueue<Long>) new LinkedBlockingQueue<>(List.of(phaseSettings.get(i)))).toList();
		// Seed the second input value for the first amplifier - subsequent iterations
		// take this from the output of the last amplifier in the feedback loop
		input_output.get(0).offer(Long.valueOf(0));

		final ExecutorService es = Executors.newFixedThreadPool(num_amplifiers);
		// Start an Intcode VM for each amplifier using the output from blocking queue n
		// as the input for blocking queue n+1; i.e. input for n is output from n-1
		IntStream.range(0, num_amplifiers)
				.mapToObj(n -> IntcodeVirtualMachine.load(programData,
						FunctionUtil.blockingLongSupplier(input_output.get(n)),
						FunctionUtil.blockingLongConsumer(input_output.get((n + 1) % num_amplifiers))))
				.forEach(es::submit);

		Logger.debug("Waiting for Intcode VMs to complete");
		try {
			es.shutdown();
			if (!es.awaitTermination(10, TimeUnit.SECONDS)) {
				throw new RuntimeException("Intcode VMs didn't terminate");
			}
		} catch (InterruptedException e) {
			Logger.error(e, "Error: {}", e);
			throw new RuntimeException(e);
		}
		Logger.debug("All Intcode VMs completed");

		return input_output.get(0).peek().intValue();
	}
}
