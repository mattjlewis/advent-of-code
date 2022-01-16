package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.tinylog.Logger;

import com.diozero.aoc.Day;

public class Day22 extends Day {
	public static void main(String[] args) {
		new Day22().run();
	}

	@Override
	public String name() {
		return "Crab Combat";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Deque<Integer>> players = loadData(input);

		final Deque<Integer> player1 = players.get(0);
		final Deque<Integer> player2 = players.get(1);

		final int winning_player_no = playGame(player1, player2, false);

		Logger.debug("== Post-game results ==");
		Logger.debug("Player 1's deck: {}", player1);
		Logger.debug("Player 2's deck: {}", player2);

		return Integer.toString(calculateDeckId(players.get(winning_player_no)));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Deque<Integer>> players = loadData(input);

		final Deque<Integer> player1 = players.get(0);
		final Deque<Integer> player2 = players.get(1);

		int winning_player_no = 0;
		while (!player1.isEmpty() && !player2.isEmpty()) {
			winning_player_no = playGame(player1, player2, true);
		}

		Logger.debug("== Post-game results ==");
		Logger.debug("Player 1's deck: {}", player1);
		Logger.debug("Player 2's deck: {}", player2);

		return Integer.toString(calculateDeckId(players.get(winning_player_no)));
	}

	private static int playGame(final Deque<Integer> player1, final Deque<Integer> player2, final boolean recursive) {
		if (recursive) {
			Logger.debug("=== New Game ===");
		}

		final Set<Integer> p1_previous_rounds = new HashSet<>();
		final Set<Integer> p2_previous_rounds = new HashSet<>();

		int round = 1;
		int winning_player_no;
		while (true) {
			if (recursive) {
				/*
				 * Before either player deals a card, if there was a previous round in this game
				 * that had exactly the same cards in the same order in the same players' decks,
				 * the game instantly ends in a win for player 1.
				 */
				Integer p1_round_id = Integer.valueOf(calculateDeckId(player1));
				Integer p2_round_id = Integer.valueOf(calculateDeckId(player2));
				if (p1_previous_rounds.contains(p1_round_id) && p2_previous_rounds.contains(p2_round_id)) {
					Logger.debug("Recursion detected in round {}, player 1 wins game!", round);
					winning_player_no = 0;
					break;
				}

				p1_previous_rounds.add(p1_round_id);
				p2_previous_rounds.add(p2_round_id);
			}

			Logger.debug("-- Round {}{} --", Integer.valueOf(round), recursive ? " (Recursive)" : "");
			Logger.debug("Player 1's deck: {}", player1);
			Logger.debug("Player 2's deck: {}", player2);

			Integer p1_card = player1.remove();
			Integer p2_card = player2.remove();
			Logger.debug("Player 1 plays: {}", p1_card);
			Logger.debug("Player 2 plays: {}", p2_card);

			/*
			 * If both players have at least as many cards in their own decks as the number
			 * on the card they just dealt, the winner of the round is determined by
			 * recursing into a sub-game of Recursive Combat.
			 */
			if (recursive && player1.size() >= p1_card.intValue() && player2.size() >= p2_card.intValue()) {
				/*
				 * To play a sub-game of Recursive Combat, each player creates a new deck by
				 * making a copy of the next cards in their deck (the quantity of cards copied
				 * is equal to the number on the card they drew to trigger the sub-game).
				 *
				 * During this sub-game, the game that triggered it is on hold and completely
				 * unaffected; no cards are removed from players' decks to form the sub-game.
				 * For example, if player 1 drew the 3 card, their deck in the sub-game would be
				 * copies of the next three cards in their deck.
				 */
				Logger.debug("Playing a sub-game to determine the winner...");
				winning_player_no = playGame(copy(player1, p1_card.intValue()), copy(player2, p2_card.intValue()),
						recursive);
				Logger.debug("...anyway, back to the previous game.");
			} else {
				winning_player_no = p1_card.compareTo(p2_card) > 0 ? 0 : 1;
			}

			Logger.debug("Player {} wins the round!", winning_player_no + 1);
			if (winning_player_no == 0) {
				player1.offer(p1_card);
				player1.offer(p2_card);
			} else {
				player2.offer(p2_card);
				player2.offer(p1_card);
			}

			if (player1.isEmpty() || player2.isEmpty()) {
				break;
			}

			round++;
		}

		return winning_player_no;
	}

	private static int calculateDeckId(final Deque<Integer> deck) {
		final AtomicInteger index = new AtomicInteger(deck.size());
		return deck.stream().mapToInt(i -> i.intValue() * index.getAndDecrement()).sum();
	}

	private static <T> Deque<T> copy(final Deque<T> deque, final int maxSize) {
		final Deque<T> copy = new ArrayDeque<>(deque);
		for (int i = maxSize; i < deque.size(); i++) {
			copy.removeLast();
		}
		return copy;
	}

	private static List<Deque<Integer>> loadData(final Path input) throws IOException {
		final List<Deque<Integer>> decks = new ArrayList<>();

		Deque<Integer> deck = new ArrayDeque<>();
		decks.add(deck);
		for (String line : Files.readAllLines(input)) {
			if (line.isBlank()) {
				deck = new ArrayDeque<>();
				decks.add(deck);
				continue;
			}

			if (line.startsWith("Player ")) {
				continue;
			}

			deck.offer(Integer.valueOf(line));
		}

		return decks;
	}
}
