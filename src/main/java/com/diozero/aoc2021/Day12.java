package com.diozero.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc2021.util.AocBase;

public class Day12 extends AocBase {
	private static final String START_NAME = "start";
	private static final String END_NAME = "end";

	public static void main(String[] args) {
		new Day12().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		final Map<String, GraphNode> nodes = loadData(input);

		Deque<Deque<GraphNode>> search_space = new ArrayDeque<>();
		search_space.addLast(new ArrayDeque<>());
		search_space.getLast().addLast(nodes.get(START_NAME));

		int num_paths = 0;
		while (!search_space.isEmpty()) {
			Deque<GraphNode> current_path = search_space.removeFirst();
			GraphNode current_node = current_path.getLast();
			for (String neighbour_name : current_node.getNeighbours()) {
				GraphNode neighbour = nodes.get(neighbour_name);
				if (neighbour.isStart()) {
					continue;
				} else if (neighbour.isEnd()) {
					num_paths++;
				} else if (neighbour.isSmall()) {
					// Small caves can only appear once
					if (!current_path.contains(neighbour)) {
						search_space.offer(joinAsNewPath(current_path, neighbour));
					}
				} else {
					search_space.offer(joinAsNewPath(current_path, neighbour));
				}
			}
		}

		return num_paths;
	}

	@Override
	public long part2(Path input) throws IOException {
		final Map<String, GraphNode> nodes = loadData(input);

		Deque<Deque<GraphNode>> search_space = new ArrayDeque<>();
		search_space.addLast(new ArrayDeque<>());
		search_space.getLast().addLast(nodes.get(START_NAME));

		int num_paths = 0;
		while (!search_space.isEmpty()) {
			Deque<GraphNode> current_path = search_space.removeFirst();
			GraphNode current_node = current_path.getLast();
			for (String neighbour_name : current_node.getNeighbours()) {
				GraphNode neighbour = nodes.get(neighbour_name);
				if (neighbour.isStart()) {
					continue;
				} else if (neighbour.isEnd()) {
					num_paths++;
				} else {
					if (neighbour.isSmall()) {
						// Only one small cave can be visited at most twice, the remaining small
						// caves can be visited at most once

						var counts = current_path.stream().filter(GraphNode::isSmall)
								.collect(Collectors.groupingBy(GraphNode::getName, Collectors.counting()));
						if (counts.containsKey(neighbour_name)) {
							// this cave has already been traversed
							if (counts.values().stream().noneMatch(c -> c > 1)) {
								// No small caves have been visited twice.
								// Add a path that re-visits this one.
								search_space.addLast(joinAsNewPath(current_path, neighbour));
							}
						} else {
							// This room has not yet been traversed
							search_space.addLast(joinAsNewPath(current_path, neighbour));
						}
					} else {
						search_space.addLast(joinAsNewPath(current_path, neighbour));
					}
				}
			}
		}

		return num_paths;
	}

	private static Map<String, GraphNode> loadData(Path input) throws IOException {
		final Map<String, GraphNode> nodes = new HashMap<>();
		// Load all nodes
		Files.lines(input).forEach(line -> processLine(line.split("-"), nodes));
		Logger.debug("nodes: {}", nodes);
		return nodes;
	}

	private static void processLine(String[] nodeNames, Map<String, GraphNode> nodes) {
		nodes.computeIfAbsent(nodeNames[0], node_name -> new GraphNode(node_name)).addNeighbour(nodeNames[1]);
		nodes.computeIfAbsent(nodeNames[1], node_name -> new GraphNode(node_name)).addNeighbour(nodeNames[0]);
	}

	private static Deque<GraphNode> joinAsNewPath(Deque<GraphNode> path, GraphNode node) {
		// Create a new path containing all of the nodes in path ...
		Deque<GraphNode> new_path = new ArrayDeque<>(path);
		// ... and add node to the end of the path
		new_path.offer(node);
		return new_path;
	}

	public static final class GraphNode {
		private String name;
		private Set<String> neighbours;

		private boolean start;
		private boolean end;
		private boolean small;

		public GraphNode(String name) {
			this.name = name;
			neighbours = new HashSet<>();
			start = name.equals(START_NAME);
			end = name.equals(END_NAME);
			small = !start && !end && name.chars().allMatch(Character::isLowerCase);
		}

		public String getName() {
			return name;
		}

		public Set<String> getNeighbours() {
			return neighbours;
		}

		public Set<String> addNeighbour(String name) {
			neighbours.add(name);
			return neighbours;
		}

		public boolean isStart() {
			return start;
		}

		public boolean isEnd() {
			return end;
		}

		public boolean isSmall() {
			return small;
		}

		@Override
		public String toString() {
			return "GraphNode [name=" + name + ", neighbours=" + neighbours + ", start=" + start + ", end=" + end
					+ ", small=" + small + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GraphNode other = (GraphNode) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
}
