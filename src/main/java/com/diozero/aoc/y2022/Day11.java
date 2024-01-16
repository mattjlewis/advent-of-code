package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongUnaryOperator;

import org.hipparchus.util.ArithmeticUtils;

import com.diozero.aoc.Day;

public class Day11 extends Day {
	public static void main(String[] args) {
		new Day11().run();
	}

	@Override
	public String name() {
		return "Monkey in the Middle";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Monkey> monkeys = load(input);
		// Note LCM not technically needed for part 1
		final long lcm = monkeys.stream().mapToLong(Monkey::divisor).reduce(1, ArithmeticUtils::lcm);

		for (int round = 1; round <= 20; round++) {
			monkeys.forEach(monkey -> monkey.step(monkeys, 3, lcm));
		}

		return Long.toString(monkeys.stream().mapToLong(Monkey::getInspectionCount).map(i -> -i).sorted().limit(2)
				.map(i -> -i).reduce(1, (a, b) -> a * b));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Monkey> monkeys = load(input);
		// Need the lcm of all divisors to prevent worry level number overflow
		final long lcm = monkeys.stream().mapToLong(Monkey::divisor).reduce(1, ArithmeticUtils::lcm);

		for (int round = 1; round <= 10_000; round++) {
			monkeys.forEach(monkey -> monkey.step(monkeys, 1, lcm));
		}

		return Long.toString(monkeys.stream().mapToLong(Monkey::getInspectionCount).map(i -> -i).sorted().limit(2)
				.map(i -> -i).reduce(1, (a, b) -> a * b));
	}

	private static List<Monkey> load(Path input) throws IOException {
		final List<Monkey> monkeys = new ArrayList<>();

		int id = 0;
		List<Long> items = new ArrayList<>();
		String operation = "";
		int divisor = 0;
		int true_dest = 0;
		int false_dest = 0;
		int line_no = 0;
		for (String line : Files.readAllLines(input)) {
			switch (line_no++) {
			case 0:
				id = Integer.parseInt(line.substring("Monkey ".length(), line.length() - 1));
				break;
			case 1:
				items = Arrays.stream(line.substring("  Starting items: ".length()).split(", ")).map(Long::valueOf)
						.toList();
				break;
			case 2:
				operation = line.substring("  Operation: new = old ".length());
				break;
			case 3:
				divisor = Integer.parseInt(line.substring("  Test: divisible by ".length()));
				break;
			case 4:
				true_dest = Integer.parseInt(line.substring("    If true: throw to monkey ".length()));
				break;
			case 5:
				false_dest = Integer.parseInt(line.substring("    If false: throw to monkey ".length()));
				monkeys.add(new Monkey(id, new ArrayDeque<>(items), parseOperation(operation), divisor, true_dest,
						false_dest, new AtomicInteger()));
				break;
			default:
				line_no = 0;
			}
		}

		return monkeys;
	}

	private static LongUnaryOperator parseOperation(String operation) {
		String[] parts = operation.split(" ");
		char op = parts[0].charAt(0);
		if (parts[1].equals("old")) {
			return switch (op) {
			case '+' -> x -> x + x;
			case '*' -> x -> x * x;
			default -> throw new IllegalArgumentException("Invalid op '" + op + "' in '" + operation + "'");
			};
		}

		long y = Long.parseLong(parts[1]);
		return switch (op) {
		case '+' -> x -> x + y;
		case '*' -> x -> x * y;
		default -> throw new IllegalArgumentException("Invalid op '" + op + "' in '" + operation + "'");
		};
	}

	private static final record Monkey(int id, Deque<Long> items, LongUnaryOperator operation, int divisor,
			int trueDest, int falseDest, AtomicInteger inspectionCount) {
		public void step(List<Monkey> monkeys, int worryLevelDivisor, long lcm) {
			while (!items.isEmpty()) {
				final long worry_level = operation.applyAsLong(items.removeFirst().longValue()) / worryLevelDivisor
						% lcm;
				monkeys.get((worry_level % divisor == 0L) ? trueDest : falseDest).addItem(worry_level);
				inspectionCount.incrementAndGet();
			}
		}

		public long getInspectionCount() {
			return inspectionCount.get();
		}

		private void addItem(long item) {
			items.offerLast(Long.valueOf(item));
		}
	}
}
