package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diozero.aoc.Day;

public class Day8 extends Day {
	public static void main(String[] args) {
		new Day8().run();
	}

	@Override
	public String name() {
		return "Handheld Halting";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Instruction[] instructions = Files.lines(input).map(Instruction::parse).toArray(Instruction[]::new);

		try {
			return Integer.toString(execute(instructions));
		} catch (IllegalProgramException e) {
			// Ignore
			return Integer.toString(e.acc());
		}
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Instruction[] orig_instructions = Files.lines(input).map(Instruction::parse).toArray(Instruction[]::new);

		// First try changing each jmp instruction to nop
		Instruction[] instructions = orig_instructions;
		int jmp_index = 0;
		boolean iterate = true;
		while (iterate) {
			try {
				return Integer.toString(execute(instructions));
			} catch (IllegalProgramException e) {
				iterate = false;

				for (int i = jmp_index; i < orig_instructions.length && !iterate; i++) {
					if (instructions[i].op() == Operation.jmp) {
						// TODO Optimise by simply swapping the instruction rather than cloning the
						// entire program - would also need to revert
						instructions = orig_instructions.clone();
						instructions[i] = new Instruction(Operation.nop, instructions[i].argument());
						jmp_index = i + 1;

						iterate = true;
					}
				}
			}
		}

		// Then try changing each nop instruction to jmp
		instructions = orig_instructions;
		int nop_index = 0;
		iterate = true;
		while (iterate) {
			try {
				return Integer.toString(execute(instructions));
			} catch (IllegalProgramException e) {
				iterate = false;

				for (int i = nop_index; i < orig_instructions.length && !iterate; i++) {
					if (instructions[i].op() == Operation.nop) {
						// TODO Optimise by simply swapping the instruction rather than cloning the
						// entire program - would also need to revert
						instructions = orig_instructions.clone();
						instructions[i] = new Instruction(Operation.jmp, instructions[i].argument());
						nop_index = i + 1;

						iterate = true;
					}
				}
			}
		}

		return "";
	}

	private static int execute(final Instruction[] instructions) throws IllegalProgramException {
		// Map instruction program counter to number of times it has been executed
		Map<Integer, AtomicInteger> execution_count = new HashMap<>();
		int acc = 0;
		int pc = 0;

		while (pc < instructions.length) {
			Instruction instr = instructions[pc];

			// Instructions can only be executed once to avoid infinite loops
			if (execution_count.computeIfAbsent(Integer.valueOf(pc), i -> new AtomicInteger()).getAndIncrement() > 0) {
				throw new IllegalProgramException("Infinite loop detected", acc);
			}

			switch (instr.op()) {
			case jmp:
				pc += instr.argument();
				break;
			case acc:
				acc += instr.argument();
				// Note deliberate fall-through to increment the program counter
			case nop:
				pc++;
				break;
			default:
				throw new IllegalArgumentException("Invalid operation " + instr.op());
			}

			if (pc < 0 || pc > instructions.length) {
				// Error in the program
				throw new IllegalProgramException("Invalid program counter: " + pc, acc);
			}
		}

		return acc;
	}

	private enum Operation {
		acc, jmp, nop;
	}

	private static record Instruction(Operation op, int argument) {
		private static final Pattern PATTERN = Pattern.compile("(acc|jmp|nop) ([+|-]\\d+)");

		public static Instruction parse(final String line) {
			Matcher m = PATTERN.matcher(line);
			if (!m.matches()) {
				throw new IllegalArgumentException("Line '" + line + "' does not match pattern " + PATTERN.pattern());
			}

			return new Instruction(Operation.valueOf(m.group(1)), Integer.parseInt(m.group(2)));
		}
	}

	private static class IllegalProgramException extends Exception {
		private static final long serialVersionUID = -7536926360866863689L;

		private final int acc;

		public IllegalProgramException(String message, int acc) {
			super(message);

			this.acc = acc;
		}

		public int acc() {
			return acc;
		}
	}
}
