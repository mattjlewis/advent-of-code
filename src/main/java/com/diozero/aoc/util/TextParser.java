package com.diozero.aoc.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.tinylog.Logger;

import com.diozero.aoc.geometry.Point2D;

public class TextParser {
	public static final char SET_CHAR = '#';
	public static final char UNSET_CHAR = '.';

	private TextParser() {
	}

	public static Set<Point2D> loadPoints(final Path input) throws IOException {
		return loadPoints(input, SET_CHAR);
	}

	public static Set<Point2D> loadPoints(final Path input, final char ch) throws IOException {
		Set<Point2D> points = new HashSet<>();

		final Iterator<String> it = Files.lines(input).iterator();
		for (int y = 0; it.hasNext(); y++) {

			PrimitiveIterator.OfInt char_it = it.next().chars().iterator();
			for (int x = 0; char_it.hasNext(); x++) {
				if (char_it.nextInt() == ch) {
					points.add(new Point2D(x, y));
				}
			}
		}

		return points;
	}

	public static boolean[][] loadBooleanArray(final Path input) throws IOException {
		return loadBooleanArray(input, SET_CHAR);
	}

	public static boolean[][] loadBooleanArray(final Path input, final char ch) throws IOException {
		return Files.lines(input).map(line -> toBooleanArray(line, ch)).toArray(boolean[][]::new);
	}

	public static boolean[][] loadBooleanArray(List<String> lines) {
		return loadBooleanArray(lines, SET_CHAR);
	}

	public static boolean[][] loadBooleanArray(List<String> lines, final char ch) {
		return lines.stream().map(line -> toBooleanArray(line, ch)).toArray(boolean[][]::new);
	}

	public static boolean[] toBooleanArray(final String line) {
		return toBooleanArray(line, SET_CHAR);
	}

	public static boolean[] toBooleanArray(final String line, final char ch) {
		final boolean[] data = new boolean[line.length()];

		for (int i = 0; i < data.length; i++) {
			data[i] = line.charAt(i) == ch ? true : false;
		}

		return data;
	}

	public static int[] loadIntArray(final Path input) throws IOException {
		return Files.lines(input).mapToInt(Integer::parseInt).toArray();
	}

	public static int[] loadIntArray(final Path input, final boolean sorted) throws IOException {
		IntStream is = Files.lines(input).mapToInt(Integer::parseInt);
		if (sorted) {
			is = is.sorted();
		}
		return is.toArray();
	}

	public static List<Integer> loadFirstLineAsIntegerList(final Path input) throws IOException {
		return Files.lines(input).findFirst()
				.map(l -> l.chars().mapToObj(ch -> Integer.valueOf(charToInt(ch))).toList()).orElseThrow();
	}

	public static int[] loadFirstLineAsIntArray(final Path input) throws IOException {
		return Files.lines(input).findFirst().map(l -> l.chars().map(ch -> charToInt(ch)).toArray()).orElseThrow();
	}

	public static int charToInt(int ch) {
		return ch - 48;
	}

	public static int toInt(char ch) {
		return ch - 48;
	}

	public static long[] loadLongArray(final Path input) throws IOException {
		return Files.lines(input).mapToLong(Long::parseLong).toArray();
	}

	public static int[][] loadIntMatrix(final Path input) throws IOException {
		// Note the lazy conversion from ASCII character code to integer
		final int[][] matrix = Files.lines(input).map(line -> line.chars().map(TextParser::charToInt).toArray())
				.toArray(int[][]::new);

		if (Logger.isDebugEnabled()) {
			// Print the matrix if not too big
			if (matrix.length < 20) {
				for (int[] row : matrix) {
					Logger.debug("matrix: {}", Arrays.toString(row));
				}
			}
		}

		return matrix;
	}

	public static char[][] loadCharMatrix(Path input) throws IOException {
		return Files.lines(input).map(String::toCharArray).toArray(char[][]::new);
	}

	public static <R> List<List<R>> loadMatrix(Path input, IntFunction<R> mapper) throws IOException {
		return Files.lines(input).map(line -> line.chars().mapToObj(i -> mapper.apply(i)).toList()).toList();
	}

	public static int[] loadFirstLineAsCsvIntArray(Path input) throws IOException {
		return loadFirstLineAsCsvIntArray(input, false);
	}

	public static int[] loadFirstLineAsCsvIntArray(Path input, boolean sorted) throws IOException {
		IntStream stream = Arrays.stream(Files.lines(input).findFirst().orElseThrow().split(","))
				.mapToInt(Integer::parseInt);
		if (sorted) {
			stream = stream.sorted();
		}

		return stream.toArray();
	}

	public static long[] loadFirstLineAsCsvLongArray(Path input) throws IOException {
		return loadFirstLineAsCsvLongArray(input, false);
	}

	public static long[] loadFirstLineAsCsvLongArray(Path input, boolean sorted) throws IOException {
		LongStream stream = Arrays.stream(Files.lines(input).findFirst().orElseThrow().split(","))
				.mapToLong(Long::parseLong);
		if (sorted) {
			stream = stream.sorted();
		}

		return stream.toArray();
	}
}
