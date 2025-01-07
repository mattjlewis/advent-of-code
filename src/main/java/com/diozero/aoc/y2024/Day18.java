package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
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
import com.diozero.aoc.geometry.Rectangle;

public class Day18 extends Day {
	public static void main(String[] args) {
		new Day18().run();
	}

	@Override
	public String name() {
		return "RAM Run";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final int num_bytes = isSample() ? 12 : 1024;

		final Set<Point2D> memory_space = Files.lines(input).limit(num_bytes).map(Point2D::parse)
				.collect(Collectors.toUnmodifiableSet());
		final Rectangle bounds = Point2D.getBounds(memory_space);

		final Graph<String, Point2D> graph = new Graph<>();

		final GraphNode<String, Point2D> start = graph.getOrPut(new Point2D(0, 0), Point2D::toString);
		final GraphNode<String, Point2D> end = graph.getOrPut(new Point2D(bounds.x2(), bounds.y2()), Point2D::toString);

		Dijkstra.findPath(start, end, n -> getNeighbours(graph, memory_space, bounds, n.value()));

		return Integer.toString(end.cost());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<String> lines = Files.readAllLines(input);

		final int start_num_bytes = isSample() ? 12 : 1024;

		final Graph<String, Point2D> graph = new Graph<>();
		final GraphNode<String, Point2D> start = graph.getOrPut(new Point2D(0, 0), Point2D::toString);
		final Set<Point2D> memory_space = new HashSet<>();

		int index = 0;
		for (; index < start_num_bytes; index++) {
			memory_space.add(Point2D.parse(lines.get(index)));
		}

		Rectangle bounds = Point2D.getBounds(memory_space);
		GraphNode<String, Point2D> end = graph.getOrPut(new Point2D(bounds.x2(), bounds.y2()), Point2D::toString);
		String coords = "";
		while (true) {
			final String line = lines.get(index++);
			final Point2D p = Point2D.parse(line);
			memory_space.add(p);

			if (p.x() > bounds.x2() || p.y() > bounds.y2()) {
				bounds = new Rectangle(start.value(), p);
				end = graph.getOrPut(new Point2D(bounds.x2(), bounds.y2()), Point2D::toString);
			}

			final Rectangle bounds_f = bounds;
			if (!Dijkstra.findPath(start, end, n -> getNeighbours(graph, memory_space, bounds_f, n.value()))) {
				coords = line;
				break;
			}
		}

		return coords;
	}

	private static Stream<Neighbour<String, Point2D>> getNeighbours(Graph<String, Point2D> graph,
			Collection<Point2D> memorySpace, Rectangle bounds, Point2D point) {
		return CompassDirection.NESW.stream().map(dir -> point.move(dir))
				.filter(p -> p.inBounds(bounds.width(), bounds.height())).filter(p -> !memorySpace.contains(p))
				.map(location -> new Neighbour<>(graph.getOrPut(location, Point2D::toString), 1));
	}
}
