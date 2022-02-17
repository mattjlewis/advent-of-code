package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;
import com.diozero.aoc.algorithm.dijkstra.Dijkstra;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;

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
		final char[][] maze = TextParser.loadCharMatrix(input);
		final int height = maze.length;
		final int width = maze[0].length;
		final Graph<String, Point2D> graph = new Graph<>();
		final Map<String, PortalGateway> portal_gateways = new HashMap<>();
		final Map<String, Portal> portals = new HashMap<>();
		final Set<Point2D> irreducible_points = new HashSet<>();
		GraphNode<String, Point2D> start_node = null;
		GraphNode<String, Point2D> end_node = null;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (maze[y][x] == ' ' || maze[y][x] == '#') {
					continue;
				}

				final GraphNode<String, Point2D> node = graph.getOrPut(new Point2D(x, y), Point2D::toString);

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
								node.addNeighbour(graph.getOrPut(new Point2D(dx, dy), Point2D::toString), 1);
							} else {
								// A portal gateway - need to find the other part
								PortalGateway p1 = buildPortal(maze,
										graph.getOrPut(new Point2D(dx, dy), Point2D::toString), node);
								node.addNeighbour(p1.node(), 1);
								irreducible_points.add(p1.node().value());
								irreducible_points.add(p1.entryOrExit().value());
								Logger.debug("Built portal gateway {}", p1);

								// Find the other part of this portal
								PortalGateway p2 = portal_gateways.remove(p1.id());
								if (p1.id().equals(ENTRANCE_PORTAL_ID)) {
									start_node = p1.entryOrExit();
								} else if (p1.id().equals(EXIT_PORTAL_ID)) {
									end_node = p1.entryOrExit();
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

		if (!Dijkstra.findPath(start_node, end_node)) {
			throw new IllegalArgumentException("No path found");
		}

		if (Logger.isDebugEnabled()) {
			GraphNode<String, Point2D> current = end_node;
			do {
				Logger.debug("current: {}", current.id());
				current = current.getParent();
			} while (current != null);
		}

		return Integer.toString(end_node.cost());
	}

	private static PortalGateway buildPortal(char[][] maze, GraphNode<String, Point2D> node,
			GraphNode<String, Point2D> entryOrExit) {
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

	@Override
	public String part2(Path input) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	private static final record PortalGateway(String id, GraphNode<String, Point2D> node,
			GraphNode<String, Point2D> entryOrExit, boolean inner) {
		//
	}

	private static final record Portal(String id, PortalGateway inner, PortalGateway outer) {
		public static Portal create(PortalGateway p1, PortalGateway p2) {
			if (p1.inner()) {
				return new Portal(p1.id(), p1, p2);
			}

			return new Portal(p1.id(), p2, p1);
		}
	}
}
