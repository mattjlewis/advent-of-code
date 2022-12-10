package com.diozero.aoc.algorithm.astar;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;
import com.diozero.aoc.algorithm.HaversineScorer;
import com.diozero.aoc.algorithm.LondonUnderground;
import com.diozero.aoc.algorithm.Station;

@SuppressWarnings("static-method")
public class LondonUndergroundAStarTest {
	private static Graph<String, Station> ALL_NODES;

	@BeforeAll
	public static void setup() {
		ALL_NODES = LondonUnderground.getGraph();
	}

	@Test
	public void findRoute() {
		GraphNode<String, Station> from = ALL_NODES.get("Earl's Court");
		GraphNode<String, Station> to = ALL_NODES.get("Angel");

		long start = System.currentTimeMillis();
		GraphNode<String, Station> result = AStarPathFinder.findPath(from, to, HaversineScorer::computeCost);
		long duration = System.currentTimeMillis() - start;
		System.out.format("Route distance: %,.1f km, duration: %,dms%n", Float.valueOf(to.cost() / 1000f), duration);

		Assertions.assertEquals(
				"Earl's Court -> Gloucester Road -> Knightsbridge -> Hyde Park Corner -> Green Park -> Oxford Circus"
						+ " -> Warren Street -> Euston -> King's Cross St. Pancras -> Angel",
				result.path().stream().map(Station::name).collect(Collectors.joining(" -> ")));
	}
}