import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4 {
	private static int[] numbers;
	private static List<Card> cards;

	public static void main(String[] args) {
		// String input_file = "day4sample.txt";
		String input_file = "day4.txt";
		Path input_path = Path.of(input_file);
		try {
			part1(input_path);
			part2(input_path);
		} catch (IOException e) {
			System.out.println("Error unable to read input file '" + input_file + "'");
		}
	}

	private static void loadData(Path input) throws IOException {
		String[] lines = Files.lines(input).collect(Collectors.toList()).toArray(new String[0]);

		numbers = Stream.of(lines[0].split(",")).mapToInt(Integer::valueOf).toArray();

		cards = new ArrayList<>();
		Card card = null;
		boolean added_to_list = false;
		for (int i = 1; i < lines.length; i++) {
			if (lines[i].trim().isEmpty()) {
				if (card != null) {
					cards.add(card);
					added_to_list = true;
				}
				card = new Card();
			} else {
				card.addRow(Stream.of(lines[i].trim().split("\\s+")).mapToInt(Integer::parseInt).toArray());
				added_to_list = false;
			}
		}
		if (!added_to_list) {
			cards.add(card);
		}
	}

	private static void part1(Path input) throws IOException {
		loadData(input);

		int winning_card_num = -1;
		int number = 0;
		for (int i = 0; i < numbers.length && winning_card_num == -1; i++) {
			number = numbers[i];
			for (int card_num = 0; card_num < cards.size(); card_num++) {
				Card card = cards.get(card_num);
				card.remove(number);
				if (card.isComplete()) {
					winning_card_num = card_num;
					System.out.println("card[" + card_num + "] is complete");
					break;
				}
			}
		}

		System.out.println("Winning card #: " + (winning_card_num + 1));
		Card winning_card = cards.get(winning_card_num);
		System.out.format("Final number: %d, Winning card sum: %d, Winning card score: %d%n", number,
				winning_card.getSum(), winning_card.getSum() * number);
	}

	private static void part2(Path input) throws IOException {
		System.out.println("part2");
		loadData(input);

		int last_number = 0;
		Card last_card = null;
		for (int i = 0; i < numbers.length; i++) {
			int number = numbers[i];
			// Remove this number from all remaining incomplete cards
			for (Card card : cards) {
				card.remove(number);
				if (card.isComplete()) {
					last_number = number;
					last_card = card;
				}
			}
			cards.removeIf(Card::isComplete);
		}
		System.out.println("last_number: " + last_number + ", sum: " + last_card.getSum() + ", score: "
				+ last_card.getSum() * last_number);
	}

	public static class Card {
		List<List<Integer>> rows = new ArrayList<>();
		List<List<Integer>> columns = new ArrayList<>();

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
					System.out.println("Row is complete");
					return true;
				}
			}
			for (List<Integer> column : columns) {
				if (column.isEmpty()) {
					System.out.println("Column is complete");
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
					System.out.format("%2d ", i);
				}
				System.out.println();
			}
		}
	}
}
