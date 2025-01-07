package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;
import com.diozero.aoc.algorithm.dijkstra.Dijkstra;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.StringUtil;
import com.diozero.aoc.util.TextParser;

public class Day20 extends Day {
	private static final String ENTRANCE_PORTAL_ID = "AA";
	private static final String EXIT_PORTAL_ID = "ZZ";
	private static final char WALL = TextParser.SET_CHAR;
	private static final char GROUND = TextParser.UNSET_CHAR;

	public static void main(String[] args) {
		new Day20().run();
	}

	@Override
	public String name() {
		return "Donut Maze";
	}

	@Override
	public String part1(Path input) throws IOException {
		final DoughnutMaze maze = DoughnutMaze.build(input, false);

		if (!Dijkstra.findPath(maze.startNode, maze.exitNode)) {
			throw new IllegalArgumentException("No path found");
		}

		if (Logger.isDebugEnabled()) {
			System.out.println("Path: " + maze.exitNode.path());
		}

		return Integer.toString(maze.exitNode.cost());
	}

	@Override
	public String part2(Path input) throws IOException {
		return Integer.toString(DoughnutMaze.build(input, true).path());
	}

	private static PortalGateway buildPortal(char[][] maze, GraphNode<Integer, Point2D> n1, Point2D n2,
			GraphNode<Integer, Point2D> entryOrExit) {
		// Note that order of characters is important - always left to right or top to bottom
		final String id = StringUtil.toString(maze[n1.value().y()][n1.value().x()], maze[n2.y()][n2.x()]);

		// Determine if this is an inner or outer portal
		boolean inner = false;
		// Inner if there is eventually a '#' or '.' both left and right
		// Search right
		for (int right_x = n1.value().x() + 1; right_x < maze[0].length && !inner; right_x++) {
			inner = maze[n1.value().y()][right_x] == WALL || maze[n1.value().y()][right_x] == GROUND;
		}
		if (inner) {
			inner = false;
			// Search left
			for (int left_x = n1.value().x() - 1; left_x >= 0 && !inner; left_x--) {
				inner = maze[n1.value().y()][left_x] == WALL || maze[n1.value().x()][left_x] == GROUND;
			}
		}

		return new PortalGateway(id, entryOrExit, inner);
	}

	private static record DoughnutMaze(Graph<Integer, Point2D> graph, Map<String, PortalPair> portalPairs,
			Map<Point2D, PortalGateway> portalLocations, GraphNode<Integer, Point2D> startNode,
			GraphNode<Integer, Point2D> exitNode) {

		public static DoughnutMaze build(Path input, boolean part2) throws IOException {
			final Graph<Integer, Point2D> graph = new Graph<>();
			final Map<String, PortalGateway> portal_gateways = new HashMap<>();
			final Map<String, PortalPair> portal_pairs = new HashMap<>();
			final Map<Point2D, PortalGateway> portal_locations = new HashMap<>();
			GraphNode<Integer, Point2D> start_node = null;
			GraphNode<Integer, Point2D> exit_node = null;

			final char[][] maze = TextParser.loadCharMatrix(input);
			final int width = maze[0].length;
			final int height = maze.length;
			final Set<Point2D> irreducible_points = new HashSet<>();

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (maze[y][x] == ' ' || maze[y][x] == WALL) {
						continue;
					}

					final GraphNode<Integer, Point2D> node = graph.getOrPut(new Point2D(x, y), p -> p.identity(width));

					if (maze[y][x] == GROUND) {
						// Look for neighbours
						for (int dy = Math.max(0, y - 1); dy <= Math.min(height - 1, y + 1); dy++) {
							for (int dx = Math.max(0, x - 1); dx <= Math.min(width - 1, x + 1); dx++) {
								if ((dy == y && dx == x) || (dy != y && dx != x) || maze[dy][dx] != GROUND) {
									continue;
								}

								node.addNeighbour(graph.getOrPut(new Point2D(dx, dy), p -> p.identity(width)), 1);
							}
						}
					} else {
						// Find the other part of this portal gateway (right or down)
						final Optional<Point2D> portal_opt = Stream.of(CompassDirection.EAST, CompassDirection.NORTH)
								.map(dir -> node.value().move(dir)).filter(p -> p.inBounds(width, height))
								.filter(p -> Character.isAlphabetic(maze[p.y()][p.x()])).findFirst();

						// Not present if this is the right or bottom part of the 2-letter gateway
						if (portal_opt.isPresent()) {
							final Point2D p2 = portal_opt.get();

							final GraphNode<Integer, Point2D> entry_or_exit_node = graph
									.getOrPut(getPortalEntryOrExit(maze, node.value(), p2), p -> p.identity(width));
							if (!part2) {
								entry_or_exit_node.addNeighbour(node, 1);
							}

							irreducible_points.add(node.value());
							irreducible_points.add(entry_or_exit_node.value());

							final PortalGateway pg1 = buildPortal(maze, node, p2, entry_or_exit_node);
							// Find the other part of this portal gateway (if discovered)
							final PortalGateway pg2 = portal_gateways.remove(pg1.id());
							if (pg1.id().equals(ENTRANCE_PORTAL_ID)) {
								start_node = entry_or_exit_node;
							} else if (pg1.id().equals(EXIT_PORTAL_ID)) {
								exit_node = entry_or_exit_node;
							} else if (pg2 != null) {
								Logger.debug("Adding neighbour from {} to {}", pg1.entryOrExit().value(),
										pg2.entryOrExit().value());
								if (!part2) {
									pg1.entryOrExit.addBiDirectionalNeighbour(pg2.entryOrExit, 1);
								}
								portal_pairs.put(pg1.id(), PortalPair.create(pg1, pg2));
								portal_locations.put(pg1.entryOrExit.value(), pg1);
								portal_locations.put(pg2.entryOrExit.value(), pg2);
							} else {
								portal_gateways.put(pg1.id(), pg1);
							}
						}
					}
				}
			}

			if (start_node == null || exit_node == null) {
				throw new IllegalStateException("Unable to find either start or exit node");
			}

			if (!portal_gateways.isEmpty()) {
				throw new IllegalStateException("Unbalanced portal gateways: " + portal_gateways);
			}

			graph.reduce(irreducible_points, false);

			return new DoughnutMaze(graph, portal_pairs, portal_locations, start_node, exit_node);
		}

		public int path() {
			final Map<NodeWithDepth, Integer> current_cost = new HashMap<>();
			final NodeWithDepth start_3d = new NodeWithDepth(startNode, 0);
			current_cost.put(start_3d, Integer.valueOf(0));

			final Queue<NodeWithDepth> queue = new LinkedList<>();
			queue.add(start_3d);

			while (!queue.isEmpty()) {
				final NodeWithDepth current_node = queue.poll();
				final Point2D current_point = current_node.node.value();

				if (current_node.depth == 0 && current_point.equals(exitNode.value())) {
					return current_cost.get(current_node).intValue();
				}

				// Is a portal accessible from this point?
				final PortalGateway pg = portalLocations.get(current_point);
				if (pg != null) {
					final PortalPair portal_pair = portalPairs.get(pg.id);
					NodeWithDepth nested_destination = null;
					if (!pg.inner) {
						if (current_node.depth() > 0) {
							nested_destination = new NodeWithDepth(portal_pair.inner.entryOrExit, current_node.depth() - 1);
						}
					} else {
						nested_destination = new NodeWithDepth(portal_pair.outer.entryOrExit, current_node.depth() + 1);
					}

					if (nested_destination != null) {
						int cost = current_cost.get(current_node).intValue() + 1;
						if (cost < current_cost.getOrDefault(nested_destination, Integer.valueOf(Integer.MAX_VALUE))
								.intValue()) {
							current_cost.put(nested_destination, Integer.valueOf(cost));
							queue.add(nested_destination);
						}
					}
				}

				// For part 2, neighbours to portals are excluded
				current_node.node.neighbours().forEach(neighbour -> {
					final NodeWithDepth destination = new NodeWithDepth(neighbour.node(), current_node.depth());

					int cost = current_cost.get(current_node).intValue() + neighbour.cost();
					if (cost < current_cost.getOrDefault(destination, Integer.valueOf(Integer.MAX_VALUE)).intValue()) {
						current_cost.put(destination, Integer.valueOf(cost));
						queue.add(destination);
					}
				});
			}

			return -1;
		}
	}

	private static final record PortalPair(String id, PortalGateway inner, PortalGateway outer) {
		public static PortalPair create(PortalGateway p1, PortalGateway p2) {
			if (p1.inner()) {
				return new PortalPair(p1.id(), p1, p2);
			}

			return new PortalPair(p1.id(), p2, p1);
		}
	}

	private static final record PortalGateway(String id, GraphNode<Integer, Point2D> entryOrExit, boolean inner) {
	}

	private static final record NodeWithDepth(GraphNode<Integer, Point2D> node, int depth) {
		public String id() {
			return depth + ":" + node.id();
		}
	}

	private static Point2D getPortalEntryOrExit(char[][] maze, Point2D p1, Point2D p2) {
		return Stream
				.concat(CompassDirection.NESW.stream().map(dir -> p1.move(dir)),
						CompassDirection.NESW.stream().map(dir -> p2.move(dir)))
				.filter(p -> p.inBounds(maze[0].length, maze.length)).filter(p -> maze[p.y()][p.x()] == GROUND)
				.findFirst().orElseThrow();
	}
}
