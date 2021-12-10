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
		return Files.lines(input).mapToInt(Day10::part1Score).sum();
	}

	private static int part1Score(String line) {
		try {
			findClosing(line, 0, new ArrayDeque<>());
			return 0;
		} catch (SyntaxError e) {
			if (e.pos == line.length()) {
				// Some of the lines aren't corrupted, just incomplete; you can ignore these
				// lines for now.
				return 0;
			}
			return PART1_ERROR_SCORES.get(e.ch).intValue();
		}
	}

	@Override
	public long part2(Path input) throws IOException {
		long[] scores = Files.lines(input).mapToLong(Day10::part2Score).filter(score -> score != 0).sorted().toArray();
		Logger.debug("Scores: " + Arrays.toString(scores));
		return scores[scores.length / 2];
	}

	private static long part2Score(String line) {
		// Don't use Stack as stream() returns items in FIFO order, not LIFO
		Deque<Character> close_char_stack = new ArrayDeque<>();
		try {
			findClosing(line, 0, close_char_stack);
			// Ignore complete lines
			return 0;
		} catch (SyntaxError e) {
			if (e.pos < line.length()) {
				// Discard the corrupted lines
				return 0;
			}
			// Process only incomplete lines

			return close_char_stack.stream().mapToLong(ch -> 1 + CLOSE_CHARS.indexOf(ch)).reduce(0,
					(a, b) -> 5 * a + b);
		}
	}

	private static void findClosing(String line, int pos, Deque<Character> closeChars) throws SyntaxError {
		Logger.debug("line: {}, pos: {}, looking for closeChar: {}", line, pos,
				closeChars.isEmpty() ? null : closeChars.peek());

		// Have we reached the end of the line?
		if (pos >= line.length()) {
			// Still searching for a close character?
			if (!closeChars.isEmpty()) {
				throw new SyntaxError(closeChars.peek(), pos);
			}

			return;
		}

		// An open character?
		int open_char_index = OPEN_CHARS.indexOf(line.charAt(pos));
		Logger.debug("current char: {}, open_char_index: {}", line.charAt(pos), open_char_index);
		if (open_char_index != -1) {
			closeChars.push(CLOSE_CHARS.get(open_char_index));
			findClosing(line, pos + 1, closeChars);
		} else {
			// Must be a close character - is it the expected one?
			Character expected_close_char = closeChars.pop();
			if (line.charAt(pos) != expected_close_char.charValue()) {
				throw new SyntaxError(line.charAt(pos), expected_close_char, pos);
			}

			findClosing(line, pos + 1, closeChars);
		}
	}

	static final class SyntaxError extends Exception {
		private static final long serialVersionUID = -3074774300106309441L;

		final Character ch;
		final Character closeChar;
		final int pos;

		public SyntaxError(Character closeChar, int pos) {
			this.ch = null;
			this.closeChar = closeChar;
			this.pos = pos;
		}

		public SyntaxError(char ch, Character closeChar, int pos) {
			this.ch = Character.valueOf(ch);
			this.closeChar = closeChar;
			this.pos = pos;
		}

		@Override
		public String toString() {
			return "SyntaxError [ch=" + ch + ", closeChar=" + closeChar + ", pos=" + pos + "]";
		}
	}
}
