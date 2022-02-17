package com.diozero.aoc.algorithm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import org.tinylog.Logger;

public class Graph<K, V> {
	private final Map<K, GraphNode<K, V>> nodes;

	public Graph() {
		this(new HashMap<>());
	}

	public Graph(final Map<K, GraphNode<K, V>> nodes) {
		this.nodes = nodes;
	}

	public GraphNode<K, V> get(K id) {
		return nodes.get(id);
	}

	public GraphNode<K, V> put(K id, V value) {
		return nodes.put(id, new GraphNode<>(id, value));
	}

	public GraphNode<K, V> getOrPut(K id, V value) {
		return nodes.computeIfAbsent(id, k -> new GraphNode<>(k, value));
	}

	public GraphNode<K, V> getOrPut(V value, Function<V, K> idFunction) {
		return nodes.computeIfAbsent(idFunction.apply(value), k -> new GraphNode<>(k, value));
	}

	public Collection<GraphNode<K, V>> nodes() {
		return nodes.values();
	}

	public void reduce(Collection<V> irreducibleValues, boolean removeDeadEnds) {
		Logger.debug("Graph size before: " + nodes.size());
		final Iterator<GraphNode<K, V>> it = nodes.values().iterator();
		while (it.hasNext()) {
			final GraphNode<K, V> node = it.next();
			if (irreducibleValues.contains(node.value()) || node.neighbours().size() != 2) {
				continue;
			}
			Logger.debug("Found node that can be removed: {}", node);

			/*-
			 * A GraphNode (a vertex) of degree 2 (2 neighbours) and can be reduced.
			 * i.e.
			 * n1 <---c1---> n <---c2---> n2
			 * can be reduced to:
			 * n1 <---c1+c2---> n2
			 *
			 * Whereby n is removed from the graph.
			 */
			final Iterator<GraphNode.Neighbour<K, V>> n_it = node.neighbours().iterator();
			final GraphNode.Neighbour<K, V> n1 = n_it.next();
			final GraphNode.Neighbour<K, V> n2 = n_it.next();

			// Remove n as a neighbour of n1 and replace with new neighbour to n2
			replaceNeighbour(node, n1, n2);

			// Remove this node from the graph
			it.remove();
		}
		Logger.debug("Graph size after: " + nodes.size());
	}

	private static <K, V> void replaceNeighbour(GraphNode<K, V> node, GraphNode.Neighbour<K, V> n1,
			GraphNode.Neighbour<K, V> n2) {
		Iterator<GraphNode.Neighbour<K, V>> it = n1.node().neighbours().iterator();
		int cost_n1_to_node = 0;
		while (it.hasNext()) {
			GraphNode.Neighbour<K, V> next = it.next();
			if (next.node().equals(node)) {
				cost_n1_to_node = next.cost();
				it.remove();
				break;
			}
		}

		it = n2.node().neighbours().iterator();
		int cost_n2_to_node = 0;
		while (it.hasNext()) {
			GraphNode.Neighbour<K, V> next = it.next();
			if (next.node().equals(node)) {
				cost_n2_to_node = next.cost();
				it.remove();
				break;
			}
		}

		n1.node().addNeighbour(n2.node(), cost_n1_to_node + cost_n2_to_node);
		n2.node().addNeighbour(n1.node(), cost_n1_to_node + cost_n2_to_node);
	}

	public void reset() {
		nodes.values().forEach(GraphNode::reset);
	}
}
