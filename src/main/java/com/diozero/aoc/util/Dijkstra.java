package com.diozero.aoc.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Courtesy of <a href="https://www.baeldung.com/java-dijkstra">Baeldung</a>
 */
public class Dijkstra {
	public static void calculateShortestPathFromSource(Node source) {
		source.setDistance(0);

		Set<Node> settled_nodes = new HashSet<>();
		Set<Node> unsettled_nodes = new HashSet<>();
		unsettled_nodes.add(source);

		while (unsettled_nodes.size() != 0) {
			Node current_node = getLowestDistanceNode(unsettled_nodes);
			unsettled_nodes.remove(current_node);

			for (Map.Entry<Node, Integer> adjacency_pair : current_node.getAdjacentNodes().entrySet()) {
				Node adjacent_node = adjacency_pair.getKey();

				// Make sure we haven't already visited this node
				if (!settled_nodes.contains(adjacent_node)) {
					calculateMinimumDistance(adjacent_node, adjacency_pair.getValue().intValue(), current_node);
					unsettled_nodes.add(adjacent_node);
				}
			}

			settled_nodes.add(current_node);
		}
	}

	private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
		Node lowest_distance_node = null;
		int lowest_distance = Integer.MAX_VALUE;
		for (Node node : unsettledNodes) {
			int node_distance = node.getDistance();
			if (node_distance < lowest_distance) {
				lowest_distance = node_distance;
				lowest_distance_node = node;
			}
		}
		return lowest_distance_node;
	}

	private static void calculateMinimumDistance(Node evaluationNode, int edgeWeight, Node sourceNode) {
		int source_distance = sourceNode.getDistance();
		if (source_distance + edgeWeight < evaluationNode.getDistance()) {
			evaluationNode.setDistance(source_distance + edgeWeight);
			LinkedList<Node> shortest_path = new LinkedList<>(sourceNode.getShortestPath());
			shortest_path.add(sourceNode);
			evaluationNode.setShortestPath(shortest_path);
		}
	}
}
