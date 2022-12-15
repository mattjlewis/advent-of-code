package com.diozero.aoc.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.diozero.aoc.geometry.Point2D;

public class OcrUtil {
	public static final int CHAR_WIDTH = 4;
	public static final int CHAR_HEIGHT = 6;

	private static final Map<Integer, Character> MAPPING;
	static {
		MAPPING = new HashMap<>();

		MAPPING.put(Integer.valueOf(10090902), Character.valueOf('A'));
		MAPPING.put(Integer.valueOf(7968663), Character.valueOf('B'));
		MAPPING.put(Integer.valueOf(6885782), Character.valueOf('C'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('D'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('E'));
		MAPPING.put(Integer.valueOf(1120031), Character.valueOf('F'));
		MAPPING.put(Integer.valueOf(15323542), Character.valueOf('G'));
		MAPPING.put(Integer.valueOf(10067865), Character.valueOf('H'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('I'));
		MAPPING.put(Integer.valueOf(6916236), Character.valueOf('J'));
		MAPPING.put(Integer.valueOf(9786201), Character.valueOf('K'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('L'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('M'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('N'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('O'));
		MAPPING.put(Integer.valueOf(1145239), Character.valueOf('P'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('Q'));
		MAPPING.put(Integer.valueOf(9795991), Character.valueOf('R'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('S'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('T'));
		MAPPING.put(Integer.valueOf(6920601), Character.valueOf('U'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('V'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('W'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('X'));
		MAPPING.put(Integer.valueOf(-1), Character.valueOf('Y'));
		MAPPING.put(Integer.valueOf(15803535), Character.valueOf('Z'));
	}

	public static String decode(boolean[][] matrix) {
		final StringBuffer sb = new StringBuffer();
		// Bit of an ugly hack with (matrix[0].length + 1) to account for a matrix
		// without blanks at the end
		for (int ch_index = 0; ch_index < (matrix[0].length + 1) / (CHAR_WIDTH + 1); ch_index++) {
			final int i = toInt(matrix, ch_index);
			final Character ch = MAPPING.get(Integer.valueOf(i));
			if (ch == null) {
				PrintUtil.print(matrix);
				throw new IllegalArgumentException(
						"Error - unrecognised character mapping value " + i + " at index " + ch_index);
			}
			sb.append(ch);
		}

		return sb.toString();
	}

	public static String decode(Set<Point2D> points) {
		return decode(MatrixUtil.toMatrix(points));
	}

	private static int toInt(boolean[][] matrix, int chIndex) {
		int ch = 0;

		// Each character is encoded as 4x6 bits (width x height) == 24 bits
		for (int row = 0; row < CHAR_HEIGHT; row++) {
			for (int col = 0; col < CHAR_WIDTH; col++) {
				ch |= matrix[row][chIndex * (CHAR_WIDTH + 1) + col] ? 1 << (row * CHAR_WIDTH + col) : 0;
			}
		}

		return ch;
	}
}
