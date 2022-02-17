package com.diozero.aoc.algorithm.dijkstra;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.diozero.aoc.algorithm.GraphNode;

/**
 * Courtesy of <a href="https://www.baeldung.com/java-dijkstra">Baeldung</a>
 *
 * https://github.com/eugenp/tutorials/blob/master/algorithms-miscellaneous-2/src/main/java/com/baeldung/algorithms/ga/dijkstra/Dijkstra.java
 *
 * https://stackabuse.com/graphs-in-java-dijkstras-algorithm/
 */
public class Dijkstra {
	private Dijkstra() {
	}

	public static <K, V> void findRoutes(GraphNode<K, V> start) {
		findRoutes(start, Optional.empty(), GraphNode::neighboursStream);
	}

	public static <K, V> boolean findPath(GraphNode<K, V> start, GraphNode<K, V> target) {
		return findRoutes(start, Optional.of(target), GraphNode::neighboursStream);
	}

	public static <K, V> boolean findPath(GraphNode<K, V> start, GraphNode<K, V> target,
			Function<GraphNode<K, V>, Stream<GraphNode.Neighbour<K, V>>> getNeighboursFunction) {
		return findRoutes(start, Optional.of(target), getNeighboursFunction);
	}

	public static <K, V> boolean findRoutes(GraphNode<K, V> start, Optional<GraphNode<K, V>> target,
			Function<GraphNode<K, V>, Stream<GraphNode.Neighbour<K, V>>> getNeighboursFunction) {
		final Queue<GraphNode<K, V>> open_nodes = new PriorityQueue<>();
		final Set<K> closed_nodes = new HashSet<>();

		start.updateCost(0);
		open_nodes.offer(start);

		while (!open_nodes.isEmpty()) {
			// Get the node with the lowest cost from the set of open nodes
			final GraphNode<K, V> current = open_nodes.poll();
			if (target.filter(node -> node == current).isPresent()) {
				return true;
			}

			getNeighboursFunction.apply(current).forEach(neighbour -> {
				final GraphNode<K, V> next = neighbour.node();
				final int new_cost = current.cost() + neighbour.cost();

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
			});

			closed_nodes.add(current.id());
		}

		if (target.isPresent()) {
			return false;
		}
		return true;
	}

	public static <K, V> void findRoutes(GraphNode<K, V> start, Collection<K> targets,
			Function<GraphNode<K, V>, Stream<GraphNode.Neighbour<K, V>>> getNeighboursFunction) {
		final Queue<GraphNode<K, V>> open_nodes = new PriorityQueue<>();
		final Set<K> closed_nodes = new HashSet<>();

		start.updateCost(0);
		open_nodes.offer(start);

		while (!open_nodes.isEmpty()) {
			// Get the node with the lowest cost from the set of open nodes
			final GraphNode<K, V> current = open_nodes.poll();
			if (targets.remove(current.id()) && targets.isEmpty()) {
				return;
			}

			getNeighboursFunction.apply(current).forEach(neighbour -> {
				final GraphNode<K, V> next = neighbour.node();
				final int new_cost = current.cost() + neighbour.cost();

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
			});

			closed_nodes.add(current.id());
		}
	}
}
