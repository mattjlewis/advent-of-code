package com.diozero.aoc.algorithm;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Stream;

/*
 * Note quite a bit slower when hashCode() and equals(o) are implemented.
 * The default object identity equality is correct.
 *
 * Heuristic is only used in A* path finder - with Dijkstra it is always == cost.
 *
 * estimate(n) = cost(n) + heuristic(n)
 *
 * f(n) = g(n) + h(n)
 *
 * cost(n) is something we can (and do) calculate at any given step, and it's
 * the distance between the start node and this node (g in the source article).
 *
 * heuristic(n) is something we don't know, and need to estimate - the distance
 * from this node to the target node (h in the source article).
 *
 * estimate(n) is the sum of the two (f in the source article).
 */
public class GraphNode<K, V> implements Comparable<GraphNode<K, V>> {
	private final K id;
	private final V value;
	private final Set<Neighbour<K, V>> neighbours;
	private GraphNode<K, V> parent;
	protected int cost;
	// Dijkstra: estimate == cost
	protected int estimate;

	public GraphNode(K id, V value) {
		this.id = id;
		this.value = value;
		neighbours = new HashSet<>();
		cost = Integer.MAX_VALUE;
		estimate = Integer.MAX_VALUE;
	}

	public K id() {
		return id;
	}

	public V value() {
		return value;
	}

	public Set<Neighbour<K, V>> neighbours() {
		return neighbours;
	}

	public Stream<Neighbour<K, V>> neighboursStream() {
		return neighbours.stream();
	}

	public void addNeighbour(GraphNode<K, V> destination, int neighbourCost) {
		neighbours.add(new Neighbour<>(destination, neighbourCost));
	}

	public GraphNode<K, V> getParent() {
		return parent;
	}

	public void setParent(GraphNode<K, V> parent) {
		this.parent = parent;
	}

	public int cost() {
		return cost;
	}

	public int estimate() {
		return estimate;
	}

	public void updateCost(int newCost) {
		cost = newCost;
		estimate = cost;
	}

	public void updateCostAndHeuristic(int newCost, int heuristic) {
		cost = newCost;
		estimate = cost + heuristic;
	}

	public void reset() {
		parent = null;
		cost = Integer.MAX_VALUE;
		estimate = Integer.MAX_VALUE;
	}

	@Override
	public int compareTo(GraphNode<K, V> other) {
		return Integer.compare(estimate, other.estimate);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", GraphNode.class.getSimpleName() + "[", "]").add("id=" + id).add("value=" + value)
				.add("parent.id=" + (parent == null ? "null" : parent.id)).add("cost=" + cost).toString();
	}

	public static record Neighbour<K, V> (GraphNode<K, V> node, int cost) {
		//
	}
}
