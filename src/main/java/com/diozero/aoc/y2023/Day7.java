package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;

public class Day7 extends Day {
	public static void main(String[] args) {
		new Day7().run();
	}

	@Override
	public String name() {
		return "Camel Cards";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final AtomicInteger i = new AtomicInteger(1);

		return Integer.toString(
				Files.lines(input).map(Hand::parse).sorted().mapToInt(hand -> i.getAndIncrement() * hand.bid()).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final AtomicInteger i = new AtomicInteger(1);

		// Replace all Jacks with Jokers
		return Integer.toString(Files.lines(input)
				.map(line -> line.replace(Character.toString(Card.JACK.value), Character.toString(Card.JOKER.value)))
				.map(Hand::parse).sorted().mapToInt(hand -> i.getAndIncrement() * hand.bid()).sum());
	}

	private static enum HandType {
		// Order is important - lowest to highest
		HIGH_CARD, PAIR, TWO_PAIRS, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND;

		public static HandType of(List<Card> hand) {
			final Map<Card, Long> card_count_map = hand.stream()
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			int num_jokers = card_count_map.getOrDefault(Card.JOKER, Long.valueOf(0)).intValue();
			card_count_map.remove(Card.JOKER);

			final List<CardCount> sorted_card_counts = card_count_map.entrySet().stream().map(CardCount::create)
					.sorted().collect(Collectors.toList());

			if (sorted_card_counts.isEmpty()) {
				// All Jokers
				sorted_card_counts.add(new CardCount(Card.JOKER, 5));
			} else if (num_jokers > 0) {
				// Add all of the jokers to the first card count as that is the best option
				final CardCount first = sorted_card_counts.removeFirst();
				sorted_card_counts.addFirst(new CardCount(first.card, first.count + num_jokers));
			}

			return switch (sorted_card_counts.size()) {
			case 1 -> FIVE_OF_A_KIND;
			case 2 -> sorted_card_counts.get(0).count == 4 ? FOUR_OF_A_KIND : FULL_HOUSE;
			case 3 -> sorted_card_counts.get(0).count == 3 ? THREE_OF_A_KIND : TWO_PAIRS;
			case 4 -> PAIR;
			case 5 -> HIGH_CARD;
			default -> throw new IllegalArgumentException(
					"Invalid number of cards " + sorted_card_counts.size() + " in " + sorted_card_counts);
			};
		}
	}

	private static record Hand(List<Card> hand, HandType type, int bid) implements Comparable<Hand> {
		public static Hand parse(String line) {
			final String[] parts = line.split(" ");
			final List<Card> cards = parts[0].trim().chars().mapToObj(Card::of).toList();

			return new Hand(cards, HandType.of(cards), Integer.parseInt(parts[1].trim()));
		}

		@Override
		public int compareTo(Hand other) {
			// First compare the hand type
			int hand_type_compare = type.compareTo(other.type);
			if (hand_type_compare != 0) {
				return hand_type_compare;
			}

			// Same hand type so find and compare the first different card
			return IntStream.range(0, hand.size()).map(i -> hand.get(i).compareTo(other.hand.get(i)))
					.filter(i -> i != 0).findFirst().orElse(0);
		}
	}

	private static enum Card {
		// Order is important - lowest to highest
		JOKER('*'), TWO('2'), THREE('3'), FOUR('4'), FIVE('5'), SIX('6'), SEVEN('7'), EIGHT('8'), NINE('9'), TEN('T'),
		JACK('J'), QUEEN('Q'), KING('K'), ACE('A');

		private static final Map<Character, Card> VALUE_MAP = Arrays.stream(Card.values())
				.collect(Collectors.toMap(c -> Character.valueOf(c.value), Function.identity()));

		private char value;

		private Card(char value) {
			this.value = value;
		}

		public static Card of(int ch) {
			final Card card = VALUE_MAP.get(Character.valueOf((char) ch));
			if (card == null) {
				throw new IllegalArgumentException("Invalid card '" + (char) ch + "'");
			}

			return card;
		}
	}

	private static record CardCount(Card card, int count) implements Comparable<CardCount> {

		public static CardCount create(Map.Entry<Card, Long> entry) {
			return new CardCount(entry.getKey(), entry.getValue().intValue());
		}

		@Override
		public int compareTo(CardCount other) {
			return 100 * (other.count - count) + card.compareTo(other.card);
		}
	}
}
