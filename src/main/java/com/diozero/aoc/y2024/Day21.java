package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.MatrixUtil;

public class Day21 extends Day {
	private static final char[][] KEYPAD = { //
			{ '#', '#', '#', '#', '#' }, //
			{ '#', '7', '8', '9', '#' }, //
			{ '#', '4', '5', '6', '#' }, //
			{ '#', '1', '2', '3', '#' }, //
			{ '#', '#', '0', 'A', '#' }, //
			{ '#', '#', '#', '#', '#' } };
	private static final char[][] ARROW_KEYPAD = { //
			{ '#', '#', '#', '#', '#' }, //
			{ '#', '#', '^', 'A', '#' }, //
			{ '#', '<', 'v', '>', '#' }, //
			{ '#', '#', '#', '#', '#' } };

	public static void main(String[] args) {
		new Day21().run();
	}

	@Override
	public String name() {
		return "Keypad Conundrum";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(getComplexity(Files.readAllLines(input), 2));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(getComplexity(Files.readAllLines(input), 25));
	}

	private static long getComplexity(List<String> input, int robots) {
		return input.stream()
				.mapToLong(
						code -> Integer.parseInt(code.substring(0, 3)) * execute(KEYPAD, code, robots, new HashMap<>()))
				.sum();
	}

	private static long execute(char[][] grid, String code, int robots, Map<String, Long> cache) {
		String key = code + "_" + robots;
		if (cache.containsKey(key)) {
			return cache.get(key).longValue();
		}

		char cur_pos = 'A';
		long length = 0;
		for (char next_pos : code.toCharArray()) {
			final List<String> paths = findAllMinPaths(grid, cur_pos, next_pos);

			if (robots == 0) {
				length += paths.getFirst().length();
			} else {
				length += paths.stream().mapToLong(path -> execute(ARROW_KEYPAD, path, robots - 1, cache)).min()
						.orElseThrow();
			}

			cur_pos = next_pos;
		}

		cache.put(key, Long.valueOf(length));

		return length;
	}

	private static List<String> findAllMinPaths(char[][] grid, char s, char e) {
		final Point2D start = MatrixUtil.find(grid, s).orElseThrow();
		final Point2D end = MatrixUtil.find(grid, e).orElseThrow();
		final Queue<Node> queue = new LinkedList<>(List.of(new Node(start, new ArrayList<>(), 0, null)));
		final Map<Point2D, Integer> seen = new HashMap<>();
		final List<List<Character>> paths = new ArrayList<>();

		int min_cost = Integer.MAX_VALUE;
		while (!queue.isEmpty()) {
			final Node cur_node = queue.poll();
			final Point2D cur_point = cur_node.p;

			if (cur_node.direction != null) {
				cur_node.path.add(Character.valueOf(cur_node.direction.toArrow()));
			}

			if (cur_point.equals(end)) {
				if (cur_node.cost < min_cost) {
					paths.clear();
					min_cost = cur_node.cost;
				}
				if (cur_node.cost == min_cost) {
					paths.add(new ArrayList<>(cur_node.path));
				}
				continue;
			}

			if (seen.getOrDefault(cur_point, Integer.valueOf(Integer.MAX_VALUE)).intValue() < cur_node.cost) {
				continue;
			}

			seen.put(cur_point, Integer.valueOf(cur_node.cost));
			if (cur_node.cost > min_cost) {
				continue;
			}

			for (CompassDirection direction : CompassDirection.NESW) {
				final Point2D next_point = cur_point.move(direction);
				if (grid[next_point.y()][next_point.x()] != '#') {
					queue.add(new Node(next_point, new ArrayList<>(cur_node.path), cur_node.cost + 1, direction));
				}
			}
		}

		final List<String> result = new ArrayList<>();
		for (List<Character> path : paths) {
			final StringBuffer sb = new StringBuffer();
			path.forEach(sb::append);
			sb.append('A');
			result.add(sb.toString());
		}

		return result;
	}

	private static record Node(Point2D p, List<Character> path, int cost, CompassDirection direction) {
	}
}
