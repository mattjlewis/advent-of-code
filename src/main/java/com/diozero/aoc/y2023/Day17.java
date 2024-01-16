package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;

public class Day17 extends Day {
	public static void main(String[] args) {
		new Day17().run();
	}

	@Override
	public String name() {
		return "Clumsy Crucible";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(calculateHeatLoss(TextParser.loadIntMatrix(input), 0, 3));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(calculateHeatLoss(TextParser.loadIntMatrix(input), 4, 10));
	}

	private static int calculateHeatLoss(int[][] grid, int minimumMoves, int maximumMoves) {
		final int width = grid[0].length;
		final int height = grid.length;

		// Simplified Dijkstra algorithm

		final Queue<Position> open_nodes = new PriorityQueue<>();
		final Set<Position> closed_nodes = new HashSet<>();

		final Point2D destination = new Point2D(width - 1, height - 1);

		/*
		 * Because you already start in the top-left block, you don't incur that block's
		 * heat loss unless you leave that block and then return to it.
		 */
		open_nodes.offer(new Position(new Point2D(1, 0), CompassDirection.EAST, 1, grid[0][1]));
		// Note that North is where dy = +1
		open_nodes.offer(new Position(new Point2D(0, 1), CompassDirection.NORTH, 1, grid[1][0]));

		while (!open_nodes.isEmpty()) {
			// Get the node with the lowest heat loss from the set of open nodes
			final Position current = open_nodes.poll();

			if (current.location.equals(destination) && current.moveCount >= minimumMoves) {
				return current.heatLoss;
			}

			// My Dijkstra implementation doesn't need this as filters prior to adding to
			// queue
			if (closed_nodes.contains(current)) {
				continue;
			}

			closed_nodes.add(current);

			allowableDirections(grid, current, minimumMoves, maximumMoves).map(dir -> move(grid, current, dir))
					.filter(Optional::isPresent).map(Optional::get).forEach(open_nodes::offer);
			/*-
			 * From Dijkstra
			final int new_cost = current.cost() + neighbour.cost();
			if (!closed_nodes.contains(next) && !open_nodes.contains(next)) {
				next.updateCost(new_cost);
				open_nodes.offer(next);
			} else if (new_cost < next.cost()) {
				next.updateCost(new_cost);
			
				if (closed_nodes.contains(next)) {
					closed_nodes.remove(next);
					open_nodes.offer(next);
				}
			}
			*/
		}

		throw new IllegalStateException("Unable to find route to destination");
	}

	private static Stream<CompassDirection> allowableDirections(int[][] grid, Position current, int minimumMoves,
			int maximumMoves) {
		if (current.moveCount < minimumMoves) {
			return Stream.of(current.direction);
		}

		if (current.moveCount < maximumMoves) {
			return Stream.of(current.direction, current.direction.turnLeft90(), current.direction.turnRight90());
		}

		return Stream.of(current.direction.turnLeft90(), current.direction.turnRight90());
	}

	private static Optional<Position> move(int[][] grid, Position current, CompassDirection direction) {
		final Point2D next = current.location.move(direction);
		if (!next.inBounds(0, 0, grid[0].length, grid.length)) {
			return Optional.empty();
		}

		return Optional.of(new Position(next, direction, direction == current.direction ? current.moveCount + 1 : 1,
				current.heatLoss + grid[next.y()][next.x()]));
	}

	private static record Position(Point2D location, CompassDirection direction, int moveCount, int heatLoss)
			implements Comparable<Position> {
		@Override
		public int compareTo(Position other) {
			return Integer.compare(heatLoss, other.heatLoss);
		}

		@Override
		public int hashCode() {
			// Deliberately exclude heatLoss and parent from hashCode and equals
			return Objects.hash(direction, location, Integer.valueOf(moveCount));
		}

		@Override
		public boolean equals(Object obj) {
			// Deliberately exclude heatLoss and parent from hashCode and equals
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Position other = (Position) obj;
			return direction == other.direction && Objects.equals(location, other.location)
					&& moveCount == other.moveCount;
		}

		@Override
		public String toString() {
			return "Position[" + location + ", " + direction + ", moves: " + moveCount + ", heat loss: " + heatLoss
					+ "]";
		}
	}
}
