package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;
import com.diozero.aoc.algorithm.dijkstra.Dijkstra;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;

/*
 * TODO Complete part2!!
 */
public class Day20 extends Day {
	private static final String ENTRANCE_PORTAL_ID = "AA";
	private static final String EXIT_PORTAL_ID = "ZZ";

	public static void main(String[] args) {
		new Day20().run();
	}

	@Override
	public String name() {
		return "Donut Maze";
	}

	@Override
	public String part1(Path input) throws IOException {
		final DoughnutMaze maze = buildDoughnutMaze(input);

		if (maze.exitNode == null || !Dijkstra.findPath(maze.startNode, maze.exitNode)) {
			throw new IllegalArgumentException("No path found");
		}

		if (Logger.isDebugEnabled()) {
			System.out.println("Path: " + maze.exitNode.path());
		}

		return Integer.toString(maze.exitNode.cost());
	}

	@Override
	public String part2(Path input) throws IOException {
		final DoughnutMaze maze = buildDoughnutMaze(input);
		final GraphNode<Integer, Point2D> target = maze.exitNode;

		final Queue<NodeWithDepth> open_nodes = new PriorityQueue<>();
		final Set<String> closed_nodes = new HashSet<>();

		maze.startNode.updateCost(0);
		if (true) {
			return null;
		}
		open_nodes.offer(new NodeWithDepth(maze.startNode, 0));

		while (!open_nodes.isEmpty()) {
			// Get the node with the lowest cost from the set of open nodes
			final NodeWithDepth current = open_nodes.poll();
			if (target == current.graphNode() && current.depth == 0) {
				break;
			}

			getNeighbours(maze, current).forEach(neighbour -> {
				final NodeWithDepth next = NodeWithDepth.create(maze, current.depth, neighbour.node());
				final int new_cost = current.graphNode.cost() + neighbour.cost();

				/*-
				if (!closed_nodes.contains(next.id()) && !open_nodes.contains(next)) {
					next.setParent(current);
					next.updateCost(new_cost);
				
					open_nodes.offer(next);
				} else if (new_cost < next.cost()) {
					next.setParent(current);
					next.updateCost(new_cost);
				
					if (closed_nodes.contains(next.id())) {
						closed_nodes.remove(next.id());
						open_nodes.offer(next);
					}
				}
				*/
			});

			closed_nodes.add(current.id());
		}

		return null;
	}

	private static Stream<GraphNode.Neighbour<Integer, Point2D>> getNeighbours(DoughnutMaze maze, NodeWithDepth node) {
		/*
		 * When at the outermost level (depth=0), only the outer labels AA and ZZ
		 * function (as the start and end, respectively); all other outer labelled tiles
		 * are effectively walls.
		 */
		if (node.depth == 0) {
			// Remove all outer portal gateways other than ZZ
			// Keep all inner portals and the ZZ outer portal
			return node.graphNode.neighbours().stream()
					.filter(neighbour -> maze.isInnerPortalAt(neighbour.node().value())
							|| maze.isExitPortalAt(neighbour.node().value()));
		}

		return node.graphNode.neighbours().stream();
	}

	private static DoughnutMaze buildDoughnutMaze(Path input) throws IOException {
		final Graph<Integer, Point2D> graph = new Graph<>();
		final Map<String, PortalGateway> portal_gateways = new HashMap<>();
		final Map<String, Portal> portals = new HashMap<>();
		GraphNode<Integer, Point2D> start_node = null;
		GraphNode<Integer, Point2D> exit_node = null;

		final char[][] maze = TextParser.loadCharMatrix(input);
		final int width = maze[0].length;
		final int height = maze.length;
		final Set<Point2D> irreducible_points = new HashSet<>();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (maze[y][x] == ' ' || maze[y][x] == '#') {
					continue;
				}

				final GraphNode<Integer, Point2D> node = graph.getOrPut(new Point2D(x, y), p -> p.identity(width));

				if (maze[y][x] == '.') {
					// Look for neighbours
					for (int dy = Math.max(0, y - 1); dy <= Math.min(height - 1, y + 1); dy++) {
						for (int dx = Math.max(0, x - 1); dx <= Math.min(height - 1, x + 1); dx++) {
							if ((dy == y && dx == x) || (dy != y && dx != x) || maze[dy][dx] == ' '
									|| maze[dy][dx] == '#') {
								continue;
							}

							// Blank spaces or portals
							if (maze[dy][dx] == '.') {
								node.addNeighbour(graph.getOrPut(new Point2D(dx, dy), p -> p.identity(width)), 1);
							} else {
								// A portal gateway - need to find the other part
								final PortalGateway p1 = buildPortal(maze,
										graph.getOrPut(new Point2D(dx, dy), p -> p.identity(width)), node);
								node.addNeighbour(p1.node(), 1);
								Logger.debug("Built portal gateway {}", p1);

								irreducible_points.add(p1.node().value());
								irreducible_points.add(p1.entryOrExit().value());

								// Find the other part of this portal
								final PortalGateway p2 = portal_gateways.remove(p1.id());
								if (p1.id().equals(ENTRANCE_PORTAL_ID)) {
									start_node = p1.entryOrExit();
								} else if (p1.id().equals(EXIT_PORTAL_ID)) {
									exit_node = p1.entryOrExit();
								} else if (p2 != null) {
									Logger.debug("Adding neighbour from {} to {}", p1.node().value(),
											p2.node().value());
									p1.node().addNeighbour(p2.entryOrExit(), 0);
									p2.node().addNeighbour(p1.entryOrExit(), 0);
									portals.put(p1.id(), Portal.create(p1, p2));
								} else {
									portal_gateways.put(p1.id(), p1);
								}
							}
						}
					}
				}
			}
		}

		graph.reduce(irreducible_points, false);

		return new DoughnutMaze(graph, portals, start_node, exit_node);
	}

	private static PortalGateway buildPortal(char[][] maze, GraphNode<Integer, Point2D> node,
			GraphNode<Integer, Point2D> entryOrExit) {
		final int x = node.value().x();
		final int y = node.value().y();

		String id = null;
		a: for (int dy = Math.max(0, y - 1); dy <= Math.min(maze.length - 1, y + 1); dy++) {
			for (int dx = Math.max(0, x - 1); dx <= Math.min(maze[0].length - 1, x + 1); dx++) {
				if ((dy == y && dx == x) || (dy != y && dx != x) || maze[dy][dx] == ' ' || maze[dy][dx] == '#'
						|| maze[dy][dx] == '.') {
					continue;
				}

				if (maze[y][x] < maze[dy][dx]) {
					id = "" + maze[y][x] + maze[dy][dx];
				} else {
					id = "" + maze[dy][dx] + maze[y][x];
				}

				break a;
			}
		}

		if (id == null) {
			throw new IllegalStateException(
					"Cannot build portal '" + maze[y][x] + "' starting @ (" + x + "," + y + ")");
		}

		// Determine if this is an inner or outer portal
		boolean inner = false;
		// Inner if there is a '#' or '.' to the left and right
		// Search right
		for (int dx = x + 1; dx < maze[0].length && !inner; dx++) {
			inner = maze[y][dx] == '#' || maze[y][dx] == '.';
		}
		if (inner) {
			inner = false;
			// Search left
			for (int dx = x - 1; dx >= 0 && !inner; dx--) {
				inner = maze[y][dx] == '#' || maze[y][dx] == '.';
			}
		}

		return new PortalGateway(id, node, entryOrExit, inner);
	}

	private static record DoughnutMaze(Graph<Integer, Point2D> graph, Map<String, Portal> portals,
			GraphNode<Integer, Point2D> startNode, GraphNode<Integer, Point2D> exitNode) {
		public boolean isInnerPortalAt(Point2D location) {
			return portals.values().stream().anyMatch(p -> p.inner().node().value().equals(location));
		}

		public boolean isExitPortalAt(Point2D location) {
			return exitNode.value().equals(location);
		}
	}

	private static final record Portal(String id, PortalGateway inner, PortalGateway outer) {
		public static Portal create(PortalGateway p1, PortalGateway p2) {
			if (p1.inner()) {
				return new Portal(p1.id(), p1, p2);
			}

			return new Portal(p1.id(), p2, p1);
		}
	}

	private static final record PortalGateway(String id, GraphNode<Integer, Point2D> node,
			GraphNode<Integer, Point2D> entryOrExit, boolean inner) {
		//
	}

	private static final record NodeWithDepth(GraphNode<Integer, Point2D> graphNode, int depth) {
		public static NodeWithDepth create(DoughnutMaze maze, int currentDepth, GraphNode<Integer, Point2D> node) {

			return null;
		}

		public String id() {
			return depth + ":" + graphNode.id();
		}
	}
}
