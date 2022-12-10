package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.StringUtil;

public class Day3 extends Day {
	public static void main(String[] args) {
		new Day3().run();
	}

	@Override
	public String name() {
		return "Rucksack Reorganization";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(
				Files.lines(input).map(s -> List.of(s.substring(0, s.length() / 2), s.substring(s.length() / 2)))
						.mapToInt(Day3::findFirstCommonChar).map(Day3::getPriority).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final AtomicInteger counter = new AtomicInteger();
		return Integer.toString(
				Files.lines(input).collect(Collectors.groupingBy(c -> Integer.valueOf(counter.getAndIncrement() / 3)))
						.values().stream().mapToInt(Day3::findFirstCommonChar).map(Day3::getPriority).sum());
	}

	private static int findFirstCommonChar(List<String> strings) {
		final Set<Character> common = StringUtil.toCharSet(strings.get(0));
		strings.stream().skip(1).map(StringUtil::toCharSet).forEach(chars -> common.retainAll(chars));
		return common.stream().mapToInt(Character::charValue).findFirst().orElseThrow();
	}

	private static int getPriority(int common) {
		return (common >= 'a' ? common - 'a' : (common - 'A') + 26) + 1;
	}
}
