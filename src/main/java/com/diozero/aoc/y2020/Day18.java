package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import com.diozero.aoc.AocBase;

public class Day18 extends AocBase {
	public static void main(String[] args) {
		new Day18().run();
	}

	@Override
	public String part1(Path input) throws IOException {
		// Operators have the same precedence, evaluate left-to-right
		return Long.toString(Files.lines(input)
				.mapToLong(line -> evaluate(line.replace(" ", ""), new AtomicInteger(), false)).sum());
	}

	@Override
	public String part2(Path input) throws IOException {
		// Addition is evaluated before multiplication
		return Long.toString(
				Files.lines(input).mapToLong(line -> evaluate(line.replace(" ", ""), new AtomicInteger(), true)).sum());
	}

	private static long evaluate(String line, AtomicInteger pos, boolean additionPrecedence) {
		/*-
		 * Grammar:
		 * Expression := Term [+|- Term]*
		 * Term := '(' Expression ')' | Literal
		 * E.g. ((2+4*9)*(6+9*8+6)+6)+2+4*2 (part 1 = (54*126+12)*2=13632; part 2 = (54*(15*14+6)+6)*2=23340)
		 *      012345678901234567890123456
		 */

		long val = parseTerm(line, pos, additionPrecedence);

		while (pos.get() < line.length()) {
			char next = line.charAt(pos.getAndIncrement());
			if (next == ')') {
				break;
			}

			switch (next) {
			case '+':
				val += parseTerm(line, pos, additionPrecedence);
				break;
			case '*':
				long right = parseTerm(line, pos, additionPrecedence);
				if (additionPrecedence) {
					// Continue to process additions
					while (pos.get() < line.length() && line.charAt(pos.get()) == '+') {
						pos.incrementAndGet();
						right += parseTerm(line, pos, additionPrecedence);
					}
				}
				val *= right;
				break;
			default:
				throw new IllegalStateException("Unexpected next ch '" + next + "'");
			}
		}

		return val;
	}

	private static long parseTerm(String line, AtomicInteger pos, boolean additionPrecedence) {
		char ch = line.charAt(pos.getAndIncrement());
		if (ch == '(') {
			return evaluate(line, pos, additionPrecedence);
		}

		if (!Character.isDigit(ch)) {
			throw new IllegalArgumentException("Expected a digit, got '" + ch + "'");
		}

		int value = ch - 48;

		if (!additionPrecedence) {
			return value;
		}

		// Continue to process additions
		while (pos.get() < line.length() && line.charAt(pos.get()) == '+') {
			pos.incrementAndGet();
			value += parseTerm(line, pos, additionPrecedence);
		}

		return value;
	}
}
