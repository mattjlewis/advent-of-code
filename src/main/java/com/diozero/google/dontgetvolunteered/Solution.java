package com.diozero.google.dontgetvolunteered;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.ToIntBiFunction;

public class Solution {
	private static final int GRID_SIZE = 8;

	public static int solution(int src, int dest) {
		int[][] grid = new int[GRID_SIZE][GRID_SIZE];
		// TODO Populate the graph

		Point2D src_point = new Point2D(src % GRID_SIZE, src / GRID_SIZE);
		Point2D dest_point = new Point2D(dest % GRID_SIZE, dest / GRID_SIZE);

		int x_delta = Math.abs(src_point.x - dest_point.x);
		int y_delta = Math.abs(src_point.y - dest_point.y);
		// System.out.println("x_delta % 2: " + (x_delta % 2));

		switch (src_point.manhattanDistance(dest_point)) {
		case 0:
			return 0;
		case 1:
			return 3;
		case 2:
			if (x_delta == 1 && (src_point.isCorner(GRID_SIZE) || dest_point.isCorner(GRID_SIZE))) {
				return 4;
			}
			return 2;
		case 3:
			if (x_delta == 0 || y_delta == 0) {
				return 3;
			}
			return 1;
		case 4:
			if (x_delta == 2) {
				return 4;
			}
			return 2;
		case 5:
			return 3;
		case 6:
			if (x_delta == 4 && y_delta == 2 || x_delta == 3 && y_delta == 3 || x_delta == 2 && y_delta == 4) {
				return 2;
			}
			return 4;
		case 7:
			if (x_delta == 7 || y_delta == 7) {
				return 5;
			}
			return 3;
		case 8:
			return 4;
		case 9:
			if (x_delta == 6 && y_delta == 3 || y_delta == 6 && x_delta == 3 //
					|| x_delta == 5 && y_delta == 4 || x_delta == 4 && y_delta == 5) {
				return 3;
			}
			return 5;
		case 10:
			return 4;
		case 11:
			return 5;
		case 12:
			return 4;
		case 13:
			return 5;
		case 14:
			return 6;
		default:
			return -1;
		}

		/*-
		System.out.println("src: " + src + ", dest: " + dest);
		
		GraphNode<String, Point2D> start = new GraphNode<>(src_point.toString(), src_point);
		GraphNode<String, Point2D> target = new GraphNode<>(dest_point.toString(), dest_point);
		
		GraphNode<String, Point2D> path = findPath(start, target, Solution::heuristic);
		
		return -1;
		*/
	}

	public static <K, V> GraphNode<K, V> findPath(GraphNode<K, V> start, GraphNode<K, V> target,
			ToIntBiFunction<V, V> heuristicFunction) {
		final Queue<GraphNode<K, V>> open_nodes = new PriorityQueue<>();
		final Set<K> closed_nodes = new HashSet<>();

		start.updateCostAndHeuristic(0, heuristicFunction.applyAsInt(start.value, target.value));
		open_nodes.offer(start);

		while (!open_nodes.isEmpty()) {
			final GraphNode<K, V> current = open_nodes.poll();

			// Safe to do object equality rather than Object.equals()
			if (current == target) {
				return current;
			}

			for (GraphNode.Neighbour<K, V> neighbour : current.neighbours) {
				final GraphNode<K, V> next = neighbour.node;
				final int new_cost = current.cost + neighbour.cost;

				if (!closed_nodes.contains(next.id) && !open_nodes.contains(next)) {
					next.setParent(current);
					/*-
					 * f(n)        = g(n)    + h(n)
					 * estimate(n) = cost(n) + heuristic(n)
					 */
					next.updateCostAndHeuristic(new_cost, heuristicFunction.applyAsInt(next.value, target.value));

					open_nodes.offer(next);
				} else if (new_cost < next.cost) {
					next.setParent(current);
					next.updateCostAndHeuristic(new_cost, heuristicFunction.applyAsInt(next.value, target.value));

					if (closed_nodes.contains(next.id)) {
						closed_nodes.remove(next.id);
						open_nodes.offer(next);
					}
				}
			}

			closed_nodes.add(current.id);
		}

		throw new IllegalStateException("No route found");
	}

	private static int heuristic(Point2D p1, Point2D p2) {
		return p1.manhattanDistance(p2);
	}

	private static class GraphNode<K, V> implements Comparable<GraphNode<K, V>> {
		K id;
		V value;
		private final Set<Neighbour<K, V>> neighbours;
		GraphNode<K, V> parent;
		int cost;
		int estimate;

		public GraphNode(K id, V value) {
			this.id = id;
			this.value = value;
			neighbours = new HashSet<>();
			cost = Integer.MAX_VALUE;
			estimate = Integer.MAX_VALUE;
		}

		public void addNeighbour(GraphNode<K, V> destination, int neighbourCost) {
			neighbours.add(new Neighbour<>(destination, neighbourCost));
		}

		public void setParent(GraphNode<K, V> parent) {
			this.parent = parent;
		}

		public void updateCostAndHeuristic(int newCost, int heuristic) {
			cost = newCost;
			estimate = cost + heuristic;
		}

		@Override
		public int compareTo(GraphNode<K, V> other) {
			return Integer.compare(estimate, other.estimate);
		}

		public static class Neighbour<K, V> {
			GraphNode<K, V> node;
			int cost;

			public Neighbour(GraphNode<K, V> node, int cost) {
				this.node = node;
				this.cost = cost;
			}
		}
	}

	private static class Point2D {
		int x;
		int y;

		public Point2D(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public boolean isCorner(int gridSize) {
			return x == 0 && y == 0 //
					|| x == gridSize - 1 && y == 0 //
					|| x == 0 && y == gridSize - 1 //
					|| x == gridSize - 1 && y == gridSize - 1;
		}

		public int manhattanDistance(Point2D other) {
			return manhattanDistance(other.x, other.y);
		}

		public int manhattanDistance(int otherX, int otherY) {
			return Math.abs(x - otherX) + Math.abs(y - otherY);
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}
}
