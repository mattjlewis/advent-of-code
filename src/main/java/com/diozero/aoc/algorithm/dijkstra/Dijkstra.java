package com.diozero.aoc.algorithm.dijkstra;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
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
		final Queue<GraphNode<K, V>> open_nodes = new PriorityQueue<>();
		final Set<K> closed_nodes = new HashSet<>();

		start.updateCost(0);
		open_nodes.add(start);

		while (!open_nodes.isEmpty()) {
			// Get the node with the lowest cost from the set of open nodes
			final GraphNode<K, V> current = open_nodes.poll();

			if (current == target) {
				return;
			}

			for (GraphNode.Neighbour<K, V> neighbour : current.neighbours()) {
				GraphNode<K, V> next = neighbour.node();
				int new_cost = current.cost() + neighbour.cost();

				if (!closed_nodes.contains(next.id()) && !open_nodes.contains(next)) {
					next.setParent(current);
					next.updateCost(new_cost);

					open_nodes.offer(next);
				} else if (new_cost < next.cost()) {
					next.setParent(current);
					next.updateCost(new_cost);

					if (closed_nodes.contains(next.id())) {
						closed_nodes.remove(next.id());
						open_nodes.offer(next);
					}
				}
			}

			closed_nodes.add(current.id());
		}

		throw new IllegalStateException("No route found");
	}
}
