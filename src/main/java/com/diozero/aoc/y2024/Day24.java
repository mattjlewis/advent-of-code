package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;

public class Day24 extends Day {
	public static void main(String[] args) {
		new Day24().run();
	}

	@Override
	public String name() {
		return "Crossed Wires";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Puzzle puzzle = Puzzle.load(input);

		return Long.toString(Long.parseLong(puzzle.gates.keySet().stream().filter(wire -> wire.startsWith("z"))
				.sorted(Comparator.reverseOrder())
				.map(wire -> computeValue(wire, puzzle.gates, puzzle.wires) ? "1" : "0").collect(Collectors.joining()),
				2));
	}

	private static boolean computeValue(String wire, Map<String, Gate> gates, Map<String, Boolean> wires) {
		// Is there a value for wire?
		if (wires.containsKey(wire)) {
			return wires.get(wire).booleanValue();
		}

		final Gate gate = gates.get(wire);
		final boolean result = gate.operator.apply(computeValue(gate.wire1, gates, wires),
				computeValue(gate.wire2, gates, wires));
		wires.put(gate.output, Boolean.valueOf(result));

		return result;
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Puzzle puzzle = Puzzle.load(input);

		final long num_z_wires = puzzle.gates.keySet().stream().filter(wire -> wire.startsWith("z")).count();
		final List<String> swapped_gates = new ArrayList<>();

		String c = "", x_xor_y = "", x_and_y = "";
		int i = 0;
		while (i < num_z_wires) {
			final String x_i = String.format("x%02d", Integer.valueOf(i));
			final String y_i = String.format("y%02d", Integer.valueOf(i));
			final String z_i = String.format("z%02d", Integer.valueOf(i));

			String z;
			if (i == 0) {
				z = findGate(puzzle.gates, x_i, Operator.XOR, y_i);
			} else {
				c = i == 1 ? x_and_y
						: findGate(puzzle.gates, x_and_y, Operator.OR,
								findGate(puzzle.gates, c, Operator.AND, x_xor_y));
				x_xor_y = findGate(puzzle.gates, x_i, Operator.XOR, y_i);
				z = findGate(puzzle.gates, x_xor_y, Operator.XOR, c);
			}

			x_and_y = findGate(puzzle.gates, x_i, Operator.AND, y_i);

			if (z == null || !z_i.equals(z)) {
				final List<String> faultyGates = z == null ? List.of(x_and_y, x_xor_y) : List.of(z, z_i);
				swapGates(puzzle.gates, faultyGates.get(0), faultyGates.get(1));
				swapped_gates.addAll(faultyGates);

				if (swapped_gates.size() >= 8) {
					break;
				}

				c = z = x_xor_y = x_and_y = "";
				i = 0;

				continue;
			}

			i++;
		}

		return swapped_gates.stream().sorted().collect(Collectors.joining(","));
	}

	private static void swapGates(Map<String, Gate> gates, String g1, String g2) {
		final Gate tmp = gates.get(g1);
		gates.put(g1, gates.get(g2));
		gates.put(g2, tmp);
	}

	private static String findGate(Map<String, Gate> gates, String op1, Operator op, String op2) {
		return gates.entrySet().stream().filter(e -> e.getValue().compatible(op1, op, op2)).map(Map.Entry::getKey)
				.findFirst().orElse(null);
	}

	private static record Puzzle(Map<String, Boolean> wires, Map<String, Gate> gates) {
		public static Puzzle load(Path input) throws IOException {
			final Map<String, Boolean> wires = new HashMap<>();
			final Map<String, Gate> gates = new HashMap<>();

			boolean reading_wires = true;
			for (String line : Files.readAllLines(input)) {
				if (line.isBlank()) {
					reading_wires = false;
					continue;
				}
				if (reading_wires) {
					final String[] parts = line.split(": ");
					wires.put(parts[0], Boolean.valueOf(parts[1].equals("1")));
				} else {
					final Gate gate = Gate.parse(line);
					gates.put(gate.output, gate);
				}
			}

			return new Puzzle(wires, gates);
		}
	}

	private static record Gate(String wire1, String wire2, Operator operator, String output) {
		public static Gate parse(String line) {
			final String[] parts = line.split(" ");
			return new Gate(parts[0], parts[2], Operator.of(parts[1]), parts[4]);
		}

		public boolean compatible(String w1, Operator op, String w2) {
			return op == operator && (w1.equals(wire1) && w2.equals(wire2) || w1.equals(wire2) && w2.equals(wire1));
		}
	}

	private enum Operator {
		AND, OR, XOR;

		public boolean apply(boolean b1, boolean b2) {
			return switch (this) {
			case AND -> b1 & b2;
			case OR -> b1 | b2;
			case XOR -> b1 ^ b2;
			};
		}

		public static Operator of(String s) {
			return switch (s) {
			case "AND" -> AND;
			case "OR" -> OR;
			case "XOR" -> XOR;
			default -> throw new IllegalArgumentException("Unexpected value: " + s);
			};
		}
	}
}
