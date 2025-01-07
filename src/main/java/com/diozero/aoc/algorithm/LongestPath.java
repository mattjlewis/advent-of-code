package com.diozero.aoc.algorithm;

import java.util.Deque;
import java.util.stream.Collectors;

import com.diozero.aoc.geometry.Point2D;

// https://www.geeksforgeeks.org/find-longest-path-directed-acyclic-graph/
public class LongestPath {
	// Driver program to test
	public static void main(String args[]) {
		test1();
		test2();
	}

	private static void test1() {
		// Create a graph given in the above diagram.
		// Here vertex numbers are 0, 1, 2, 3, 4, 5 with
		// following mappings:
		// 0=r, 1=s, 2=t, 3=x, 4=y, 5=z
		final Graph<String, Point2D> graph = new Graph<>();
		final GraphNode<String, Point2D> r = graph.getOrPut("r", new Point2D(0, 0));
		final GraphNode<String, Point2D> s = graph.getOrPut("s", new Point2D(1, 0));
		final GraphNode<String, Point2D> t = graph.getOrPut("t", new Point2D(2, 0));
		final GraphNode<String, Point2D> x = graph.getOrPut("x", new Point2D(3, 0));
		final GraphNode<String, Point2D> y = graph.getOrPut("y", new Point2D(4, 0));
		final GraphNode<String, Point2D> z = graph.getOrPut("z", new Point2D(5, 0));
		r.addNeighbour(s, 5);
		r.addNeighbour(t, 3);

		s.addNeighbour(t, 2);
		s.addNeighbour(x, 6);

		t.addNeighbour(x, 7);
		t.addNeighbour(y, 4);
		t.addNeighbour(z, 2);
		// t.addNeighbour(r, 12);

		x.addNeighbour(y, -1);
		x.addNeighbour(z, 1);

		y.addNeighbour(z, -2);

		final GraphNode<String, Point2D> start = r;
		final GraphNode<String, Point2D> end = y;

		System.out.println("Following is a Topological Sort");
		Deque<GraphNode<String, Point2D>> topological_order = graph.kahnsTopologicalSort();
		// Print topological order - should output "4, 5, 2, 0, 3, 1"
		System.out.println("Kahn's: " + topological_order.stream().map(GraphNode::id).map(i -> i.toString())
				.collect(Collectors.joining(", ")));

		topological_order = graph.recursiveTopologicalSort();
		System.out.println("Recursive: " + topological_order.stream().map(GraphNode::id).map(i -> i.toString())
				.collect(Collectors.joining(", ")));

		// For r to y
		// r -> s (+5) -> t (+2=7) -> x (+7=14) -> y (-1=13) TARGET
		// r -> s (+5) -> t (+2=7) -> x (+7=14) -> z (+1=15) DEAD-END
		// r -> s (+5) -> t (+2=7) -> y (+4=11) -> z (-2=9) DEAD-END
		// r -> s (+5) -> t (+2=7) -> z (+2=9) DEAD-END
		// r -> s (+5) -> x (+6=11) -> y (-1=10) TARGET
		// r -> s (+5) -> x (+6=11) -> z (+1=12) DEAD-END
		// r -> t (+3) -> x (+7=10) -> y (-1=9) TARGET
		// r -> t (+3) -> x (+7=10) -> z (+1=11) DEAD-END
		// r -> t (+3) -> y (+4=7) TARGET
		// r -> t (+3) -> z (+2=5) DEAD-END
		final int longest_path_cost = longestPath(graph, start, end);
		System.out.println("Longest path from " + start.id() + " " + start.value() + " to " + end.id() + " "
				+ end.value() + " " + end.path() + ": " + longest_path_cost);
	}

	public static void test2() {
		// Create a graph given in the above diagram
		final Graph<Integer, Integer> g = new Graph<>();
		addEdge(g, 5, 2);
		addEdge(g, 5, 0);
		addEdge(g, 4, 0);
		addEdge(g, 4, 1);
		addEdge(g, 2, 3);
		addEdge(g, 3, 1);

		System.out.println("Following is a Topological Sort");
		Deque<GraphNode<Integer, Integer>> topological_order = g.kahnsTopologicalSort();
		// Print topological order - should output "4, 5, 2, 0, 3, 1"
		System.out.println("Kahn's: " + topological_order.stream().map(GraphNode::id).map(i -> i.toString())
				.collect(Collectors.joining(", ")));

		topological_order = g.recursiveTopologicalSort();
		System.out.println("Recursive: " + topological_order.stream().map(GraphNode::id).map(i -> i.toString())
				.collect(Collectors.joining(", ")));
	}

	private static void addEdge(Graph<Integer, Integer> graph, int from, int to) {
		graph.getOrPut(Integer.valueOf(from), Integer.valueOf(from))
				.addNeighbour(graph.getOrPut(Integer.valueOf(to), Integer.valueOf(to)), 1);
	}

	/*
	 * Function to find longest distances from a given vertex. Uses the recursive
	 * topologicalSort() method to sort the nodes in topological order.
	 */
	public static <K extends Comparable<K>, V> int longestPath(Graph<K, V> graph, GraphNode<K, V> from,
			GraphNode<K, V> to) {
		// Default cost for all nodes to Integer.MIN_VALUE
		graph.nodes().forEach(n -> n.updateCost(Integer.MIN_VALUE));
		// Set the cost for this node to 0
		from.updateCost(0);

		// Build the Topological Sort result for all vertices
		final Deque<GraphNode<K, V>> stack = graph.recursiveTopologicalSort();
		// final Deque<GraphNode<K, V>> stack = graph.kahnsTopologicalSort();

		// Process nodes in topological order
		while (!stack.isEmpty()) {
			// Get the next node from topological order (the head)
			final GraphNode<K, V> current = stack.pop();

			// Update distances of all neighbours
			for (final GraphNode.Neighbour<K, V> neighbour : current.neighbours()) {
				final GraphNode<K, V> next = neighbour.node();

				// Make sure we haven't already visited this neighbour
				GraphNode<K, V> n = current;
				boolean next_already_visited = false;
				while (n != null && !next_already_visited) {
					next_already_visited = n.idEquals(next);
					n = n.getParent();
				}
				if (next_already_visited) {
					System.out.println("Skipping " + next + " as already visited while processing " + current);
					continue;
				}

				final int new_cost = current.cost() + neighbour.cost();

				if (next.cost() != Integer.MIN_VALUE) {
					final int neighbour_cost = next.cost();
					if (new_cost > neighbour_cost) {
						next.setParent(current);
						next.updateCost(new_cost);
					}
				} else {
					next.setParent(current);
					next.updateCost(new_cost);
				}
			}
		}

		return to.cost();
	}
}
