package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.diozero.aoc.AocBase;

public class Day24 extends AocBase {
	public static void main(String[] args) {
		new Day24().run();
	}

	private static List<List<Instruction>> loadData(final Path input) throws IOException {
		final List<Instruction> alu = Files.lines(input).map(Instruction::parse).toList();

		/*
		 * The ALU has 14 blocks each with 18 instructions; one block for each input.
		 * Only the Z variable carries over between instruction blocks; W, X, and Y all
		 * get reset. Therefore we can calculate the ALU output once for each input
		 * digit and previous Z value.
		 *
		 * Note the only difference between ALU blocks is the literal values on lines 5,
		 * 6 and 16.
		 */
		// Note only actually use the literal values in blocks 4, 5 and 15 - could
		// simply extract these values
		final List<List<Instruction>> alu_blocks = new ArrayList<>();
		for (int i = 0; i < 14; i++) {
			alu_blocks.add(alu.subList(i * 18, (i + 1) * 18));
		}

		return alu_blocks;
	}

	@Override
	public long part1(final Path input) throws IOException {
		final List<List<Instruction>> alu_blocks = loadData(input);

		// Find the largest valid input value
		try {
			execute(alu_blocks, 0, 0, 0, false);
		} catch (Result r) {
			return r.getModelNumber();
		}

		return -1;
	}

	@Override
	public long part2(final Path input) throws IOException {
		final List<List<Instruction>> alu_blocks = loadData(input);

		// Find the smallest valid input value
		try {
			execute(alu_blocks, 0, 0, 0, true);
		} catch (Result r) {
			return r.getModelNumber();
		}

		return -1;
	}

	private static void execute(final List<List<Instruction>> aluBlocks, final int n, final int z,
			final long modelNumber, boolean incrementing) throws Result {
		if (n == 14) {
			if (z == 0) {
				throw new Result(modelNumber);
			}
			if (modelNumber % 100_000_000L == 99_999_999) {
				System.out.println("Model number " + modelNumber + " is invalid (z = " + z + ")");
			}
			return;
		}

		// Bail out if there is no way for z to go back to 0
		if (n > 8 && z > 26 * 26 * 26 * 26) {
			return;
		}

		if (incrementing) {
			for (int input = 1; input < 10; input++) {
				// Pass the Z value to the next ALU block calculation
				execute(aluBlocks, n + 1, runAlu(aluBlocks.get(n), z, input), modelNumber * 10 + input, incrementing);
			}
		} else {
			for (int input = 9; input > 0; input--) {
				// Pass the Z value to the next ALU block calculation
				execute(aluBlocks, n + 1, runAlu(aluBlocks.get(n), z, input), modelNumber * 10 + input, incrementing);
			}
		}
	}

	private static int runAlu(final List<Instruction> aluBlock, final int z, final int input) {
		// aluBlock.get(4).valB.intValue() is only either 1 or 26
		int new_z;
		if (((z % 26) + aluBlock.get(5).valB.intValue()) != input) {
			new_z = 26 * z / aluBlock.get(4).valB.intValue() + input + aluBlock.get(15).valB.intValue();
		} else {
			new_z = z / aluBlock.get(4).valB.intValue();
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
