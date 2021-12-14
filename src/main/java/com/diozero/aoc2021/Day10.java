package com.diozero.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import com.diozero.aoc2021.util.AocBase;

public class Day10 extends AocBase {
	private static final List<Character> OPEN_CHARS = Arrays.asList('(', '[', '{', '<');
	private static final List<Character> CLOSE_CHARS = Arrays.asList(')', ']', '}', '>');
	private static final Map<Character, Integer> PART1_ERROR_SCORES;
	static {
		PART1_ERROR_SCORES = new HashMap<>();
		PART1_ERROR_SCORES.put(')', 3);
		PART1_ERROR_SCORES.put(']', 57);
		PART1_ERROR_SCORES.put('}', 1197);
		PART1_ERROR_SCORES.put('>', 25137);
	}

	public static void main(String[] args) {
		new Day10().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		return Files.lines(input).map(Day10::findClosing)
				.filter(result -> result.isIncomplete() && result.isCorrupted())
				.mapToInt(result -> PART1_ERROR_SCORES.get(result.getOpenChar()).intValue()).sum();
	}

	@Override
	public long part2(Path input) throws IOException {
		final long[] scores = Files.lines(input).map(Day10::findClosing)
				.filter(result -> result.isIncomplete() && result.isUncorrupted())
				.mapToLong(result -> result.getCloseCharStack().stream().mapToLong(ch -> 1 + CLOSE_CHARS.indexOf(ch))
						.reduce(0, (a, b) -> 5 * a + b))
				.sorted().toArray();
		Logger.debug("Scores: " + Arrays.toString(scores));
		return scores[scores.length / 2];
	}

	private static Result findClosing(String line) {
		// Don't use Stack as Stack.stream() returns items in FIFO order, not LIFO
		final Deque<Character> close_char_stack = new ArrayDeque<>();
		for (int i = 0; i < line.length(); i++) {
			final Character ch = Character.valueOf(line.charAt(i));
			final int open_char_index = OPEN_CHARS.indexOf(ch);
			if (open_char_index != -1) {
				close_char_stack.push(CLOSE_CHARS.get(open_char_index));
			} else {
				final Character expected_close_char = close_char_stack.pop();
				if (!ch.equals(expected_close_char)) {
					return new Result(ch.charValue(), true);
				}
			}
		}

		if (!close_char_stack.isEmpty()) {
			return new Result(close_char_stack, false);
		}

		return Result.success();
	}

	private static final class Result {
		private static Result SUCCESS = new Result();

		private final boolean complete;
		private final Character openChar;
		private final boolean corrupted;
		private final Deque<Character> closeCharStack;

		private Result() {
			complete = true;
			openChar = null;
			corrupted = false;
			closeCharStack = null;
		}

		public static Result success() {
			return SUCCESS;
		}

		public Result(Deque<Character> closeCharStack, boolean corrupted) {
			complete = false;
			this.openChar = null;
			this.corrupted = corrupted;
			this.closeCharStack = closeCharStack;
		}

		public Result(char openChar, boolean corrupted) {
			complete = false;
			this.openChar = Character.valueOf(openChar);
			this.corrupted = corrupted;
			closeCharStack = null;
		}

		public boolean isIncomplete() {
			return !complete;
		}

		public Character getOpenChar() {
			return openChar;
		}

		public boolean isCorrupted() {
			return corrupted;
		}

		public boolean isUncorrupted() {
			return !corrupted;
		}

		public Deque<Character> getCloseCharStack() {
			return closeCharStack;
		}

		@Override
		public String toString() {
			return "Result [openChar=" + openChar + ", corrupted=" + corrupted + "]";
		}
	}
}
