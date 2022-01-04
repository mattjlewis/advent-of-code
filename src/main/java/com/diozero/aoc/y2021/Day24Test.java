package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.diozero.aoc.y2021.Day24.Instruction;

public class Day24Test {
	public static void main(String[] args) throws IOException {
		List<Instruction> alu = Files.lines(Path.of("src/main/resources/input/2021/day24.txt")).map(Instruction::parse)
				.toList();

		System.out.println(9945 / 26);
		System.out.println(9945 % 26);
		System.out.println((9945 / 26) % 26);

		// The ALU has 14 blocks each with 18 instructions; one block for each input.
		// Only the Z variable carries over between instruction blocks; W, X, and Y all
		// get reset.
		// The only difference between blocks is the literal values on lines 5, 6 and 16
		List<List<Instruction>> alu_blocks = new ArrayList<>();
		for (int i = 0; i < 14; i++) {
			alu_blocks.add(alu.subList(i * 18, (i + 1) * 18));
		}

		/*-
		for (int input = 1; input < 10; input++) {
			for (int block = 0; block < 14; block++) {
				for (int z = -300000; z < 300000; z++) {
					int a = runAlu(alu_blocks.get(block), z, input);
					int b = run(alu_blocks.get(block), z, input);
					if (a != b) {
						System.out.format("Error input: %d, block: %d, z: %d. %d != %d%n", input, block, z, a, b);
					}
				}
			}
		}
		*/

		System.out.println("mod " + 12_999_987 % 1000);
		System.out.println("mod " + 91_999_999 % 1000);
		System.out.println("mod " + 92_999_999 % 1000);
		System.out.println("mod " + 95_999_999 % 1000);

		long val = 99394899891971L;
		final Queue<Byte> input = new ArrayDeque<>(14);
		Long.toString(val).chars().forEach(ch -> input.add(Byte.valueOf((byte) (ch - 48))));

		int z = 0;
		for (int block = 0; block < 14; block++) {
			z = runAlu(alu_blocks.get(block), z, input.remove().byteValue());
			System.out.println(z);
		}
		System.out.println(z);
	}

	private static int runAlu(final List<Instruction> aluBlock, int z, final int input) {
		int new_z = z / aluBlock.get(4).valB.intValue();
		if (((z % 26) + aluBlock.get(5).valB.intValue()) != input) {
			new_z = 26 * new_z + input + aluBlock.get(15).valB.intValue();
		}
		return new_z;
	}

	private static int run(List<Instruction> aluBlock, int z, int input) {
		int w = input;
		int x = z % 26;
		z = z / aluBlock.get(4).valB.intValue();
		x = x + aluBlock.get(5).valB.intValue();
		x = x == w ? 1 : 0;
		x = x == 0 ? 1 : 0;
		int y = 25;
		y = y * x;
		y = y + 1;
		z = z * y;
		y = w;
		y = y + aluBlock.get(15).valB.intValue();
		y = y * x;
		z = z + y;
		return z; // pass z forward
	}
}
