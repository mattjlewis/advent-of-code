package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.OcrUtil;

public class Day8 extends Day {
	public static void main(String[] args) {
		new Day8().run();
	}

	private static final char BLACK = '0';
	private static final char WHITE = '1';
	private static final char TRANSPARENT = '2';

	@Override
	public String name() {
		return "Space Image Format";
	}

	@Override
	public String part1(Path input) throws IOException {
		final List<String> layer = loadData(input).stream().min(Comparator.comparing(Day8::countBlack)).orElseThrow();

		return Long.toString(count(layer, WHITE) * count(layer, TRANSPARENT));
	}

	@Override
	public String part2(Path input) throws IOException {
		List<List<String>> layers = loadData(input);

		int width = layers.get(0).get(0).length();
		int height = layers.get(0).size();

		// TODO Can this be optimised?
		boolean[][] image = new boolean[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				for (List<String> layer : layers) {
					char ch = layer.get(y).charAt(x);
					if (ch == '0' || ch == '1') {
						image[y][x] = ch == '1' ? true : false;
						break;
					}
				}
			}
		}
		return OcrUtil.decode(image);
	}

	private static List<List<String>> loadData(Path input) throws IOException {
		final String image_text = Files.lines(input).findFirst().orElseThrow();
		// Part 1 example image is 3 pixels wide and 2 pixels tall
		// Part 2 example image is 2 pixels wide and 2 pixels tall
		// The image you received is 25 pixels wide and 6 pixels tall
		final int width = image_text.length() == 12 ? 3 : image_text.length() == 16 ? 2 : 25;
		final int height = image_text.length() == 12 ? 2 : image_text.length() == 16 ? 2 : 6;

		final Pattern p = Pattern.compile(String.format("(\\d{%d})", Integer.valueOf(width)).repeat(height));
		return p.matcher(image_text).results()
				.map(mr -> IntStream.rangeClosed(1, height).mapToObj(i -> mr.group(i)).toList()).toList();
	}

	private static final long countBlack(List<String> layer) {
		return count(layer, BLACK);
	}

	private static final long count(List<String> layer, char pixel) {
		return layer.stream().mapToLong(part -> part.chars().filter(ch -> ch == pixel).count()).sum();
	}
}
