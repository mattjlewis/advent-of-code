package com.diozero.aoc.algorithm;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.tinylog.Logger;

public class Graph<K extends Comparable<K>, V> {
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
		return getOrPut(idFunction.apply(value), value);
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
			 * Whereby n is removed from the graph (if no other node references it).
			 */
			final Iterator<GraphNode.Neighbour<K, V>> n_it = n.neighbours().iterator();
			final GraphNode.Neighbour<K, V> n_to_n1 = n_it.next();
			final GraphNode.Neighbour<K, V> n_to_n2 = n_it.next();
			if (n_it.hasNext()) {
				throw new IllegalStateException();
			}

			final K n_id = n.id();

			// Remove n as a neighbour of n1 and replace with new neighbour to n2
			replaceNeighbour(n_id, n_to_n1, n_to_n2);

			// Remove this node from the graph if no other node references it
			if (nodes.values().parallelStream().flatMap(GraphNode::neighboursStream)
					.noneMatch(neighbour -> neighbour.nodeIdEquals(n_id))) {
				it.remove();
			}
		}
		Logger.debug("Graph size after reduce: " + nodes.size());

		// TBD Search for nodes with only one neighbour?
		/*-
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
		*/
	}

	public static <K, V> void replaceNeighbour(K nId, GraphNode.Neighbour<K, V> nToN1,
			GraphNode.Neighbour<K, V> nToN2) {
		final GraphNode<K, V> n1 = nToN1.node();
		final GraphNode<K, V> n2 = nToN2.node();
		final Optional<GraphNode.Neighbour<K, V>> n1_to_n = n1.neighboursStream()
				.filter(neighbour -> neighbour.nodeIdEquals(nId)).findAny();
		final Optional<GraphNode.Neighbour<K, V>> n2_to_n = n2.neighboursStream()
				.filter(neighbour -> neighbour.nodeIdEquals(nId)).findAny();

		// Remove the link from n1 to n (if present)
		if (n1_to_n.isPresent()) {
			n1.neighbours().removeIf(neighbour -> neighbour.nodeIdEquals(nId));
			/*-
			final Iterator<GraphNode.Neighbour<K, V>> it = n1.neighbours().iterator();
			while (it.hasNext()) {
				final GraphNode.Neighbour<K, V> next = it.next();
				if (next.nodeIdEquals(nId)) {
					// Remove the link from n1 to n
					it.remove();
					break;
				}
			}
			*/
			// Connect n1 to n2
			n1.addNeighbour(n2, n1_to_n.get().cost() + nToN2.cost());
		}

		// Remove the link from n2 to n (if present)
		if (n2_to_n.isPresent()) {
			final Iterator<GraphNode.Neighbour<K, V>> it = n2.neighbours().iterator();
			while (it.hasNext()) {
				final GraphNode.Neighbour<K, V> next = it.next();
				if (next.nodeIdEquals(nId)) {
					// Remove the link from n2 to n
					it.remove();
					break;
				}
			}
			// Connect n2 to n1
			n2.addNeighbour(n1, n2_to_n.get().cost() + nToN1.cost());
		}
	}

	public int longestPath(GraphNode<K, V> start, GraphNode<K, V> end) {
		return dfs(new HashSet<>(), start, end.id());
	}

	private int dfs(Set<K> visited, GraphNode<K, V> node, K endId) {
		if (node.id().equals(endId)) {
			return 0;
		}

		visited.add(node.id());

		int max = Integer.MIN_VALUE;
		for (GraphNode.Neighbour<K, V> neighbour : node.neighbours()) {
			if (!visited.contains(neighbour.node().id())) {
				max = Math.max(max, neighbour.cost() + dfs(visited, neighbour.node(), endId));
			}
		}

		visited.remove(node.id());

		return max;
	}

	public void reset() {
		nodes.values().forEach(GraphNode::reset);
	}

	public Deque<GraphNode<K, V>> kahnsTopologicalSort() {
		// Store indegrees for all vertices
		final Map<K, AtomicInteger> indegree = new ConcurrentHashMap<>();

		// Traverse all nodes to populate vertex indegrees
		nodes.values().parallelStream().flatMap(GraphNode::neighboursStream).forEach(neighbour -> indegree
				.computeIfAbsent(neighbour.node().id(), i -> new AtomicInteger()).incrementAndGet());

		// Create a queue and add all vertices with in-degree 0
		final Queue<GraphNode<K, V>> queue = new ConcurrentLinkedDeque<>();
		nodes.values().parallelStream().filter(node -> !indegree.containsKey(node.id())).forEach(queue::add);

		// List of nodes in topological order
		final Deque<GraphNode<K, V>> topological_order = new ArrayDeque<>();
		// Count of visited vertices
		int count = 0;
		while (!queue.isEmpty()) {
			// Extract front of queue (or perform dequeue) and add it to topological order
			final GraphNode<K, V> node = queue.poll();
			topological_order.add(node);

			// Iterate through all its neighbouring nodes of the dequeued node and decrease
			// their in-degree by 1
			for (GraphNode.Neighbour<K, V> neighbour : node.neighbours()) {
				// If in-degree becomes zero, add it to queue
				if (indegree.get(neighbour.node().id()).decrementAndGet() == 0) {
					queue.add(neighbour.node());
				}
			}

			count++;
		}

		// Check if there was a cycle
		if (count != nodes.size()) {
			System.out.println("There exists a cycle in the graph, count=" + count + ", nodes.size()=" + nodes.size());
			return new ArrayDeque<>();
		}

		return topological_order;
	}

	public Deque<GraphNode<K, V>> recursiveTopologicalSort() {
		final Deque<GraphNode<K, V>> stack = new ArrayDeque<>();
		final Set<K> visited = new HashSet<>();

		nodes.values().stream().filter(n -> !visited.contains(n.id()))
				.forEach(n -> recursiveTopologicalSort(n, visited, stack));

		return stack;
	}

	// A recursive function used by longestPath. See link for details:
	// https:// www.geeksforgeeks.org/topological-sorting/
	private static <K, V> void recursiveTopologicalSort(GraphNode<K, V> node, Set<K> visited,
			Deque<GraphNode<K, V>> stack) {
		final K id = node.id();
		if (visited.contains(id)) {
			return;
		}

		// Mark the current node as visited
		visited.add(id);

		// Recurse for all the vertices adjacent to this vertex
		node.neighboursStream().filter(n -> !visited.contains(n.node().id()))
				.forEach(n -> recursiveTopologicalSort(n.node(), visited, stack));

		// Push current vertex to stack which stores topological sort (to the head)
		stack.push(node);
	}
}
