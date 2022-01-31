package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.MutablePoint2D;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.PrintUtil;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

public class Day13 extends Day {
	public static void main(String[] args) {
		new Day13().run();
	}

	@Override
	public String name() {
		return "Care Package";
	}

	@Override
	public String part1(Path input) throws IOException {
		BreakoutGame ac = new BreakoutGame();
		IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input, null, ac::instruction);

		vm.run();
		if (Logger.isDebugEnabled()) {
			PrintUtil.print(ac.tiles, BreakoutGame.EMPTY_CHAR, BreakoutGame.Tile::valueOf);
		}

		return Long.toString(ac.tiles.values().stream().filter(tile -> tile == BreakoutGame.Tile.BLOCK).count());
	}

	@Override
	public String part2(Path input) throws IOException {
		BreakoutGame game = new BreakoutGame();
		IntcodeVirtualMachine vm = IntcodeVirtualMachine.load(input, game::move, game::instruction);

		// Memory address 0 represents the number of quarters that have been inserted;
		// set it to 2 to play for free.
		vm.store(0, 2);

		// Run the VM until the game finishes (no more blocks or the ball goes below the
		// paddle)
		vm.run();
		if (Logger.isDebugEnabled()) {
			PrintUtil.print(game.tiles, BreakoutGame.EMPTY_CHAR, BreakoutGame.Tile::valueOf);
			Logger.debug("--- Score: {} ---", Integer.valueOf(game.score));
		}

		return Integer.toString(game.score);
	}

	public static class BreakoutGame {
		static final char EMPTY_CHAR = PrintUtil.BLANK_PIXEL;

		enum Tile {
			EMPTY, WALL, BLOCK, PADDLE, BALL;

			public static char valueOf(Tile tile) {
				return switch (tile) {
				case EMPTY -> EMPTY_CHAR;
				case WALL -> PrintUtil.FILLED_PIXEL;
				case BLOCK -> '#';
				case PADDLE -> '_';
				case BALL -> 'o';
				default -> throw new IllegalArgumentException("Invalid tile " + tile);
				};
			}
		}

		private int index;
		private MutablePoint2D position;
		private Map<Point2D, Tile> tiles;
		private int score;
		private MutablePoint2D ballPosition;
		private MutablePoint2D paddlePosition;

		BreakoutGame() {
			position = new MutablePoint2D(0, 0);
			tiles = new HashMap<>();
		}

		void instruction(long instruction) {
			switch (index) {
			case 0:
				position.setX((int) instruction);
				index++;
				break;
			case 1:
				position.setY((int) instruction);
				index++;
				break;
			case 2:
				if (position.x() == -1 && position.y() == 0) {
					score = (int) instruction;
				} else {
					Tile tile = Tile.values()[(int) instruction];
					tiles.put(position.immutable(), tile);

					if (tile == Tile.BALL) {
						if (ballPosition == null) {
							ballPosition = position.clone();
						} else {
							ballPosition.set(position.x(), position.y());
						}
					} else if (tile == Tile.PADDLE) {
						if (paddlePosition == null) {
							paddlePosition = position.clone();
						} else {
							paddlePosition.set(position.x(), position.y());
						}
					}
				}

				index = 0;

				break;
			default:
				// Not possible
				throw new IllegalArgumentException();
			}
		}

		long move() {
			// Move the paddle towards the ball on the X axis
			return Integer.compare(ballPosition.x(), paddlePosition.x());
		}
	}
}
