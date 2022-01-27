package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.ArrayUtil;
import com.diozero.aoc.util.TextParser;

public class Day10 extends Day {
	public static void main(String[] args) {
		new Day10().run();
	}

	@Override
	public String name() {
		return "Monitoring Station";
	}

	@Override
	public String part1(Path input) throws IOException {
		boolean[][] map = TextParser.loadBooleanArray(input);

		ArrayUtil.print(map, '#', '.');

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String part2(Path input) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
