package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point3D;
import com.diozero.aoc.util.Tuple2;

public class Day8 extends Day {
	public static void main(String[] args) {
		new Day8().run();
	}

	@Override
	public String name() {
		return "Playground";
	}

	@Override
	public String part1(final Path input) throws IOException {
		// Pairs sorted by distance
		final Queue<Pair> pairs = loadData(input).second();

		final List<Set<Point3D>> circuits = new ArrayList<>();
		// Only process the 1000 pairs of junction boxes which are closest together
		for (int i = 0; i < (isSample() ? 10 : 1000) && !pairs.isEmpty(); i++) {
			pairs.remove().connect(circuits);
		}

		return Integer.toString(circuits.stream().mapToInt(c -> -c.size()).sorted().map(i -> i * -1).limit(3).reduce(1,
				(i, j) -> i * j));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Tuple2<List<Point3D>, Queue<Pair>> puzzle = loadData(input);
		final List<Point3D> junction_boxes = puzzle.first();
		// Pairs sorted by distance
		final Queue<Pair> pairs = puzzle.second();

		final List<Set<Point3D>> circuits = new ArrayList<>();
		// Process pairs until all junction boxes are in one circuit
		Pair last_pair;
		do {
			last_pair = pairs.remove().connect(circuits);
		} while (circuits.size() != 1 || circuits.get(0).size() != junction_boxes.size());

		return Long.toString(last_pair.a.x() * (long) last_pair.b.x());
	}

	private static Tuple2<List<Point3D>, Queue<Pair>> loadData(Path input) throws IOException {
		final List<Point3D> junction_boxes = Files.lines(input).map(Point3D::parse).toList();

		final Queue<Pair> pairs = new PriorityQueue<>();
		for (int i = 0; i < junction_boxes.size() - 1; i++) {
			for (int j = i + 1; j < junction_boxes.size(); j++) {
				pairs.add(Pair.create(junction_boxes.get(i), junction_boxes.get(j)));
			}
		}

		return new Tuple2<>(junction_boxes, pairs);
	}

	private static record Pair(Point3D a, Point3D b, double distance) implements Comparable<Pair> {
		public static Pair create(Point3D a, Point3D b) {
			return new Pair(a, b, a.distance(b));
		}

		@Override
		public int compareTo(Pair o) {
			return Comparator.comparingDouble(Pair::distance).compare(this, o);
		}

		public Set<Point3D> toSet() {
			return Stream.of(a, b).collect(Collectors.toSet());
		}

		public Pair connect(final List<Set<Point3D>> circuits) {
			// Are any of these junction boxes already in a circuit?
			final Optional<Set<Point3D>> circuit_a = circuits.stream().filter(c -> c.contains(a)).findFirst();
			final Optional<Set<Point3D>> circuit_b = circuits.stream().filter(c -> c.contains(b)).findFirst();
			if (circuit_a.isEmpty()) {
				if (circuit_b.isEmpty()) {
					// Neither are in a circuit - create a new circuit with a and b
					circuits.add(toSet());
				} else {
					// b is in a circuit - add a to circuit_b
					circuit_b.get().add(a);
				}
			} else if (circuit_b.isEmpty()) {
				// a is in a circuit and b isn't - add b to circuit_a
				circuit_a.get().add(b);
			} else {
				final Set<Point3D> ca = circuit_a.get();
				final Set<Point3D> cb = circuit_b.get();
				// a and b are both in circuits - merge them if they are different, otherwise do nothing
				if (!ca.equals(cb)) {
					ca.addAll(cb);
					circuits.remove(cb);
				}
			}

			return this;
		}
	}
}
