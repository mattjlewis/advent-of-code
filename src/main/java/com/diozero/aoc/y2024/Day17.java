package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;

public class Day17 extends Day {
	public static void main(String[] args) {
		new Day17().run();
	}

	@Override
	public String name() {
		return "Chronospatial Computer";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Program program = Program.load(input);
		return program.getOutputs().stream().map(i -> i.toString()).collect(Collectors.joining(","));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Program program = Program.load(input);

		Set<Long> possible_a_values = new HashSet<>();
		possible_a_values.add(Long.valueOf(0));

		for (int i = 1; i <= program.instructions.length; i++) {
			final Set<Long> new_possible_a_values = new HashSet<>();
			for (long possible_a : possible_a_values) {
				for (long a = possible_a; a < possible_a + 8; a++) {
					program.registers[0] = a;
					if (program.isValidOutput(i)) {
						new_possible_a_values.add(Long.valueOf(i < program.instructions.length ? a << 3 : a));
					}
				}
			}
			possible_a_values = new_possible_a_values;
		}

		return Long.toString(possible_a_values.stream().mapToLong(Long::longValue).min().orElseThrow());
	}

	private static final record Program(long[] registers, int[] instructions) {
		private static final int REG_A = 0;
		private static final int REG_B = 1;
		private static final int REG_C = 2;
		private static final int ADV = 0;
		private static final int BXL = 1;
		private static final int BST = 2;
		private static final int JNZ = 3;
		private static final int BXC = 4;
		private static final int OUT = 5;
		private static final int BDV = 6;
		private static final int CDV = 7;

		public static Program load(Path input) throws IOException {
			final List<String> lines = Files.readAllLines(input);
			final int a = Integer.parseInt(lines.get(0).split(":")[1].trim());
			final int b = Integer.parseInt(lines.get(1).split(":")[1].trim());
			final int c = Integer.parseInt(lines.get(2).split(":")[1].trim());
			final int[] instructions = Arrays.stream(lines.get(4).split(":")[1].trim().split(","))
					.mapToInt(Integer::parseInt).toArray();

			return new Program(new long[] { a, b, c }, instructions);
		}

		public List<Integer> getOutputs() {
			final List<Integer> outputs = new ArrayList<>();

			long a = registers[REG_A];
			long b = registers[REG_B];
			long c = registers[REG_C];

			int instr_ptr = 0;
			do {
				final int instr = instructions[instr_ptr];
				final int operand = instructions[instr_ptr + 1];
				switch (instr) {
				case ADV -> a >>= comboOperand(operand, a, b, c);
				case BXL -> b ^= operand;
				case BST -> b = comboOperand(operand, a, b, c) % 8;
				case JNZ -> {
					if (a != 0) {
						instr_ptr = operand;
						continue;
					}
				}
				case BXC -> b ^= c;
				case OUT -> outputs.add(Integer.valueOf((int) (comboOperand(operand, a, b, c) % 8)));
				case BDV -> b = a >> comboOperand(operand, a, b, c);
				case CDV -> c = a >> comboOperand(operand, a, b, c);
				default -> throw new IllegalArgumentException("Invalid instruction: " + instr);
				}

				instr_ptr += 2;
			} while (instr_ptr < instructions.length);

			return outputs;
		}

		public boolean isValidOutput(int i) {
			final List<Integer> outputs = getOutputs();
			for (int j = i; j > 0; j--) {
				if (i > outputs.size()
						|| instructions[instructions.length - i] != outputs.get(outputs.size() - i).intValue()) {
					return false;
				}
			}
			return true;
		}

		private static long comboOperand(int operand, long a, long b, long c) {
			return switch (operand) {
			case 0, 1, 2, 3 -> operand;
			case 4 -> a;
			case 5 -> b;
			case 6 -> c;
			default -> throw new IllegalStateException("Unexpected value: " + operand);
			};
		}
	}
}
