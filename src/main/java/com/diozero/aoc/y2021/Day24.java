package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.diozero.aoc.Day;

/*-
 * Maximum: 99,394,899,891,971
 * Minimum: 92,171,126,131,911
 * Wrong:   92,171,126,131,811
 *                   |   Maximum    |   Minimum    |    Wrong
 *-------------------+--------------+--------------+-------------
 * Z  Div   X  Y inc | Input      Z | Input      Z | Input      Z
 *-------------------+--------------+--------------+-------------
 *  1   1  11      6 |   9       15 |   9       15 |   9       15
 *  2   1  13     14 |   9      413 |   2      406 |   2      406
 *  3   1  15     14 |   3   10,755 |   1   10,571 |   1   10,571
 *  4  26  -8     10 |   9      413 |   7      406 |   7      406
 *  5   1  13      9 |   4   10,751 |   1   10,566 |   1   10,566
 *  6   1  15     12 |   8  279,546 |   1  274,729 |   1  274,729
 *  7  26 -11      8 |   9   10,751 |   2   10,566 |   2   10,566
 *  8  26  -4     13 |   9      413 |   6      406 |   6      406
 *  9  26 -15     12 |   8       15 |   1       15 |   1       15
 * 10   1  14      6 |   9      405 |   3      399 |   3      399
 * 11   1  14      9 |   1   10,540 |   1   10,384 |   1   10,384
 * 12  26  -1     15 |   9      405 |   9      399 |   8   10,397
 * 13  26  -8      4 |   7       15 |   1       15 |   1   10,379
 * 14  26 -14     10 |   1        0 |   1        0 |   1   10,385
 *
 * If Div != 1 then prev Z % 26 + X must equal Input.
 * E.g. Max row 4, prev z = 10,755; 10,755 % 26 = 17; 17 + -8 = 9 == Input (9)
 * E.g. Wrong row 12, prev z = 10,384; 10,384 % 26 = 10; 10 + -1 = 9 != Input (8)
 */
public class Day24 extends Day {
	public static void main(String[] args) {
		new Day24().run();
	}

	@Override
	public String name() {
		return "Arithmetic Logic Unit";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final byte[][] alu_variables = loadData(input);

		// Find the largest valid input value
		try {
			execute(alu_variables, 0, 0, 0, false);
		} catch (Result r) {
			return Long.toString(r.getModelNumber());
		}

		return "";
	}

	@Override
	public String part2(final Path input) throws IOException {
		final byte[][] alu_variables = loadData(input);

		// Find the smallest valid input value
		try {
			execute(alu_variables, 0, 0, 0, true);
		} catch (Result r) {
			return Long.toString(r.getModelNumber());
		}

		return "";
	}

	private static byte[][] loadData(final Path input) throws IOException {
		final List<Instruction> alu = Files.lines(input).map(Instruction::parse).toList();

		/*
		 * The ALU has 14 repeating blocks each with 18 instructions, one block for each
		 * of the 14 model number digits. Only the Z variable carries over between
		 * instruction blocks; W, X, and Y all get reset.
		 *
		 * Note the only difference between ALU blocks is the literal values on lines 5,
		 * 6 and 16.
		 */
		final List<List<Instruction>> alu_blocks = new ArrayList<>();
		for (int i = 0; i < 14; i++) {
			alu_blocks.add(alu.subList(i * 18, (i + 1) * 18));
		}

		// Only the literal values in instructions 4, 5 and 15 are required from each
		// ALU block
		byte[][] alu_variables = alu_blocks.stream().map(block -> new byte[] { block.get(4).valB.byteValue(),
				block.get(5).valB.byteValue(), block.get(15).valB.byteValue() }).toArray(byte[][]::new);

		return alu_variables;
	}

	private static void execute(final byte[][] aluVariables, final int n, final int z, final long modelNumber,
			boolean incrementing) throws Result {
		if (n == 14) {
			if (z == 0) {
				throw new Result(modelNumber);
			}
			return;
		}

		if (incrementing) {
			for (int input = 1; input < 10; input++) {
				// Previous Z % 26 + X must be == input if Div != 1, see the table above
				if (aluVariables[n][0] != 1 && z % 26 + aluVariables[n][1] != input) {
					continue;
				}

				execute(aluVariables, n + 1, runAlu(aluVariables[n], z, input), modelNumber * 10 + input, incrementing);
			}
		} else {
			for (int input = 9; input > 0; input--) {
				// Previous Z % 26 + X must be == input if Div != 1, see the table above
				if (aluVariables[n][0] != 1 && z % 26 + aluVariables[n][1] != input) {
					continue;
				}

				execute(aluVariables, n + 1, runAlu(aluVariables[n], z, input), modelNumber * 10 + input, incrementing);
			}
		}
	}

	private static int runAlu(final byte[] aluBlockVariables, final int z, final int input) {
		// Manually interpreted the blocks of instructions down to the following
		// aluBlockVariables[0] is only either 1 or 26
		int new_z;
		if ((z % 26 + aluBlockVariables[1]) == input) {
			new_z = z / 26;
		} else {
			new_z = 26 * z + input + aluBlockVariables[2];
		}
		return new_z;
	}

	private static class Result extends Exception {
		private static final long serialVersionUID = -5965153570497470667L;

		private long modelNumber;

		public Result(long modelNumber) {
			this.modelNumber = modelNumber;
		}

		public long getModelNumber() {
			return modelNumber;
		}
	}

	enum Operation {
		inp, add, mul, div, mod, eql;
	}

	static class Instruction {
		public static Instruction parse(String line) {
			String[] parts = line.split(" ");

			Operation op = Operation.valueOf(parts[0]);
			byte a = (byte) (parts[1].charAt(0) - 'w');
			String b = parts.length > 2 ? parts[2] : null;

			if (b == null) {
				return new Instruction(op, a);
			}

			return new Instruction(op, a, b);
		}

		private Operation op;
		private byte varA;
		private Byte varB;
		Integer valB;

		public Instruction(Operation op, byte varA) {
			this.op = op;
			this.varA = varA;
		}

		public Instruction(Operation op, byte varA, String b) {
			this.op = op;
			this.varA = varA;

			if (Character.isDigit(b.charAt(b.length() - 1))) {
				valB = Integer.valueOf(b);
			} else {
				varB = Byte.valueOf((byte) (b.charAt(0) - 'w'));
			}
		}
	}
}
