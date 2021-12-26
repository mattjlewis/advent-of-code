package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;

public class Day21 extends AocBase {
	public static void main(String[] args) {
		new Day21().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		List<MutablePlayer> players = loadData(input);
		Logger.debug("players: {}", players);

		int max_score = 1000;
		Dice dice = new DeterministicDice(100);
		boolean game_over = false;
		while (!game_over) {
			for (MutablePlayer player : players) {
				int move = dice.roll(3);
				Logger.trace("move: {}", move);
				if (player.move(move) >= max_score) {
					game_over = true;
					break;
				}
			}
			Logger.trace("players: {}", players);
		}

		Logger.debug("players: {}, numRolls: {}", players, dice.getNumRolls());

		return dice.getNumRolls() * players.get(1).getScore();
	}

	private static List<MutablePlayer> loadData(Path input) throws IOException {
		return Files.lines(input).map(Day21::extractPlayerAndPosition)
				.sorted((a, b) -> Integer.compare(a.getId(), b.getId())).toList();
	}

	private static MutablePlayer extractPlayerAndPosition(String line) {
		Pattern p = Pattern.compile("Player (\\d+) starting position: (\\d+)");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			return new MutablePlayer(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
		}
		throw new IllegalArgumentException("Cannot parse line '" + line + "'");
	}

	private static abstract class Dice {
		protected int sides;
	
		protected Dice(int sides) {
			this.sides = sides;
		}
	
		public abstract int roll();
	
		public abstract int roll(int times);
	
		public abstract int getNumRolls();
	}

	private static class DeterministicDice extends Dice {
		private int numRolls = 0;
	
		public DeterministicDice(int sides) {
			super(sides);
		}
	
		@Override
		public int roll() {
			return (numRolls++ % sides) + 1;
		}
	
		@Override
		public int roll(int times) {
			int val = 0;
	
			for (int i = 0; i < times; i++) {
				val += roll();
			}
	
			return val;
		}
	
		@Override
		public int getNumRolls() {
			return numRolls;
		}
	}

	private static class MutablePlayer {
		private int id;
		private int pos;
		private int score;
	
		public MutablePlayer(int id, int pos) {
			this.id = id;
			this.pos = pos - 1;
		}
	
		public int getId() {
			return id;
		}
	
		public int getPos() {
			return pos;
		}
	
		public int getScore() {
			return score;
		}
	
		public int move(int move) {
			pos = (pos + move) % 10;
			score += pos + 1;
			return score;
		}
	
		@Override
		public String toString() {
			return "MutablePlayer [id=" + id + ", pos=" + (pos + 1) + ", score=" + score + "]";
		}
	}

	@Override
	public long part2(Path input) throws IOException {
		List<Player> players = loadData(input).stream().map(p -> new Player(p.getPos(), 0)).toList();
		Logger.debug("players: {}", players);

		long[] res = play(21, new State(0, 0, 0, players.get(0), players.get(1)), new HashMap<>());

		return Math.max(res[0], res[1]);
	}

	// Return an array containing number of player 1 and 2 wins
	private static long[] play(int maxScore, State state, Map<State, long[]> cache) {
		long[] wins = cache.get(state);

		if (wins == null) {
			wins = new long[] { 0, 0 };

			for (int roll = 1; roll <= 3; roll++) {
				long[] this_round_wins = playRound(maxScore, roll, state, cache);

				wins[0] += this_round_wins[0];
				wins[1] += this_round_wins[1];
			}

			cache.put(state, wins);
		}

		return wins;
	}

	private static long[] playRound(int maxScore, int currentRoll, State state, Map<State, long[]> cache) {
		State new_state = state.roll(currentRoll);

		if (new_state.isGameOver(maxScore)) {
			return new_state.playerTurn() == 0 ? new long[] { 1, 0 } : new long[] { 0, 1 };
		}

		return play(maxScore, new_state, cache);
	}

	private static record Player(int pos, int score) {
		public Player move(int move) {
			int new_pos = (this.pos + move) % 10;
			return new Player(new_pos, score + new_pos + 1);
		}

		@Override
		public String toString() {
			return "Player [pos=" + (pos + 1) + ", score=" + score + "]";
		}
	}

	private static record State(int totalRolls, int prevRoll, int prevRoll2, Player p1, Player p2) {
		public boolean isGameOver(int maxScore) {
			return p1.score >= maxScore || p2.score >= maxScore;
		}

		public int playerTurn() {
			return (totalRolls / 3) % 2;
		}

		public State roll(int currentRoll) {
			Player new_p1 = this.p1;
			Player new_p2 = this.p2;

			// IMove the player on the third roll
			if (totalRolls % 3 == 2) {
				final int move = currentRoll + prevRoll + prevRoll2;
				if (playerTurn() == 0) {
					new_p1 = this.p1.move(move);
				} else {
					new_p2 = this.p2.move(move);
				}
			}

			return new State(totalRolls + 1, currentRoll, prevRoll, new_p1, new_p2);
		}
	}
}
