package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;
import com.diozero.aoc.algorithm.GraphNode.Neighbour;
import com.diozero.aoc.algorithm.dijkstra.Dijkstra;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.MatrixUtil;
import com.diozero.aoc.util.TextParser;

public class Day16 extends Day {
	private static final char START = 'S';
	private static final char END = 'E';

	public static void main(String[] args) {
		new Day16().run();
	}

	@Override
	public String name() {
		return "Reindeer Maze";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(loadAndFindRoutes(input).cost());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final GraphNode<String, LocationAndDirection> end_node = loadAndFindRoutes(input);

		final Set<Point2D> path = new HashSet<>();
		addPath(end_node, path);

		return Integer.toString(path.size());
	}

	private static void addPath(GraphNode<String, LocationAndDirection> node, Set<Point2D> path) {
		// Recursively add the position for all paths that lead to node, including alternative
		// parents that have an equal score
		GraphNode<String, LocationAndDirection> n = node;
		do {
			path.add(n.value().position);
			n.altParents().forEach(p -> addPath(p, path));
			n = n.getParent();
		} while (n != null);
	}

	private static Stream<Neighbour<String, LocationAndDirection>> getNeighbours(
			Graph<String, LocationAndDirection> graph, char[][] maze, GraphNode<String, LocationAndDirection> n) {
		final Point2D p = n.value().position;
		final CompassDirection dir = n.value().direction;
		final Collection<Neighbour<String, LocationAndDirection>> neighbours = new ArrayList<>();
		// Find the locations accessible from n
		for (CompassDirection new_dir : new CompassDirection[] { dir.turnLeft90(), dir, dir.turnRight90() }) {
			final Point2D new_p = p.move(new_dir);
			if (maze[new_p.y()][new_p.x()] == TextParser.UNSET_CHAR || maze[new_p.y()][new_p.x()] == END) {
				neighbours.add(new Neighbour<>(
						graph.getOrPut(new LocationAndDirection(new_p, new_dir), LocationAndDirection::toString),
						new_dir == dir ? 1 : 1001));
			}
		}

		return neighbours.stream();
	}

	private static final GraphNode<String, LocationAndDirection> loadAndFindRoutes(Path input) throws IOException {
		final char[][] maze = TextParser.loadCharMatrix(input);
		final Graph<String, LocationAndDirection> graph = new Graph<>();
		final GraphNode<String, LocationAndDirection> start_node = graph.getOrPut(
				new LocationAndDirection(MatrixUtil.find(maze, START).orElseThrow(), CompassDirection.EAST),
				LocationAndDirection::toString);
		final Point2D end = MatrixUtil.find(maze, END).orElseThrow();
		// List of all possible end node configuration
		final List<GraphNode<String, LocationAndDirection>> end_nodes = List.of(
				graph.getOrPut(new LocationAndDirection(end, CompassDirection.NORTH), LocationAndDirection::toString),
				graph.getOrPut(new LocationAndDirection(end, CompassDirection.EAST), LocationAndDirection::toString),
				graph.getOrPut(new LocationAndDirection(end, CompassDirection.SOUTH), LocationAndDirection::toString),
				graph.getOrPut(new LocationAndDirection(end, CompassDirection.WEST), LocationAndDirection::toString));

		Dijkstra.findRoutes(start_node, end_nodes.stream().map(GraphNode::id).collect(Collectors.toList()),
				n -> getNeighbours(graph, maze, n));

		return end_nodes.stream().min(Comparator.comparingInt(GraphNode::cost)).orElseThrow();
	}

	private static final record LocationAndDirection(Point2D position, CompassDirection direction) {

	}
}
