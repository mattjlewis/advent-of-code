package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.geometry.Point2DLong;
import com.diozero.aoc.geometry.Rectangle;
import com.diozero.aoc.util.CircularLinkedList;
import com.diozero.aoc.util.TextParser;

public class Day17 extends Day {
	private static final int WIDTH = 7;
	private static final int START_POS_X = 2;
	private static final int START_POS_Y_DELTA = 3;

	private static final List<Collection<Point2D>> SHAPES = Arrays.asList(loadShape("####"), loadShape(".#.\n###\n.#."),
			loadShape("###\n..#\n..#"), loadShape("#\n#\n#\n#"), loadShape("##\n##"));

	public static void main(String[] args) {
		new Day17().run();
	}

	@Override
	public String name() {
		return "Pyroclastic Flow";
	}

	@Override
	public String part1(Path input) throws IOException {
		final CircularLinkedList<Collection<Point2D>> shapes = new CircularLinkedList<>(SHAPES);
		final CircularLinkedList<CompassDirection> directions = new CircularLinkedList<>(Files.lines(input).limit(1)
				.flatMapToInt(String::chars).mapToObj(i -> CompassDirection.fromArrow(i)).toList());

		final Collection<Point2DLong> cave = new HashSet<>();
		/*-Collection<Point2DLong> cave = IntStream.range(0, 7).mapToObj(x -> new Point2DLong(x, 0))
				.collect(Collectors.toList());*/

		Rock current_rock = Rock.create(shapes.poll(), START_POS_Y_DELTA);
		for (int i = 0; i < 2022; i++) {
			boolean at_bottom;
			do {
				final CompassDirection dir = directions.poll();
				current_rock.move(dir, cave);
				at_bottom = current_rock.move(CompassDirection.SOUTH, cave);
				if (at_bottom) {
					current_rock.addTo(cave);
					final long top_y = cave.stream().mapToLong(Point2DLong::y).max().orElseThrow() + 1;
					current_rock = Rock.create(shapes.poll(), START_POS_Y_DELTA + top_y);
				}
			} while (!at_bottom);
		}

		return Long.toString(cave.stream().mapToLong(Point2DLong::y).max().orElseThrow() + 1);
	}

	@Override
	public String part2(Path input) throws IOException {
		final CircularLinkedList<Collection<Point2D>> shapes = new CircularLinkedList<>(SHAPES);
		final CircularLinkedList<CompassDirection> directions = new CircularLinkedList<>(Files.lines(input).limit(1)
				.flatMapToInt(String::chars).mapToObj(i -> CompassDirection.fromArrow(i)).toList());

		int num_directions = directions.size();
		if (num_directions < 200) {
			num_directions *= 200 / num_directions;
		}

		final long max_rocks = 1_000_000_000_000L;

		// Set<Point2DLong> cave = new HashSet<>();
		Collection<Point2DLong> cave = IntStream.range(0, 7).mapToObj(x -> new Point2DLong(x, 0))
				.collect(Collectors.toList());

		long height = 0, rocks = 0;
		long first_cycle_height = 0, first_cycle_rocks = 0;
		long next_cycle_height, next_cycle_rocks = 0;

		Rock current_rock = Rock.create(shapes.poll(), START_POS_Y_DELTA + 1);
		for (int i = 0; rocks < max_rocks; i++) {
			if (i >= num_directions) {
				i = 0;
			}

			if (first_cycle_rocks == 0 && i == 0 && rocks > 0) {
				first_cycle_rocks = rocks;
				first_cycle_height = height;
			} else if (first_cycle_rocks > 0 && next_cycle_rocks == 0 && i == 0 && rocks > first_cycle_rocks) {
				next_cycle_rocks = rocks;
				next_cycle_height = height;
				final long rocks_per_cycle = next_cycle_rocks - first_cycle_rocks;
				final long number_of_cycles = (max_rocks - first_cycle_rocks) / rocks_per_cycle - 1;
				final long add_height = (next_cycle_height - first_cycle_height) * number_of_cycles;

				rocks += rocks_per_cycle * number_of_cycles;
				height += add_height;

				cave = cave.stream().map(p -> p.translate(0, add_height)).collect(Collectors.toList());
				current_rock.y.addAndGet(add_height);
			}

			current_rock.move(directions.poll(), cave);
			if (current_rock.move(CompassDirection.SOUTH, cave)) {
				// At the bottom
				current_rock.addTo(cave);
				height = Math.max(current_rock.topY(), height);
				current_rock = Rock.create(shapes.poll(), START_POS_Y_DELTA + height);
				rocks++;
			}
		}

		return Long.toString(height - 1);
	}

	static Collection<Point2D> loadShape(String pattern) {
		return TextParser.loadPoints(Arrays.stream(pattern.split("\\n")));
	}

	private static record Rock(Collection<Point2D> points, Rectangle bounds, AtomicInteger x, AtomicLong y) {

		public static Rock create(Collection<Point2D> points, long topY) {
			final Rectangle bounds = Point2D.getBounds(points);
			return new Rock(points, bounds, new AtomicInteger(START_POS_X), new AtomicLong(topY));
		}

		public long topY() {
			return y.get() + bounds.height();
		}

		public boolean move(CompassDirection dir, Collection<Point2DLong> cave) {
			switch (dir) {
			case EAST:
				// Can we move within the side walls?
				if (x.get() + bounds.width() < WIDTH) {
					// Can we move and not overlap with any points in the cave?
					final int dx = dir == CompassDirection.EAST ? 1 : -1;
					if (points.stream().map(p -> p.translate(x.get() + dx, y.get())).noneMatch(cave::contains)) {
						x.incrementAndGet();
					}
				}
				break;
			case WEST:
				// Can we move within the side walls?
				if (x.get() > 0) {
					// Can we move and not overlap with any points in the cave?
					final int dx = dir == CompassDirection.EAST ? 1 : -1;
					if (points.stream().map(p -> p.translate(x.get() + dx, y.get())).noneMatch(cave::contains)) {
						x.decrementAndGet();
					}
				}
				break;
			case SOUTH:
				if (y.get() > 0) {
					// Can we move and not overlap with any points in the cave?
					if (points.stream().map(p -> p.translate(x.get(), y.get() - 1)).noneMatch(cave::contains)) {
						y.decrementAndGet();
					} else {
						return true;
					}
				} else {
					return true;
				}
				break;
			default:
				// Ignore
			}

			return false;
		}

		public void addTo(Collection<Point2DLong> cave) {
			points.stream().map(p -> p.translate(x.get(), y.get())).forEach(cave::add);
		}
	}
}
