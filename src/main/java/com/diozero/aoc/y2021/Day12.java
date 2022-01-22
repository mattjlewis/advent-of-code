package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.tinylog.Logger;

import com.diozero.aoc.Day;

public class Day12 extends Day {
	private static final String START_NAME = "start";
	private static final String END_NAME = "end";

	public static void main(String[] args) {
		new Day12().run();
	}

	@Override
	public String name() {
		return "Passage Pathing";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(getNumPaths(loadData(input), Day12::canVisitSmallNeighbourPart1));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(getNumPaths(loadData(input), Day12::canVisitSmallNeighbourPart2));
	}

	private static int getNumPaths(final Map<String, D12GraphNode> allNodes, SmallNeighourLogic smallNeighbourLogic) {
		final Queue<Deque<D12GraphNode>> open_set = new ArrayDeque<>();

		final Deque<D12GraphNode> start = new ArrayDeque<>();
		start.add(allNodes.get(START_NAME));
		open_set.offer(start);

		int num_paths = 0;
		while (!open_set.isEmpty()) {
			// Get and remove the head of the open set of nodes
			final Deque<D12GraphNode> current_path = open_set.poll();

			// Get but don't remove the tail of the current path
			final D12GraphNode current_node = current_path.peekLast();

			for (String neighbour_name : current_node.neighbours()) {
				final D12GraphNode neighbour = allNodes.get(neighbour_name);

				if (neighbour.isStart()) {
					continue;
				} else if (neighbour.isEnd()) {
					num_paths++;
				} else if (neighbour.isSmall()) {
					if (smallNeighbourLogic.canVisitSmallNeighbour(current_path, neighbour)) {
						open_set.offer(joinAsNewPath(current_path, neighbour));
					}
				} else {
					open_set.offer(joinAsNewPath(current_path, neighbour));
				}
			}
		}

		return num_paths;
	}

	private static boolean canVisitSmallNeighbourPart1(Deque<D12GraphNode> path, D12GraphNode neighbour) {
		// Small caves can only appear once
		return !path.contains(neighbour);
	}

	private static boolean canVisitSmallNeighbourPart2(Deque<D12GraphNode> path, D12GraphNode neighbour) {
		// Only one small cave can be visited at most twice, the remaining small
		// caves can be visited at most once

		// Has this small neighbour already been visited?
		if (!path.contains(neighbour)) {
			return true;
		}

		// This small room has already been visited -> its visit count _will_ increment
		// to two. Is there another small room that has been visited more than once?

		final Map<String, AtomicInteger> counts = new HashMap<>();
		for (D12GraphNode node : path) {
			if (node.isSmall()) {
				if (counts.computeIfAbsent(node.name(), n -> new AtomicInteger()).incrementAndGet() > 1) {
					return false;
				}
			}
		}

		return true;
	}

	private static Map<String, D12GraphNode> loadData(final Path input) throws IOException {
		final Map<String, D12GraphNode> nodes = new HashMap<>();

		// Load all nodes
		Files.lines(input).forEach(line -> processLine(line.split("-"), nodes));
		Logger.debug("nodes: {}", nodes);

		return nodes;
	}

	private static void processLine(final String[] nodeNames, final Map<String, D12GraphNode> nodes) {
		nodes.computeIfAbsent(nodeNames[0], node_name -> new D12GraphNode(node_name)).addNeighbour(nodeNames[1]);
		nodes.computeIfAbsent(nodeNames[1], node_name -> new D12GraphNode(node_name)).addNeighbour(nodeNames[0]);
	}

	private static Deque<D12GraphNode> joinAsNewPath(final Deque<D12GraphNode> path, final D12GraphNode node) {
		// Create a new path containing all of the nodes in path ...
		final Deque<D12GraphNode> new_path = new ArrayDeque<>(path);
		// ... and add node to the end of the path
		new_path.offer(node);

		return new_path;
	}

	private interface SmallNeighourLogic {
		boolean canVisitSmallNeighbour(Deque<D12GraphNode> current_path, D12GraphNode neighbour);
	}

	/*
	 * Note no need to override hashCode() and equals(o) as the default behaviour is
	 * correct - nodes will be unique and object identity is correct.
	 */
	public static final class D12GraphNode {
		private final String name;
		private final Set<String> neighbours;

		private final boolean start;
		private final boolean end;
		private final boolean small;

		public D12GraphNode(String name) {
			this.name = name;
			neighbours = new HashSet<>();
			start = name.equals(START_NAME);
			end = name.equals(END_NAME);
			small = !start && !end && name.chars().allMatch(Character::isLowerCase);
		}

		public D12GraphNode(String name, String neighbourName) {
			this.name = name;
			neighbours = new HashSet<>();
			neighbours.add(neighbourName);
			start = name.equals(START_NAME);
			end = name.equals(END_NAME);
			small = !start && !end && name.chars().allMatch(Character::isLowerCase);
		}

		public String name() {
			return name;
		}

		public Set<String> neighbours() {
			return neighbours;
		}

		public Set<String> addNeighbour(String neighbourName) {
			neighbours.add(neighbourName);
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
			return "D12GraphNode [name=" + name + ", neighbours=" + neighbours + ", start=" + start + ", end=" + end
					+ ", small=" + small + "]";
		}
	}
}
