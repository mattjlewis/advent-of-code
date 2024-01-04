package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;

public class Day16 extends Day {
	private static final int TIME_TO_ERRUPTION = 30;

	public static void main(String[] args) {
		new Day16().run();
	}

	@Override
	public String name() {
		return "Proboscidea Volcanium";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Map<String, Valve> valves = Files.lines(input).map(Valve::parse)
				.collect(Collectors.toMap(Valve::name, Function.identity()));
		System.out.println(valves);

		final Graph<String, Valve> graph = new Graph<>();
		valves.values().forEach(valve -> addToGraph(valves, graph, valve));

		return null;
	}

	private static void addToGraph(final Map<String, Valve> valves, final Graph<String, Valve> graph,
			final Valve valve) {
		final GraphNode<String, Valve> node = graph.getOrPut(valve, Valve::name);
		valve.tunnels().forEach(tunnel -> node.addNeighbour(graph.getOrPut(valves.get(tunnel), Valve::name), 2));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return null;
	}

	private static record Valve(String name, AtomicBoolean opened, int flowRate, List<String> tunnels) {

		// Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
		private static final Pattern PATTERN = Pattern
				.compile("Valve (\\w+) has flow rate=(\\d+); tunnel(?:s?) lead(?:s?) to valve(?:s?) (.*)$");

		public static Valve parse(String s) {
			Matcher mr = PATTERN.matcher(s);
			if (!mr.matches()) {
				throw new IllegalArgumentException("'" + s + "' doesn't match pattern '" + PATTERN.pattern() + "'");
			}

			return new Valve(mr.group(1), new AtomicBoolean(), Integer.parseInt(mr.group(2)),
					Arrays.asList(mr.group(3).split(", ")));
		}
	}
}
