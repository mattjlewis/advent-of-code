package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;

public class Day4 extends Day {
	public static void main(String[] args) {
		new Day4().run();
	}

	@Override
	public String name() {
		return "Scratchcards";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(Files.lines(input).map(Scratchcard::parse).mapToInt(Scratchcard::calcScore).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Scratchcard> cards = Files.lines(input).map(Scratchcard::parse).toList();
		cards.forEach(c -> c.adjustCardCopies(cards));
		return Integer.toString(cards.stream().map(Scratchcard::numCopies).mapToInt(AtomicInteger::get).sum());
	}

	private static final record Scratchcard(int num, List<Integer> winningNumbers, List<Integer> numbers,
			List<Integer> cardWinningNumbers, AtomicInteger numCopies) {

		private static final Pattern SCRATCHCARD_PATTERN = Pattern
				.compile("^Card\\s+(?<num>\\d+):\\s+(?<winningnumbers>.*)\\s+\\|\\s+(?<numbers>.*)$");

		public static Scratchcard parse(String line) {
			final Matcher m = SCRATCHCARD_PATTERN.matcher(line);
			if (!m.matches()) {
				throw new IllegalArgumentException("Invalid line '" + line + "'");
			}

			return Scratchcard.create(Integer.parseInt(m.group("num")),
					Arrays.stream(m.group("winningnumbers").split("\\s+")).map(Integer::valueOf).toList(),
					Arrays.stream(m.group("numbers").split("\\s+")).map(Integer::valueOf).toList());
		}

		private static Scratchcard create(int num, List<Integer> winningNumbers, List<Integer> numbers) {
			return new Scratchcard(num, winningNumbers, numbers,
					numbers.stream().filter(winningNumbers::contains).toList(), new AtomicInteger(1));
		}

		public int calcScore() {
			return (int) Math.pow(2, cardWinningNumbers.size() - 1);
		}

		public void adjustCardCopies(List<Scratchcard> cards) {
			/*
			 * XXX Note that card.num starts at 1 rather than 0 hence the range function
			 * starts at num, not num+1
			 */
			IntStream.range(num, num + cardWinningNumbers.size()).mapToObj(cards::get)
					.forEach(card -> card.numCopies.addAndGet(numCopies.get()));
		}
	}
}
