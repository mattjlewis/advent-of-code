package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;
import com.diozero.aoc.algorithm.astar.AStarPathFinder;
import com.diozero.aoc.algorithm.dijkstra.Dijkstra;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.PrintUtil;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

/*
 * Ref: https://www.baeldung.com/java-solve-maze#variant---shortest-path-bfs
 */
public class Day15 extends Day {
	public static void main(String[] args) {
		new Day15().run();
	}

	@Override
	public String name() {
		return "Oxygen System";
	}

	@Override
	public String part1(Path input) throws IOException {
		try (final ShipExplorer ship = new ShipExplorer(input)) {
			// First of all fully explore the ship
			ship.exploreAndBuildGraph();

			// Can use generic path finder algorithms now that the ship is fully explored
			final GraphNode<String, Point2D> o2_supply = ship.graph().get(ship.o2SupplyPosition().toString());
			// Find the shortest path from the droid's start position to the O2 Supply
			// Dijkstra.findPath(ship.graph().get(Point2D.ORIGIN.toString()), o2_supply);
			AStarPathFinder.findPath(ship.graph().get(Point2D.ORIGIN.toString()), o2_supply, Day15::heuristic);

			return Integer.toString(o2_supply.cost());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static int heuristic(Point2D p1, Point2D p2) {
		return p1.manhattanDistance(p2);
	}

	@Override
	public String part2(Path input) throws IOException {
		try (final ShipExplorer ship = new ShipExplorer(input)) {
			// First of all fully explore the ship
			ship.exploreAndBuildGraph();

			// Can use generic path finder algorithms now that the ship is fully explored
			final GraphNode<String, Point2D> o2_supply = ship.graph().get(ship.o2SupplyPosition().toString());
			// Populate all of the paths that are accessible from the O2 Supply position
			Dijkstra.findRoutes(o2_supply);

			// Find the longest path from the O2 supply position to any node in the graph -
			// that will be the time taken to fill the area with oxygen
			return Integer.toString(ship.graph().nodes().stream().mapToInt(GraphNode::cost).max().orElseThrow());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private enum Tile {
		// Note that DROID tile is only used when printing the maze
		WALL(PrintUtil.FILLED_PIXEL), CLEAR('.'), O2_SUPPLY('O'), DROID('D');

		private final char pixel;

		Tile(char pixel) {
			this.pixel = pixel;
		}

		public char pixel() {
			return pixel;
		}

		public static Tile valueOf(int ordinal) {
			return values()[ordinal];
		}
	}

	private static class ShipExplorer implements AutoCloseable {
		private static final Map<CompassDirection, Integer> DIRECTION_MAPPING;
		static {
			DIRECTION_MAPPING = new HashMap<>();
			DIRECTION_MAPPING.put(CompassDirection.NORTH, Integer.valueOf(1));
			DIRECTION_MAPPING.put(CompassDirection.SOUTH, Integer.valueOf(2));
			DIRECTION_MAPPING.put(CompassDirection.EAST, Integer.valueOf(3));
			DIRECTION_MAPPING.put(CompassDirection.WEST, Integer.valueOf(4));
		}

		// For the Intcode VM
		private final BlockingQueue<CompassDirection> movementInstructions;
		private final BlockingQueue<Tile> responses;
		private final IntcodeVirtualMachine intcodeVm;
		private final ExecutorService executor;
		private Future<?> intcodeVmFuture;

		// A complete map from point to tile, including walls
		private final Map<Point2D, Tile> map;
		// Graph of valid routes; excludes walls of course
		private final Graph<String, Point2D> graph;
		private Point2D o2SupplyPosition;

		// Droid state
		private Point2D droidPosition;
		private CompassDirection droidDirection;

		private ShipExplorer(Path input) throws IOException {
			movementInstructions = new LinkedBlockingQueue<>();
			responses = new LinkedBlockingQueue<>();
			intcodeVm = IntcodeVirtualMachine.load(input, this::getMove, this::tileValue);
			// executor = Executors.newSingleThreadExecutor();
			// Note pool size is 0 and keepAliveTime is 0 to prevent shutdown delays
			executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 0, TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>());

			map = new HashMap<>();
			graph = new Graph<>();

			droidPosition = Point2D.ORIGIN;
			droidDirection = CompassDirection.NORTH;

			map.put(droidPosition, Tile.CLEAR);
			graph.getOrPut(droidPosition, Point2D::toString);
		}

		/**
		 * Input for the Intcode VM - droid movement instructions
		 *
		 * @return Droid movement instruction
		 */
		public int getMove() {
			try {
				return DIRECTION_MAPPING.get(movementInstructions.take()).intValue();
			} catch (InterruptedException e) {
				Logger.debug("Interrupted! {}", e);
				// Inform the Intcode VM that it was interrupted and should halt
				Thread.currentThread().interrupt();
				return -1;
			}
		}

		/**
		 * Output from the Intcode VM - tile information
		 *
		 * @param value the tile type that the droid requested to move to
		 */
		public void tileValue(long value) {
			try {
				responses.put(Tile.valueOf((int) value));
			} catch (InterruptedException e) {
				Logger.debug(e, "Interrupted! {}", e);
				throw new RuntimeException("Interrupted while putting new element on queue", e);
			}
		}

		/**
		 * Move the droid to fully explore the maze using a depth-first search
		 * algorithm. The DFS search algorithm is used to simplify the drone movements
		 * given that it isn't easy to move to a specific point in the maze - the DFS
		 * approaches minimises the number of backtrack movements.
		 *
		 * Also builds the graph that is used later to find the shortest / longest paths
		 * through the maze.
		 *
		 * @throws InterruptedException if interrupted when interacting with the Intcode
		 *                              VM input / output blocking queues
		 */
		public void exploreAndBuildGraph() throws InterruptedException {
			// Start the Intcode VM
			intcodeVmFuture = executor.submit(intcodeVm);

			// Keep track of where we have already visited
			final Set<Point2D> visited = new HashSet<>();
			visited.add(droidPosition);

			// Record the moves so we can backtrack when we reach a dead-end
			// A stack so they can be accessed in LIFO order
			final Deque<CompassDirection> movement_history = new ArrayDeque<>();
			// A stack of unexplored paths that we will backtrack to on reaching a dead-end
			final Deque<Point2D> branches = new ArrayDeque<>();

			while (true) {
				/*
				 * Continue on a single path until we hit a dead-end, then traverse back to the
				 * last branch point until there are no more branches to explore.
				 */

				// Explore the immediate area and get a list of unexplored directions
				final List<CompassDirection> unexplored_directions = getUnexploredDirections(visited);

				printMaze();

				// A dead-end?
				if (unexplored_directions.size() == 0) {

					// Exit the loop if there are no more branches to explore
					if (branches.isEmpty()) {
						Logger.debug("No more branches, exiting exploration loop");
						break;
					}

					// Get the position of the last branch and backtrack to it
					final Point2D branch_position = branches.removeLast();
					Logger.debug("Traversing back to branch @ {} from current position {}", branch_position,
							droidPosition);
					do {
						// Move the droid in the opposite direction
						moveDroid(movement_history.pollLast().opposite(), visited);

						printMaze();
					} while (!droidPosition.equals(branch_position));
				} else {
					if (unexplored_directions.size() == 1) {
						// Just continue if there is only one choice
						droidDirection = unexplored_directions.get(0);
					} else {
						Logger.debug("Multiple directions to explore: {}...", unexplored_directions);
						// Multiple paths to explore, continue on the current one if possible
						if (!unexplored_directions.remove(droidDirection)) {
							// Otherwise just pick any unexplored direction
							droidDirection = unexplored_directions.get(0);
						}

						// Add the location of this branch position the tail of the queue
						Logger.debug("Adding a branch @ {}...", droidPosition);
						branches.offerLast(droidPosition);
					}

					// Move the droid and keep track of this movement direction
					moveDroid(droidDirection, visited);
					movement_history.add(droidDirection);
				}
			}

			// Shutdown the Intcode VM
			intcodeVmFuture.cancel(true);
			executor.shutdown();

			// Reduce the graph to replace long corridors with only one way in and out into
			// a single node (neighbours.size() == 2)
			graph.reduce(Set.of(Point2D.ORIGIN, o2SupplyPosition), false);
			// Verifying the we no longer need the map
			map.clear();
		}

		public Point2D o2SupplyPosition() {
			return o2SupplyPosition;
		}

		public Graph<String, Point2D> graph() {
			return graph;
		}

		private void printMaze() {
			if (Logger.isDebugEnabled()) {
				Tile tile = map.get(droidPosition);
				map.put(droidPosition, Tile.DROID);
				PrintUtil.print(map, '?', Tile::pixel);
				map.put(droidPosition, tile);
				System.out.println();
			}
		}

		/**
		 * Get a list of unexplored directions from the current droid position. Also
		 * builds the graph that is used later to find the shortest / longest paths
		 * through the maze.
		 *
		 * @param visited list of points in the maze that have already been visited
		 * @return a list of unexplored directions
		 * @throws InterruptedException if the Intcode VM blocking queues are
		 *                              interrupted
		 */
		private List<CompassDirection> getUnexploredDirections(Set<Point2D> visited) throws InterruptedException {
			final List<CompassDirection> directions = getValidDirections();
			directions.removeIf(direction -> visited.contains(droidPosition.translate(direction)));
			return directions;
		}

		/**
		 * Get all valid directions from the current droid position. Also builds the
		 * graph that is used later to find the shortest / longest paths through the
		 * maze.
		 *
		 * @return a list of all valid directions that we can go to from here
		 * @throws InterruptedException if the Intcode VM blocking queues are
		 *                              interrupted
		 */
		private List<CompassDirection> getValidDirections() throws InterruptedException {
			final List<CompassDirection> valid_directions = new ArrayList<>();
			final Set<GraphNode.Neighbour<String, Point2D>> neighbours = graph.get(droidPosition.toString())
					.neighbours();

			// Try to move in each direction to discover what is there
			for (CompassDirection direction : DIRECTION_MAPPING.keySet()) {
				final Point2D target_pos = droidPosition.translate(direction);

				GraphNode<String, Point2D> neighbour_node = graph.get(target_pos.toString());
				Tile target_tile = map.get(target_pos);
				// Do we already know what is at target_pos?
				if (target_tile == null) {
					// Discover what is at this position by moving the droid in this direction
					movementInstructions.put(direction);

					// Wait for the response
					target_tile = responses.take();
					// Store all tiles in the maze map, only store valid paths in the graph
					map.put(target_pos, target_tile);

					switch (target_tile) {
					case O2_SUPPLY:
						o2SupplyPosition = target_pos;
						Logger.debug("Found O2 supply! " + o2SupplyPosition);
						//$FALL-THROUGH$ Note deliberate case statement fall-through
					case CLEAR:
						// Add to the list of neighbours for this graph node
						// Note that neighbour_node must be null here as this map node was unknown
						neighbour_node = graph.getOrPut(target_pos, Point2D::toString);
						// Move back to where we were
						movementInstructions.put(direction.opposite());
						// Consume and ignore the output as we already know it
						responses.take();
						break;
					case WALL:
					default:
						// Ignore
					}
				}

				// If there is a valid route
				if (neighbour_node != null) {
					neighbours.add(new GraphNode.Neighbour<>(neighbour_node, 1));
					valid_directions.add(direction);
				}
			}

			return valid_directions;
		}

		private void moveDroid(CompassDirection direction, final Set<Point2D> visited) throws InterruptedException {
			// Move in this direction
			movementInstructions.put(direction);
			// Consume and ignore the output as we know what it is
			responses.take();
			// Update the droid's position and direction
			droidPosition = droidPosition.translate(direction);
			droidDirection = direction;
			// Mark this position as visited
			visited.add(droidPosition);
		}

		@Override
		public void close() {
			if (intcodeVmFuture != null) {
				intcodeVmFuture.cancel(true);
			}
			executor.shutdown();
		}
	}
}
