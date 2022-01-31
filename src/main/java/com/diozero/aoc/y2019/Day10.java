package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;

public class Day10 extends Day {
	public static void main(String[] args) {
		new Day10().run();
	}

	@Override
	public String name() {
		return "Monitoring Station";
	}

	@Override
	public String part1(Path input) throws IOException {
		final Set<Point2D> asteroids = TextParser.loadPoints(input, '#');

		return Integer.toString(asteroids.stream()
				.mapToInt(asteroid -> getAngleColinearPoints(asteroid, asteroids).size()).max().orElseThrow());
	}

	@Override
	public String part2(Path input) throws IOException {
		final Set<Point2D> asteroids = TextParser.loadPoints(input, '#');
		final Point2D station = asteroids.stream()
				.max(Comparator.comparingInt(asteroid -> getAngleColinearPoints(asteroid, asteroids).size()))
				.orElseThrow();
		final List<Queue<Point2D>> angles = getAngleColinearPoints(station, asteroids);

		int count = 0;
		Point2D last_asteroid = null;
		Iterator<Queue<Point2D>> angles_it = angles.iterator();
		while (count < 200 && angles_it.hasNext()) {
			if (angles.isEmpty()) {
				throw new IllegalStateException("No more asteroids");
			}

			// Vaporise the closest asteroid on each angle
			final Queue<Point2D> angle_points = angles_it.next();
			last_asteroid = angle_points.poll();
			count++;
			Logger.debug("Vaporised asteroid #{} at {}", count, last_asteroid);
			if (angle_points.isEmpty()) {
				angles_it.remove();
			}

			// Might need to loop around the angles many times
			if (!angles_it.hasNext()) {
				angles_it = angles.iterator();
			}
		}

		if (last_asteroid == null) {
			throw new IllegalStateException("No asteroids were vaporised");
		}

		return Integer.toString(last_asteroid.x() * 100 + last_asteroid.y());
	}

	private static List<Queue<Point2D>> getAngleColinearPoints(Point2D origin, Set<Point2D> points) {
		// Create a map from angle to a queue of points ordered by distance from origin
		final Map<Double, Queue<Point2D>> angles_to_points = points.stream() //
				.filter(p -> !p.equals(origin)) //
				.collect(Collectors.groupingBy(asteroid -> Double.valueOf(origin.angleTo(asteroid, 90)),
						Collectors.toCollection(() -> new PriorityQueue<>(
								Comparator.comparingInt(body -> body.manhattanDistance(origin))))));

		// Convert the map to a list that is ordered by angle
		return angles_to_points.entrySet().stream() // For every angle from origin
				.sorted(Map.Entry.comparingByKey()) // Sort by angle
				.map(Map.Entry::getValue) //
				.collect(Collectors.toList());
	}
}
