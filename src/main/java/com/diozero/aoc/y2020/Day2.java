package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diozero.aoc.AocBase;

public class Day2 extends AocBase {
	public static void main(String[] args) {
		new Day2().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		List<PasswordRule> rules = Files.lines(input).map(PasswordRule::parse).toList();

		return rules.stream().filter(PasswordRule::isValidSledRental).count();
	}

	@Override
	public long part2(Path input) throws IOException {
		List<PasswordRule> rules = Files.lines(input).map(PasswordRule::parse).toList();

		return rules.stream().filter(PasswordRule::isValid).count();
	}

	private static record PasswordRule(int min, int max, char ch, String password) {

		private static final Pattern PATTERN = Pattern.compile("(\\d+)-(\\d+) (\\w): (\\w+)");

		public static PasswordRule parse(String line) {
			Matcher m = PATTERN.matcher(line);
			if (!m.matches()) {
				throw new IllegalArgumentException("Line '" + line + "' doesn't match pattern " + PATTERN.pattern());
			}

			return new PasswordRule(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), m.group(3).charAt(0),
					m.group(4));
		}

		public boolean isValidSledRental() {
			long count = password.chars().filter(c -> c == ch).count();
			return count >= min && count <= max;
		}

		public boolean isValid() {
			return password.charAt(min - 1) == ch ^ password.charAt(max - 1) == ch;
		}
	}
}
