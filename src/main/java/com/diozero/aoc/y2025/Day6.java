package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.MathematicalOperator;
import com.diozero.aoc.util.MatrixUtil;
import com.diozero.aoc.util.StringUtil;

public class Day6 extends Day {
	public static void main(String[] args) {
		new Day6().run();
	}

	@Override
	public String name() {
		return "Trash Compactor";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<String> lines = Files.readAllLines(input);

		final List<MathematicalOperator> operators = Arrays.stream(lines.removeLast().trim().split("\s+"))
				.map(MathematicalOperator::of).toList();

		// Transpose the number 2d array
		final long[][] numbers = MatrixUtil.transpose(
				lines.stream().map(line -> Arrays.stream(line.trim().split("\s+")).mapToLong(Long::valueOf).toArray())
						.toArray(long[][]::new));

		return Long.toString(
				IntStream.range(0, operators.size()).mapToLong(i -> operators.get(i).apply(numbers[i])).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<String> lines = Files.readAllLines(input);

		final List<MathematicalOperator> operators = Arrays.stream(lines.removeLast().trim().split("\s+"))
				.map(MathematicalOperator::of).toList().reversed();

		// Treat the numbers as a grid of characters - transpose them to match Cephalopod
		// mathematics which is written right-to-left in columns
		final List<List<Long>> data = Arrays
				.stream(MatrixUtil.transpose(
						lines.stream().map(StringUtil::reverse).map(String::toCharArray).toArray(char[][]::new)))
				.map(String::new).map(String::trim).map(s -> s.isEmpty() ? null : Long.valueOf(s))
				.gather(new ListSplitterGatherer<Long>()).toList();

		return Long.toString(
				IntStream.range(0, operators.size()).mapToLong(i -> operators.get(i).applyAsLong(data.get(i))).sum());
	}

	private static class ListSplitterGatherer<T> implements Gatherer<T, Deque<T>, List<T>> {
		@Override
		public Supplier<Deque<T>> initializer() {
			return ArrayDeque::new;
		}

		@Override
		public Integrator<Deque<T>, T, List<T>> integrator() {
			return new Integrator<>() {
				@Override
				public boolean integrate(Deque<T> state, T element, Downstream<? super List<T>> downstream) {
					if (element == null) {
						downstream.push(new ArrayList<>(state));
						state.clear();
					} else {
						state.addLast(element);
					}
					return true;
				}
			};
		}

		@Override
		public BiConsumer<Deque<T>, Downstream<? super List<T>>> finisher() {
			return (state, downstream) -> {
				downstream.push(new ArrayList<>(state));
			};
		}
	}
}
