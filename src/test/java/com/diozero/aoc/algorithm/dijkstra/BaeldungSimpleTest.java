package com.diozero.aoc.algorithm.dijkstra;

import java.util.ArrayDeque;
import java.util.Deque;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.diozero.aoc.algorithm.GraphNode;

@SuppressWarnings("static-method")
public class BaeldungSimpleTest {
	@Test
	public void test() {
		GraphNode<String, String> node_a = new GraphNode<>("A", "A");
		GraphNode<String, String> node_b = new GraphNode<>("B", "B");
		GraphNode<String, String> node_c = new GraphNode<>("C", "C");
		GraphNode<String, String> node_d = new GraphNode<>("D", "D");
		GraphNode<String, String> node_e = new GraphNode<>("E", "E");
		GraphNode<String, String> node_f = new GraphNode<>("F", "F");
		GraphNode<String, String> node_g = new GraphNode<>("G", "G");

		node_a.addNeighbour(node_b, 10);
		node_a.addNeighbour(node_c, 15);

		node_b.addNeighbour(node_d, 12);
		node_b.addNeighbour(node_f, 15);

		node_c.addNeighbour(node_e, 10);

		node_d.addNeighbour(node_e, 2);
		node_d.addNeighbour(node_f, 1);
		node_d.addNeighbour(node_g, 1);

		node_f.addNeighbour(node_e, 5);

		node_g.addNeighbour(node_e, 5);

		GraphNode<String, String> start = node_a;
		GraphNode<String, String> target = node_e;

		Dijkstra.findPath(start, target);
		System.out.format("Lowest cost to get from %s to %s: %d%n", start.value(), target.value(), target.cost());
		Assertions.assertEquals(24, target.cost());

		Deque<String> path = new ArrayDeque<>();
		GraphNode<String, String> current = target;
		do {
			path.addFirst(current.value());
			current = current.getParent();
		} while (current != null);

		String path_string = String.join("->", path);
		System.out.println("Shortest path: " + path_string);
		Assertions.assertEquals("A->B->D->E", path_string);
	}
}
