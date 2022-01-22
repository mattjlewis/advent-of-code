package com.diozero.aoc.algorithm.astar;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.ToIntBiFunction;

import com.diozero.aoc.algorithm.GraphNode;

public class AStarPathFinder {
	public static <K, V> GraphNode<K, V> findRoute(GraphNode<K, V> start, GraphNode<K, V> target,
			ToIntBiFunction<V, V> heuristicFunction) {
		start.updateCostAndHeuristic(0, heuristicFunction.applyAsInt(start.value(), target.value()));

		final Queue<GraphNode<K, V>> open_nodes = new PriorityQueue<>();
		final Set<K> closed_nodes = new HashSet<>();

		open_nodes.offer(start);

		while (!open_nodes.isEmpty()) {
			final GraphNode<K, V> current = open_nodes.poll();

			// Safe to do object equality rather than Object.equals()
			if (current == target) {
				return current;
			}

			for (GraphNode.Neighbour<K, V> neighbour : current.neighbours()) {
				GraphNode<K, V> next = neighbour.node();
				int new_cost = current.cost() + neighbour.cost();

				if (!open_nodes.contains(next) && !closed_nodes.contains(next.id())) {
					next.setParent(current);
					/*-
					 * f(n)        = g(n)    + h(n)
					 * estimate(n) = cost(n) + heuristic(n)
					 */
					next.updateCostAndHeuristic(new_cost, heuristicFunction.applyAsInt(next.value(), target.value()));

					open_nodes.offer(next);
				} else if (new_cost < next.cost()) {
					next.setParent(current);
					next.updateCostAndHeuristic(new_cost, heuristicFunction.applyAsInt(next.value(), target.value()));

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