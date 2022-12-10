package com.diozero.google.brailletranslation;

import java.util.HashMap;
import java.util.Map;

public class Solution {
	private static final int BRAILLE_ENC_LEN = 6;

	private static final String CAPS_ENC = toBinaryString(0b000001, BRAILLE_ENC_LEN);

	private static final Map<Character, String> BRAILLE = new HashMap<>();
	static {
		BRAILLE.put(Character.valueOf('a'), toBinaryString(0b100000, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('b'), toBinaryString(0b110000, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('c'), toBinaryString(0b100100, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('d'), toBinaryString(0b100110, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('e'), toBinaryString(0b100010, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('f'), toBinaryString(0b110100, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('g'), toBinaryString(0b110110, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('h'), toBinaryString(0b110010, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('i'), toBinaryString(0b010100, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('j'), toBinaryString(0b010110, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('k'), toBinaryString(0b101000, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('l'), toBinaryString(0b111000, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('m'), toBinaryString(0b101100, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('n'), toBinaryString(0b101110, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('o'), toBinaryString(0b101010, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('p'), toBinaryString(0b111100, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('q'), toBinaryString(0b111110, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('r'), toBinaryString(0b111010, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('s'), toBinaryString(0b011100, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('t'), toBinaryString(0b011110, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('u'), toBinaryString(0b101001, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('v'), toBinaryString(0b111001, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('w'), toBinaryString(0b010111, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('x'), toBinaryString(0b101101, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('y'), toBinaryString(0b101111, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('z'), toBinaryString(0b101011, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('#'), toBinaryString(0b001111, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('1'), toBinaryString(0b100000, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('2'), toBinaryString(0b110000, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('3'), toBinaryString(0b100100, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('4'), toBinaryString(0b100110, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('5'), toBinaryString(0b100010, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('6'), toBinaryString(0b110100, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('7'), toBinaryString(0b110110, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('8'), toBinaryString(0b110010, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('9'), toBinaryString(0b010100, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf('0'), toBinaryString(0b010110, BRAILLE_ENC_LEN));
		BRAILLE.put(Character.valueOf(' '), toBinaryString(0b000000, BRAILLE_ENC_LEN));
	}

	private static String toBinaryString(int i, int padding) {
		return String.format("%" + padding + "s", Integer.toBinaryString(i)).replace(' ', '0');
	}

	private static final String toBrailleEnc(char ch) {
		if (Character.isDigit(ch)) {
			return BRAILLE.get(Character.valueOf('#')) + BRAILLE.get(Character.valueOf(ch));
		}

		if (Character.isUpperCase(ch)) {
			return CAPS_ENC + BRAILLE.get(Character.valueOf(Character.toLowerCase(ch)));
		}

		return BRAILLE.get(Character.valueOf(ch));
	}

	public static String solution(String s) {
		StringBuilder builder = new StringBuilder();
		for (char ch : s.toCharArray()) {
			builder.append(toBrailleEnc(ch));
		}
		return builder.toString();
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: Solution <string>");
			System.exit(1);
		}
		String s = args[0];
		System.out.format("Braille for '%s': %s%n", s, solution(s));
	}
}
