package com.diozero.aoc.algorithm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
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
		Iterator<GraphNode<K, V>> it = nodes.values().iterator();
		while (it.hasNext()) {
			final GraphNode<K, V> n = it.next();
			if (irreducibleValues.contains(n.value()) || n.neighbours().size() != 2) {
				continue;
			}
			Logger.debug("Found node with 2 neighbours that can be reduced: {}", n);

			/*-
			 * A GraphNode (a vertex) of degree 2 (2 neighbours) and can be reduced.
			 * i.e.
			 * n1 <---c1---> n <---c2---> n2
			 * can be reduced to:
			 * n1 <---c1+c2---> n2
			 *
			 * Whereby n is removed from the graph.
			 */
			final Iterator<GraphNode.Neighbour<K, V>> n_it = n.neighbours().iterator();
			final GraphNode.Neighbour<K, V> n_to_n1 = n_it.next();
			final GraphNode.Neighbour<K, V> n_to_n2 = n_it.next();
			if (n_it.hasNext()) {
				throw new IllegalStateException();
			}

			// Remove n as a neighbour of n1 and replace with new neighbour to n2
			replaceNeighbour(n, n_to_n1, n_to_n2);

			// Remove this node from the graph
			it.remove();
		}
		Logger.debug("Graph size after reduce: " + nodes.size());

		// TBD Search for nodes with only one neighbour?
		it = nodes.values().iterator();
		while (it.hasNext()) {
			final GraphNode<K, V> n = it.next();
			if (irreducibleValues.contains(n.value()) || n.neighbours().size() != 1) {
				continue;
			}
			Logger.debug("Found node with 1 neighbour that can possibly be reduced: {}", n);
			final GraphNode.Neighbour<K, V> neighbour = n.neighbours().stream().findAny().orElseThrow();
			Logger.trace("{} with neighbours {}", neighbour.node(), neighbour.node().neighbours());
		}
	}

	private static <K, V> void replaceNeighbour(GraphNode<K, V> n, GraphNode.Neighbour<K, V> nToN1,
			GraphNode.Neighbour<K, V> nToN2) {
		final GraphNode<K, V> n1 = nToN1.node();
		final GraphNode<K, V> n2 = nToN2.node();
		final Optional<GraphNode.Neighbour<K, V>> n1_to_n = nToN1.node().neighboursStream()
				.filter(neighbour -> neighbour.node().equals(n)).findAny();
		final Optional<GraphNode.Neighbour<K, V>> n2_to_n = nToN2.node().neighboursStream()
				.filter(neighbour -> neighbour.node().equals(n)).findAny();

		// Remove the link from n1 to n (if present)
		if (n1_to_n.isPresent()) {
			final Iterator<GraphNode.Neighbour<K, V>> it = n1.neighbours().iterator();
			while (it.hasNext()) {
				final GraphNode.Neighbour<K, V> next = it.next();
				if (next.node().equals(n)) {
					// Remove the link from n1 to n
					it.remove();
					break;
				}
			}
			// Connect n1 to n2
			n1.addNeighbour(n2, n1_to_n.get().cost() + nToN2.cost());
		}

		// Remove the link from n2 to n (if present)
		if (n2_to_n.isPresent()) {
			final Iterator<GraphNode.Neighbour<K, V>> it = n2.neighbours().iterator();
			while (it.hasNext()) {
				final GraphNode.Neighbour<K, V> next = it.next();
				if (next.node().equals(n)) {
					// Remove the link from n2 to n
					it.remove();
					break;
				}
			}
			// Connect n2 to n1
			n2.addNeighbour(n1, n2_to_n.get().cost() + nToN1.cost());
		}
	}

	public void reset() {
		nodes.values().forEach(GraphNode::reset);
	}
}
