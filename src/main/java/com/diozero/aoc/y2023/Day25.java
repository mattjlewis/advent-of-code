package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.diozero.aoc.Day;

public class Day25 extends Day {
	private static Random RANDOM = new Random();

	public static void main(String[] args) {
		new Day25().run();
	}

	@Override
	public String name() {
		return "Snowverload";
	}

	@Override
	public String part1(final Path input) throws IOException {
		while (true) {
			final Graph graph = Graph.build(input);

			if (graph.reduce() == 3) {
				return Integer.toString(graph.components().stream().mapToInt(component -> component.reduced.get())
						.reduce(1, (a, b) -> a * b));
			}
		}
	}

	@Override
	public String part2(final Path input) throws IOException {
		return NOT_APPLICABLE;
	}

	private static record Graph(List<Component> components, Map<String, Component> componentMap) {
		private static Graph build(Path input) throws IOException {
			final Graph graph = new Graph();
			for (String line : Files.readAllLines(input)) {
				final String[] parts = line.split(": ");
				final Component component = graph.getComponent(parts[0]);
				final String[] connections = parts[1].split(" ");
				for (String connection : connections) {
					component.addConnection(graph.getComponent(connection));
				}
			}
			return graph;
		}

		public Graph() {
			this(new ArrayList<>(), new HashMap<>());
		}

		public Component getComponent(String name) {
			Component component = componentMap.get(name);
			if (component == null) {
				component = componentMap.computeIfAbsent(name, Component::new);
				components.add(component);
			}
			return component;
		}

		public int reduce() {
			while (components.size() > 2) {
				final Component node1 = components.get(RANDOM.nextInt(components.size()));
				final Component node2 = node1.connections.get(RANDOM.nextInt(node1.connections.size()));

				node1.connections.addAll(node2.connections);

				for (Component node : node2.connections) {
					node.connections.remove(node2);
					node.connections.add(node1);
				}

				node1.reduced.addAndGet(node2.reduced.get());
				components.remove(node2);
				node1.connections.removeIf(n -> n == node1);
			}

			return components.getFirst().connections.size();
		}
	}

	private static record Component(String name, List<Component> connections, AtomicInteger reduced) {
		public Component(String name) {
			this(name, new ArrayList<>(), new AtomicInteger(1));
		}

		public void addConnection(Component other) {
			if (!connections.contains(other)) {
				connections.add(other);
				other.addConnection(this);
			}
		}
	}
}
