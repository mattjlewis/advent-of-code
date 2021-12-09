import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Day5 {
	public static void main(String[] args) {
		// String input_file = "day5sample.txt";
		String input_file = "day5.txt";
		Path input_path = Path.of(input_file);
		try {
			part1(input_path);
			System.out.println();
			part2(input_path);
		} catch (IOException e) {
			System.out.println("Error unable to read input file '" + input_file + "'");
		}
	}

	private static void print(Map<Point, AtomicInteger> counts) {
		int max_x = counts.keySet().stream().mapToInt(point -> point.x).max().getAsInt();
		int max_y = counts.keySet().stream().mapToInt(point -> point.y).max().getAsInt();
		System.out.println("max_x: " + max_x + ", max_y: " + max_y);
		if (max_x < 20 && max_y < 20) {
			for (int y = 0; y <= max_y; y++) {
				for (int x = 0; x <= max_x; x++) {
					Point p = new Point(x, y);
					AtomicInteger count = counts.get(p);
					if (count == null) {
						System.out.print(".");
					} else {
						System.out.print(count.get());
					}
				}
				System.out.println();
			}
		}
	}

	private static List<Line> loadData(Path input) throws IOException {
		return Files.lines(input).map(Line::new).collect(Collectors.toList());
	}

	private static void part1(Path input) throws IOException {
		List<Line> lines = loadData(input);
		lines.removeIf(Line::isDiagonal);

		Map<Point, AtomicInteger> counts = new HashMap<>();
		for (Line line : lines) {
			if (line.x1 == line.x2) {
				for (int y = line.getMinY(); y <= line.getMaxY(); y++) {
					incrementCount(counts, line.x1, y);
				}
			} else {
				for (int x = line.getMinX(); x <= line.getMaxX(); x++) {
					incrementCount(counts, x, line.y1);
				}
			}
		}

		print(counts);

		long num = counts.values().stream().filter(count -> count.get() >= 2).count();
		System.out.println("part1: " + num);
	}

	private static void part2(Path input) throws IOException {
		List<Line> lines = loadData(input);

		Map<Point, AtomicInteger> counts = new HashMap<>();
		for (Line line : lines) {
			if (line.isDiagonal()) {
				int y = line.y1;
				// Lines can only be at 45 degrees
				for (int x = line.x1;;) {
					incrementCount(counts, x, y);

					if (line.x1 > line.x2) {
						if (--x < line.x2) {
							break;
						}
					} else {
						if (++x > line.x2) {
							break;
						}
					}
					if (line.y1 > line.y2) {
						y--;
					} else {
						y++;
					}
				}
			} else {
				// Horizontal or vertical?
				if (line.x1 == line.x2) {
					for (int y = line.getMinY(); y <= line.getMaxY(); y++) {
						incrementCount(counts, line.x1, y);
					}
				} else {
					for (int x = line.getMinX(); x <= line.getMaxX(); x++) {
						incrementCount(counts, x, line.y1);
					}
				}
			}
		}

		print(counts);

		long num = counts.values().stream().filter(count -> count.get() > 1).count();
		System.out.println("part2: " + num);
	}

	private static void incrementCount(Map<Point, AtomicInteger> counts, int x, int y) {
		Point p = new Point(x, y);
		AtomicInteger count = counts.get(p);
		if (count == null) {
			count = new AtomicInteger();
			counts.put(p, count);
		}
		count.incrementAndGet();
	}
}
