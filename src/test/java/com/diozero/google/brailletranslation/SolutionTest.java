package com.diozero.google.brailletranslation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class SolutionTest {
	@Test
	public void code() {
		Assertions.assertEquals("100100101010100110100010", Solution.solution("code"));
	}

	@Test
	public void braille() {
		Assertions.assertEquals("000001110000111010100000010100111000111000100010", Solution.solution("Braille"));
	}

	@Test
	public void tqbfjotld() {
		Assertions.assertEquals(
				"000001011110110010100010000000111110101001010100100100101000000000110000111010101010010111101110000000110100101010101101000000010110101001101100111100011100000000101010111001100010111010000000011110110010100010000000111000100000101011101111000000100110101010110110",
				Solution.solution("The quick brown fox jumps over the lazy dog"));
	}
}
