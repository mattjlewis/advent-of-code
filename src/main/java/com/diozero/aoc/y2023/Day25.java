package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;
import com.diozero.aoc.algorithm.dijkstra.Dijkstra;

public class Day25 extends Day {
	public static void main(String[] args) {
		new Day25().run();
	}

	@Override
	public String name() {
		return "Snowverload";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Graph<String, String> graph = load(input);
		System.out.println(graph.nodes());
		System.out.println(graph.nodes().size());

		System.out.println(1);
		final GraphNode<String, String> start = graph.nodes().stream().findAny().orElseThrow();
		System.out.println("Picked " + start.id() + " as the random start node");
		Dijkstra.findRoutes(start);
		System.out.println(2);
		graph.nodes().forEach((node -> System.out.println("node " + node.id() + ", cost " + node.cost()
				+ ", neighbours: " + node.neighbours().size() + " - "
				+ node.neighboursStream().map(n -> n.node().id() + "-" + n.cost()).collect(Collectors.joining(", ")))));
		final Map<String, Deque<String>> paths = graph.nodes().stream()
				.collect(Collectors.toMap(GraphNode::id, GraphNode::path));
		System.out.println(paths);

		reduce(graph);

		return Integer.toString(0);
	}

	private static int reduce(final Graph<String, String> graph) {
		while (false && graph.nodes().size() > 2) {

		}
		return 0;
	}

	@Override
	public String part2(final Path input) throws IOException {
		return NOT_APPLICABLE;
	}

	private static Graph<String, String> load(Path input) throws IOException {
		final Map<String, List<String>> data = Files.lines(input).map(line -> line.split(": "))
				.collect(Collectors.toMap(parts -> parts[0], parts -> Arrays.stream(parts[1].split(" ")).toList()));

		final Graph<String, String> graph = new Graph<>();
		for (Map.Entry<String, List<String>> entry : data.entrySet()) {
			final GraphNode<String, String> node = graph.getOrPut(entry.getKey(), Function.identity());
			entry.getValue().forEach(
					neighbour -> node.addBiDirectionalNeighbour(graph.getOrPut(neighbour, Function.identity()), 1));
		}

		return graph;
	}

	private static record Component(String id, List<String> connections) {
		public static Component parse(String line) {
			final String[] parts = line.split(": ");
			return new Component(parts[0], List.of(parts[1].split(" ")));
		}

		@Override
		public String toString() {
			return id;
		}
	}
}
