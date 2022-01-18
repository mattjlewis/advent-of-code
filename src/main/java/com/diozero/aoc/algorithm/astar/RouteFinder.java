package com.diozero.aoc.algorithm.astar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class RouteFinder<T extends GraphNode> {
	private final Graph<T> graph;
	private final Scorer<T> nextNodeScorer;
	private final Scorer<T> targetScorer;

	public RouteFinder(Graph<T> graph, Scorer<T> nextNodeScorer, Scorer<T> targetScorer) {
		this.graph = graph;
		this.nextNodeScorer = nextNodeScorer;
		this.targetScorer = targetScorer;
	}

	public List<T> findRoute(T a, T b) {
		Queue<RouteNode<T>> open_set = new PriorityQueue<>();
		Map<T, RouteNode<T>> all_nodes = new HashMap<>();

		RouteNode<T> start = new RouteNode<>(a, null, 0d, targetScorer.computeCost(a, b));
		open_set.add(start);
		all_nodes.put(a, start);

		while (!open_set.isEmpty()) {
			RouteNode<T> next = open_set.poll();

			if (next.current().equals(b)) {
				List<T> route = new ArrayList<>();

				// Build the list of routes used to get from a to b
				RouteNode<T> current = next;
				do {
					route.add(0, current.current());
					current = all_nodes.get(current.getPrevious());
				} while (current != null);

				return route;
			}

			graph.getConnections(next.current()).forEach(connection -> {
				double new_score = next.getRouteScore() + nextNodeScorer.computeCost(next.current(), connection);
				RouteNode<T> next_node = all_nodes.getOrDefault(connection, new RouteNode<>(connection));
				all_nodes.put(connection, next_node);

				if (next_node.getRouteScore() > new_score) {
					next_node.setPrevious(next.current());
					next_node.setRouteScore(new_score);
					next_node.setEstimatedScore(new_score + targetScorer.computeCost(connection, b));
					open_set.add(next_node);
				}
			});
		}

		throw new IllegalStateException("No route found");
	}
}