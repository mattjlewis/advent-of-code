package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;

public class Day4 extends AocBase {
	public static void main(String[] args) {
		new Day4().run();
	}

	private static BingoGame loadData(Path input) throws IOException {
		final int[] numbers = Arrays.stream(Files.lines(input).findFirst().orElseThrow().split(","))
				.mapToInt(Integer::valueOf).toArray();
		Logger.debug(Arrays.toString(numbers));

		final String[] card_lines = Files.lines(input).skip(1).toList().toArray(new String[0]);

		final List<BingoCard> cards = new ArrayList<>();
		BingoCard card = null;
		boolean added_to_list = false;
		for (int i = 0; i < card_lines.length; i++) {
			if (card_lines[i].trim().isEmpty()) {
				if (card != null) {
					cards.add(card);
					added_to_list = true;
				}
				card = new BingoCard(new ArrayList<>(), new ArrayList<>());
			} else {
				card.addRow(Arrays.stream(card_lines[i].trim().split("\\s+")).mapToInt(Integer::parseInt).toArray());
				added_to_list = false;
			}
		}
		if (!added_to_list) {
			cards.add(card);
		}

		return new BingoGame(numbers, cards);
	}

	@Override
	public String part1(Path input) throws IOException {
		final BingoGame bg = loadData(input);

		int winning_card_num = -1;
		int number = 0;
		for (int i = 0; i < bg.numbers.length && winning_card_num == -1; i++) {
			number = bg.numbers[i];
			for (int card_num = 0; card_num < bg.cards.size(); card_num++) {
				BingoCard card = bg.cards.get(card_num);
				card.remove(number);
				if (card.isComplete()) {
					winning_card_num = card_num;
					Logger.debug("card[{}] is complete", card_num);
					break;
				}
			}
		}

		Logger.debug("Winning card #: {}", winning_card_num + 1);
		final BingoCard winning_card = bg.cards.get(winning_card_num);
		Logger.debug("Final number: {}, Winning card sum: {}, Winning card score: {}", number, winning_card.getSum(),
				winning_card.getSum() * number);
		return Integer.toString(winning_card.getSum() * number);
	}

	@Override
	public String part2(Path input) throws IOException {
		final BingoGame bg = loadData(input);

		int last_number = 0;
		BingoCard last_winning_card = null;
		for (int i = 0; i < bg.numbers.length; i++) {
			int number = bg.numbers[i];
			// Remove this number from all remaining incomplete cards
			for (BingoCard card : bg.cards) {
				card.remove(number);
				if (card.isComplete()) {
					last_number = number;
					last_winning_card = card;
				}
			}
			bg.cards.removeIf(BingoCard::isComplete);
		}
		Logger.debug("last_number: {}, sum: {}, score: {}", last_number, last_winning_card.getSum(),
				last_winning_card.getSum() * last_number);
		return Integer.toString(last_winning_card.getSum() * last_number);
	}

	public static record BingoGame(int[] numbers, List<BingoCard> cards) {
		//
	}

	public static record BingoCard(List<List<Integer>> rows, List<List<Integer>> columns) {
		public void addRow(int[] numbers) {
			List<Integer> row = new ArrayList<>();
			for (int i = 0; i < numbers.length; i++) {
				row.add(Integer.valueOf(numbers[i]));

				List<Integer> column;
				if (columns.size() <= i) {
					column = new ArrayList<>();
					columns.add(column);
				} else {
					column = columns.get(i);
				}
				column.add(Integer.valueOf(numbers[i]));
			}
			rows.add(row);
		}

		public void remove(int number) {
			for (List<Integer> row : rows) {
				row.remove(Integer.valueOf(number));
			}
			for (List<Integer> column : columns) {
				column.remove(Integer.valueOf(number));
			}
		}

		public boolean isComplete() {
			for (List<Integer> row : rows) {
				if (row.isEmpty()) {
					Logger.debug("Row is complete");
					return true;
				}
			}
			for (List<Integer> column : columns) {
				if (column.isEmpty()) {
					Logger.debug("Column is complete");
					return true;
				}
			}
			return false;
		}

		public int getSum() {
			int sum = 0;
			for (List<Integer> row : rows) {
				for (Integer val : row) {
					sum += val.intValue();
				}
			}
			return sum;
		}

		public void print() {
			for (List<Integer> row : rows) {
				for (Integer i : row) {
					Logger.debug("{} ", i);
				}
				Logger.debug("");
			}
		}
	}
}
