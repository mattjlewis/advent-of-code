package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.PrintUtil;

public class Day18 extends Day {
	public static void main(String[] args) {
		new Day18().run();
	}

	@Override
	public String name() {
		return "Many-Worlds Interpretation";
	}

	@Override
	public String part1(Path input) throws IOException {
		return Integer.toString(solve(Maze.load(input, false)));
	}

	@Override
	public String part2(Path input) throws IOException {
		return Integer.toString(solve(Maze.load(input, true)));
	}

	public static int solve(Maze maze) {
		return solve(maze, maze.startingPositions().stream().map(MazeNode::location).toList());
	}

	public static int solve(final Maze maze, Collection<Point2D> startPositions) {
		final Queue<State> open_nodes = new PriorityQueue<>(Comparator.comparingInt(State::cost));
		final Map<String, Integer> closed_nodes = new HashMap<>();

		final State start = new State(startPositions, new TreeSet<>(), 0);
		open_nodes.offer(start);

		while (!open_nodes.isEmpty()) {
			final State current_state = open_nodes.poll();
			// Complete if all keys have been collected
			if (current_state.isComplete(maze.keys().size())) {
				return current_state.cost();
			}

			for (Map.Entry<Character, DistanceFromTo> entry : distancesToUncollectedKeys(maze, current_state)
					.entrySet()) {
				final DistanceFromTo distance_from_to = entry.getValue();

				final SortedSet<Character> new_keys_collected = new TreeSet<>(current_state.keysCollected());
				new_keys_collected.add(entry.getKey());

				final Set<Point2D> new_positions = current_state.positions().stream()
						.map(position -> position == distance_from_to.from() ? distance_from_to.to() : position)
						.collect(Collectors.toSet());

				final int new_cost = current_state.cost() + distance_from_to.distance();
				final State next_state = new State(new_positions, new_keys_collected, new_cost);

				final String state_repr = next_state.id();
				final Integer cached_cost = closed_nodes.get(state_repr);
				if (cached_cost == null || new_cost < cached_cost.longValue()) {
					// No, add to the queue to continue processing
					closed_nodes.put(state_repr, Integer.valueOf(new_cost));
					open_nodes.offer(next_state);
				}
			}
		}

		throw new IllegalArgumentException("No solution");
	}

	public static Map<Character, DistanceFromTo> distancesToUncollectedKeys(Maze maze, State state) {
		return state.positions().stream()
				.flatMap(
						position -> distanceToUncollectedKeys(maze, position, state.keysCollected()).entrySet().stream()
								.map(e -> Map.entry(e.getKey(),
										new DistanceFromTo(position, e.getValue().to(), e.getValue().distance()))))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public static Map<Character, DistanceTo> distanceToUncollectedKeys(Maze maze, Point2D position,
			SortedSet<Character> keysCollected) {
		final Map<Character, DistanceTo> distance_to_unowned_keys = new HashMap<>();

		final Queue<Point2D> open_nodes = new ArrayDeque<>();
		final Map<Point2D, Integer> closed_nodes = new HashMap<>();

		closed_nodes.put(position, Integer.valueOf(0));
		open_nodes.add(position);

		while (!open_nodes.isEmpty()) {
			final Point2D current = open_nodes.poll();

			for (GraphNode.Neighbour<Integer, MazeNode> neighbour : maze.getNode(current).neighbours()) {
				final MazeNode maze_node = neighbour.node().value();

				if (closed_nodes.containsKey(maze_node.location())) {
					continue;
				}

				int new_cost = closed_nodes.get(current).intValue() + neighbour.cost();
				closed_nodes.put(maze_node.location(), Integer.valueOf(new_cost));

				// Is this a door that we haven't yet collected the key for or a key that we
				// haven't yet collected?
				if (maze_node.isDoor()) {
					final Character ch = maze_node.door().get();

					// Is it a door that we don't have the key to?
					if (!keysCollected.contains(ch)) {
						continue;
					}
				} else if (maze_node.isKey()) {
					final Character ch = maze_node.key().get();

					// Is it a key that we haven't yet collected?
					if (!keysCollected.contains(ch)) {
						distance_to_unowned_keys.put(ch, new DistanceTo(maze_node.location(), new_cost));
						continue;
					}
				}

				open_nodes.offer(maze_node.location());
			}
		}

		return distance_to_unowned_keys;
	}

	private static record DistanceFromTo(Point2D from, Point2D to, int distance) {
		//
	}

	private static record DistanceTo(Point2D to, int distance) {
		//
	}

	private static class State {
		private final String id;
		private final Collection<Point2D> positions;
		private final SortedSet<Character> keysCollected;
		private int cost;

		public State(Collection<Point2D> positions, SortedSet<Character> keysCollected, int cost) {
			this.id = positions + ":" + keysCollected;
			this.positions = positions;
			this.keysCollected = keysCollected;
			this.cost = cost;
		}

		public boolean isComplete(int totalNumberOfKeys) {
			return keysCollected.size() == totalNumberOfKeys;
		}

		public String id() {
			return id;
		}

		public Collection<Point2D> positions() {
			return positions;
		}

		public SortedSet<Character> keysCollected() {
			return keysCollected;
		}

		public int cost() {
			return cost;
		}
	}

	private static GraphNode<Integer, MazeNode> getOrCreateGraphNode(Graph<Integer, MazeNode> graph, int width, int x,
			int y, char ch) {
		return graph.getOrPut(MazeNode.create(x, y, ch),
				mn -> Integer.valueOf(mn.location().y() * width + mn.location().x()));
	}

	public static record Maze(Graph<Integer, MazeNode> graph, List<MazeNode> startingPositions,
			Map<Character, MazeNode> keys, Map<Character, MazeNode> doors,
			Map<Point2D, GraphNode<Integer, MazeNode>> pointsToGraphNodeMap) {

		private static final char WALL = '#';
		private static final char FLOOR = '.';
		private static final char ROBOT = '@';

		public static Maze load(Path input, boolean part2) throws IOException {
			final char[][] maze = Files.lines(input).map(line -> line.toCharArray()).toArray(char[][]::new);

			final int height = maze.length;
			final int width = maze[0].length;

			// If part 2, locate the @ and update the surrounding squares
			if (part2) {
				a: for (int y = 0; y < maze.length; y++) {
					for (int x = 0; x < maze[0].length; x++) {
						if (maze[y][x] == ROBOT) {
							for (int dy = Math.max(0, y - 1); dy <= Math.min(height - 1, y + 1); dy++) {
								for (int dx = Math.max(0, x - 1); dx <= Math.min(width - 1, x + 1); dx++) {
									if (dy != y && dx != x) {
										maze[dy][dx] = ROBOT;
									} else {
										maze[dy][dx] = WALL;
									}
								}
							}
							break a;
						}
					}
				}

				if (Logger.isDebugEnabled()) {
					PrintUtil.print(maze);
				}
			}

			final Graph<Integer, MazeNode> graph = new Graph<>();

			final Map<Character, MazeNode> keys = new HashMap<>();
			final Map<Character, MazeNode> doors = new HashMap<>();
			final List<MazeNode> starting_positions = new ArrayList<>();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (maze[y][x] == WALL) {
						continue;
					}

					final GraphNode<Integer, MazeNode> node = getOrCreateGraphNode(graph, width, x, y, maze[y][x]);
					// Now find the neighbours
					for (int dy = Math.max(0, y - 1); dy <= Math.min(height - 1, y + 1); dy++) {
						for (int dx = Math.max(0, x - 1); dx <= Math.min(width - 1, x + 1); dx++) {
							// No diagonals
							if (dy == y && dx == x || dy != y && dx != x) {
								continue;
							}

							if (maze[dy][dx] != WALL) {
								node.neighbours().add(new GraphNode.Neighbour<>(
										getOrCreateGraphNode(graph, width, dx, dy, maze[dy][dx]), 1));
							}
						}
					}

					if (maze[y][x] == FLOOR) {
						continue;
					}

					if (maze[y][x] == ROBOT) {
						starting_positions.add(node.value());
						continue;
					}

					Character ch = Character.valueOf(maze[y][x]);
					if (Character.isLowerCase(maze[y][x])) {
						keys.put(ch, node.value());
					} else if (Character.isUpperCase(maze[y][x])) {
						doors.put(Character.valueOf(Character.toLowerCase(ch.charValue())), node.value());
					}
				}
			}

			Logger.debug("starting positions: {}", starting_positions);
			Logger.debug("keys: {}", keys);
			Logger.debug("doors: {}", doors);

			final Set<MazeNode> irreducible_points = new HashSet<>();
			irreducible_points.addAll(starting_positions);
			irreducible_points.addAll(keys.values());
			irreducible_points.addAll(doors.values());
			graph.reduce(irreducible_points, true);

			// Is it also possible to remove dead-ends that don't contain keys?

			return new Maze(graph, starting_positions, keys, doors, graph.nodes().stream()
					.collect(Collectors.toMap(node -> node.value().location(), Function.identity())));
		}

		public GraphNode<Integer, MazeNode> getNode(Point2D position) {
			return pointsToGraphNodeMap.get(position);
		}

		public GraphNode<Integer, MazeNode> getStartNode() {
			return getNode(startingPositions.get(0).location());
		}

		public GraphNode<Integer, MazeNode> getKeyNode(Character key) {
			return getNode(keys.get(key).location());
		}
	}

	public static record MazeNode(Point2D location, Optional<Character> key, Optional<Character> door) {
		public static MazeNode create(int x, int y, char ch) {
			final Optional<Character> key = Character.isLowerCase(ch) ? Optional.of(Character.valueOf(ch))
					: Optional.empty();
			final Optional<Character> door = Character.isUpperCase(ch)
					? Optional.of(Character.valueOf(Character.toLowerCase(ch)))
					: Optional.empty();
			return new MazeNode(new Point2D(x, y), key, door);
		}

		public boolean isKey() {
			return key.isPresent();
		}

		public boolean isDoor() {
			return door.isPresent();
		}

		public boolean isBlockedByDoorOrOtherKey(char targetKey, Collection<Character> remainingKeys) {
			if (!isKey() && !isDoor()) {
				return false;
			}

			if (isDoor()) {
				return remainingKeys.contains(door.get());
			}

			// Is this the key that we are looking for?
			Character ch = key.get();
			if (ch.charValue() == targetKey) {
				return false;
			}

			// Do we already have this key?
			return remainingKeys.contains(ch);
		}

		public boolean isBlockedByDoor(GraphNode<Integer, MazeNode> node, SortedSet<Character> remainingKeys) {
			if (isDoor()) {
				return remainingKeys.contains(door.get());
			}

			// Should be able to reject this node if any parent node is a key...
			GraphNode<Integer, MazeNode> parent = node.getParent();
			while (parent != null) {
				if (parent.value().isKey()) {
					return true;
				}
				parent = parent.getParent();
			}

			return false;
		}
	}
}
