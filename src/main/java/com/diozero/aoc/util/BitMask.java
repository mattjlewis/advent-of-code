package com.diozero.aoc.util;

public record BitMask(int value, int length) {
	public static BitMask create(String value, char set, char unset) {
		return new BitMask(Integer.parseInt(StringUtil.reverse(value.replace(set, '1').replace(unset, '0')), 2),
				value.length());
	}

	public BitMask toggle(int[] bits) {
		return new BitMask(toggle(value, bits), length);
	}

	public static int parse(String value, char set) {
		return Integer.parseInt(StringUtil.reverse(value.replaceAll("[^" + set + "]", "0").replace(set, '1')), 2);
	}

	public static int toggle(int value, int[] bits) {
		int i = value;
		for (int bit : bits) {
			i ^= (1 << bit);
		}
		return i;
	}
}
