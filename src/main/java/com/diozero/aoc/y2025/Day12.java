package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point2D;

public class Day12 extends Day {
	public static void main(String[] args) {
		new Day12().run();
	}

	@Override
	public String name() {
		return "Christmas Tree Farm";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Iterator<String> it = Files.readAllLines(input).iterator();
		boolean reading_shapes = true;
		final Map<Integer, Shape> shapes = new HashMap<>();
		while (it.hasNext()) {
			final String line = it.next().trim();
			if (reading_shapes && line.contains("x")) {
				reading_shapes = false;
			}
			if (reading_shapes) {
				final Integer shape_index = Integer.valueOf(line.substring(0, line.length() - 1));
				final Set<Point2D> points = new HashSet<>();
				for (int y = 0; y < 3; y++) {
					final String row = it.next();
					for (int x = 0; x < row.length(); x++) {
						if (row.charAt(x) == '#') {
							points.add(new Point2D(x, y));
						}
					}
				}
				shapes.put(shape_index, new Shape(points));
				it.next();
			}
		}
		System.out.println(shapes);

		main(Files.readAllLines(input).iterator());
		return "";
	}

	@Override
	public String part2(final Path input) throws IOException {
		return "";
	}

	private static void main(Iterator<String> lines) {
		final Pattern re = Pattern.compile("\\d+");

		int res = 0, n = 0;
		final Map<Integer, Integer> shapes = new HashMap<>();

		String line = lines.next();
		while (lines.hasNext()) {
			if (line.length() == 2 && line.charAt(1) == ':') {
				shapes.put(Integer.valueOf(n++), Integer.valueOf(
						(int) (lines.next() + lines.next() + lines.next()).chars().filter(c -> c == '#').count()));
			} else if (line.contains("x")) {
				final int[] region_nums = re.matcher(line).results().mapToInt(r -> Integer.parseInt(r.group()))
						.toArray();
				final int area = region_nums[0] * region_nums[1];
				final List<Integer> shape_ids = IntStream.of(region_nums).skip(2).boxed().toList();
				int s = 0;
				for (int i = 0; i < shape_ids.size(); i++) {
					s += shape_ids.get(i).intValue() * shapes.get(Integer.valueOf(i)).intValue();
				}
				if (s <= area) {
					res++;
				}
			}
			line = lines.next();
		}
		System.out.println(res);
	}

	private static record Shape(Set<Point2D> points) {

	}
}
