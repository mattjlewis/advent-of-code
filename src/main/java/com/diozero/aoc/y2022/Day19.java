package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import com.diozero.aoc.Day;

public class Day19 extends Day {
	public static void main(String[] args) {
		new Day19().run();
	}

	@Override
	public String name() {
		return "Not Enough Minerals";
	}

	@Override
	public String part1(Path input) throws IOException {
		return Integer.toString(Files.lines(input).map(Blueprint::parse).mapToInt(b -> b.id * b.openGeodes(24)).sum());
	}

	@Override
	public String part2(Path input) throws IOException {
		return Integer.toString(Files.lines(input).limit(3).map(Blueprint::parse).mapToInt(b -> b.openGeodes(32))
				.reduce(1, (a, b) -> a * b));
	}

	private static record Blueprint(int id, int oreCostForOreRobot, int oreCostForClayRobot,
			int oreCostForObsidianRobot, int clayCostForObsidianRobot, int oreCostForGeodeRobot,
			int obsidianCostForGeodeRobot) {

		private static final Pattern PATTERN = Pattern.compile("(\\d+)");

		public static Blueprint parse(String line) {
			int[] values = PATTERN.matcher(line).results().map(MatchResult::group).mapToInt(Integer::parseInt)
					.toArray();

			return new Blueprint(values[0], values[1], values[2], values[3], values[4], values[5], values[6]);
		}

		public int openGeodes(int time) {
			return openGeodes(0, 0, 0, 0, 1, 0, 0, 0, time, 0);
		}

		private int openGeodes(int ore, int clay, int obsidian, int geodes, int oreRobots, int clayRobots,
				int obsidianRobots, int geodeRobots, int timeLeft, int best) {
			if (timeLeft == 0) {
				return geodes;
			}

			if (geodes + (geodeRobots * timeLeft) + (timeLeft * (timeLeft - 1) / 2) < best) {
				return 0;
			}

			final int new_ore = ore + oreRobots;
			final int new_clay = clay + clayRobots;
			final int new_obsidian = obsidian + obsidianRobots;
			final int new_geode = geodes + geodeRobots;

			if (ore >= oreCostForGeodeRobot && obsidian >= obsidianCostForGeodeRobot()) {
				return openGeodes(new_ore - oreCostForGeodeRobot, new_clay, new_obsidian - obsidianCostForGeodeRobot,
						new_geode, oreRobots, clayRobots, obsidianRobots, geodeRobots + 1, timeLeft - 1, best);
			}

			if (clayRobots >= clayCostForObsidianRobot && obsidianRobots < obsidianCostForGeodeRobot
					&& ore >= oreCostForObsidianRobot && clay >= clayCostForObsidianRobot) {
				return openGeodes(new_ore - oreCostForObsidianRobot, new_clay - clayCostForObsidianRobot, new_obsidian,
						new_geode, oreRobots, clayRobots, obsidianRobots + 1, geodeRobots, timeLeft - 1, best);
			}

			int new_best = best;
			if (obsidianRobots < obsidianCostForGeodeRobot && ore >= oreCostForObsidianRobot
					&& clay >= clayCostForObsidianRobot) {
				new_best = Math.max(new_best,
						openGeodes(new_ore - oreCostForObsidianRobot, new_clay - clayCostForObsidianRobot, new_obsidian,
								new_geode, oreRobots, clayRobots, obsidianRobots + 1, geodeRobots, timeLeft - 1,
								new_best));
			}

			if (clayRobots < clayCostForObsidianRobot && ore >= oreCostForClayRobot) {
				new_best = Math.max(new_best, openGeodes(new_ore - oreCostForClayRobot, new_clay, new_obsidian,
						new_geode, oreRobots, clayRobots + 1, obsidianRobots, geodeRobots, timeLeft - 1, new_best));
			}

			if (oreRobots < 4 && ore >= oreCostForOreRobot) {
				new_best = Math.max(new_best, openGeodes(new_ore - oreCostForOreRobot, new_clay, new_obsidian,
						new_geode, oreRobots + 1, clayRobots, obsidianRobots, geodeRobots, timeLeft - 1, new_best));
			}

			if (ore <= 4) {
				new_best = Math.max(new_best, openGeodes(new_ore, new_clay, new_obsidian, new_geode, oreRobots,
						clayRobots, obsidianRobots, geodeRobots, timeLeft - 1, new_best));
			}

			return new_best;
		}
	}
}
