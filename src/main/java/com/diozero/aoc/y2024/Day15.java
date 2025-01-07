package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.MutablePoint2D;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.StringUtil;
import com.diozero.aoc.util.TextParser;

public class Day15 extends Day {
	public static void main(String[] args) {
		new Day15().run();
	}

	@Override
	public String name() {
		return "Warehouse Woes";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(Puzzle.load(input, false).solve());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(Puzzle.load(input, true).solve());
	}

	public static record Puzzle(char[][] map, MutablePoint2D robotPos, List<CompassDirection> moves, boolean part2) {

		private static final char ROBOT = '@';
		private static final char BLOCK = 'O';
		private static final char LEFT_BLOCK = '[';
		private static final char RIGHT_BLOCK = ']';

		public static Puzzle load(Path input, boolean doubleWidth) throws IOException {
			MutablePoint2D robot_pos = null;
			final List<CompassDirection> moves = new ArrayList<>();

			boolean reading_grid = true;
			final List<String> map_lines = new ArrayList<>();
			int y = 0;
			for (String line : Files.readAllLines(input)) {
				if (line.isBlank()) {
					reading_grid = false;
					continue;
				}

				if (reading_grid) {
					if (doubleWidth) {
						line = line
								.replace(Character.toString(TextParser.UNSET_CHAR),
										StringUtil.repeat(TextParser.UNSET_CHAR, 2))
								.replace(Character.toString(TextParser.SET_CHAR),
										StringUtil.repeat(TextParser.SET_CHAR, 2))
								.replace(Character.toString(BLOCK), StringUtil.toString(LEFT_BLOCK, RIGHT_BLOCK))
								.replace(Character.toString(ROBOT), StringUtil.toString(ROBOT, TextParser.UNSET_CHAR));
					}

					final int at_index = line.indexOf(ROBOT);
					if (at_index != -1) {
						robot_pos = new MutablePoint2D(at_index, y);
						line = line.replace(ROBOT, TextParser.UNSET_CHAR);
					}

					map_lines.add(line);
					y++;
				} else {
					line.chars().mapToObj(CompassDirection::fromArrow).forEach(moves::add);
				}
			}

			if (robot_pos == null) {
				throw new IllegalStateException("Couldn't locate robot");
			}

			return new Puzzle(TextParser.loadCharMatrix(map_lines.stream()), robot_pos, moves, doubleWidth);
		}

		public int solve() {
			moves.forEach(this::move);

			int score = 0;
			for (int y = 0; y < map.length; y++) {
				for (int x = 0; x < map[0].length; x++) {
					if (map[y][x] == BLOCK || map[y][x] == LEFT_BLOCK) {
						score += 100 * y + x;
					}
				}
			}

			return score;
		}

		private void move(CompassDirection direction) {
			// Must be a sequenced collection as the blocks have to be moved in FIFO order
			final SequencedCollection<Point2D> moved_blocks = new ArrayList<>();

			if (canMove(robotPos.immutable(), direction, moved_blocks)) {
				// Move the blocks in FIFO order
				moved_blocks.forEach(p -> {
					final Point2D new_p = p.move(direction);

					map[new_p.y()][new_p.x()] = map[p.y()][p.x()];
					map[p.y()][p.x()] = TextParser.UNSET_CHAR;
				});
				robotPos.translate(direction);
			}
		}

		private boolean canMove(Point2D p, CompassDirection direction, Collection<Point2D> movedBlocks) {
			if (movedBlocks.contains(p)) {
				return true;
			}

			final Point2D new_p = p.move(direction);

			if (map[new_p.y()][new_p.x()] == TextParser.SET_CHAR) {
				return false;
			}

			if (part2) {
				if (direction == CompassDirection.EAST) {
					if (map[new_p.y()][new_p.x()] == LEFT_BLOCK) {
						if (!canMove(new_p.move(direction), direction, movedBlocks)) {
							return false;
						}
						movedBlocks.add(new_p);
					}
				} else if (direction == CompassDirection.WEST) {
					if (map[new_p.y()][new_p.x()] == RIGHT_BLOCK) {
						if (!canMove(new_p.move(direction), direction, movedBlocks)) {
							return false;
						}
						movedBlocks.add(new_p);
					}
				} else {
					if (map[new_p.y()][new_p.x()] == LEFT_BLOCK && (!canMove(new_p, direction, movedBlocks)
							|| !canMove(new_p.move(CompassDirection.EAST), direction, movedBlocks))) {
						return false;
					}
					if (map[new_p.y()][new_p.x()] == RIGHT_BLOCK && (!canMove(new_p, direction, movedBlocks)
							|| !canMove(new_p.move(CompassDirection.WEST), direction, movedBlocks))) {
						return false;
					}
				}
			} else if (map[new_p.y()][new_p.x()] == BLOCK && !canMove(new_p, direction, movedBlocks)) {
				return false;
			}

			movedBlocks.add(p);

			return true;
		}
	}
}
