package com.diozero.aoc.algorithm.dijkstra;

import java.util.HashSet;
import java.util.Set;

import com.diozero.aoc.algorithm.GraphNode;

/**
 * Courtesy of <a href="https://www.baeldung.com/java-dijkstra">Baeldung</a>
 *
 * https://github.com/eugenp/tutorials/blob/master/algorithms-miscellaneous-2/src/main/java/com/baeldung/algorithms/ga/dijkstra/Dijkstra.java
 *
 * https://stackabuse.com/graphs-in-java-dijkstras-algorithm/
 */
public class Dijkstra {
	public static <K, V> void findRoute(GraphNode<K, V> start, GraphNode<K, V> target) {
		start.updateCost(0);

		Set<GraphNode<K, V>> open_nodes = new HashSet<>();
		// Queue<GraphNode<K, V>> open_nodes = new PriorityQueue<>();
		Set<GraphNode<K, V>> closed_nodes = new HashSet<>();

		open_nodes.add(start);

		while (!open_nodes.isEmpty()) {
			final GraphNode<K, V> current = open_nodes.stream().min(GraphNode::compareTo).orElseThrow();
			open_nodes.remove(current);
			// FIXME Can this be optimised by using a PriorityQueue instead?!
			// LondonUndergroundDijkstraTest works with a PrirityQueue, Day15 goes into an
			// infinite loop...
			// GraphNode<K, V> current = open_nodes.poll();

			if (current == target) {
				return;
			}

			for (GraphNode.Neighbour<K, V> neighbour : current.neighbours()) {
				GraphNode<K, V> next = neighbour.node();

				// Make sure we haven't already visited this node
				if (!closed_nodes.contains(next)) {
					int new_cost = current.cost() + neighbour.cost();

					if (new_cost < next.cost()) {
						next.updateCost(new_cost);
						next.setParent(current);
					}

					open_nodes.add(next);
				}
			}

			closed_nodes.add(current);
		}
	}
}
