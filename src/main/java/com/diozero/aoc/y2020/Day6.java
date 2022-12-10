package com.diozero.aoc.y2020;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.StringUtil;

public class Day6 extends Day {
	public static void main(String[] args) {
		new Day6().run();
	}

	@Override
	public String name() {
		return "Custom Customs";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(loadData(input, false).stream().mapToInt(Set::size).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(loadData(input, true).stream().mapToInt(Set::size).sum());
	}

	private static List<Set<Character>> loadData(final Path input, final boolean union)
			throws FileNotFoundException, IOException {
		final List<Set<Character>> group_answers = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(input.toFile()))) {
			Set<Character> answers = null;
			while (true) {
				String line = br.readLine();
				if (line == null || line.isBlank()) {
					group_answers.add(answers);
					if (line == null) {
						break;
					}
					answers = null;
				} else {
					Set<Character> set = StringUtil.toCharSet(line);
					if (answers == null) {
						answers = set;
					} else {
						if (union) {
							answers.retainAll(set);
						} else {
							answers.addAll(set);
						}
					}
				}
			}
		}

		return group_answers;
	}
}
