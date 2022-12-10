package com.diozero.google.dontgetvolunteered;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
public class SolutionTest {
	@Test
	public void test() {
		// Adjacent, anywhere (Manhattan distance == 1)
		Assertions.assertEquals(3, Solution.solution(0, 1));
		Assertions.assertEquals(3, Solution.solution(1, 0));
		Assertions.assertEquals(3, Solution.solution(39, 47));
		Assertions.assertEquals(3, Solution.solution(60, 52));
		Assertions.assertEquals(3, Solution.solution(27, 35));
		Assertions.assertEquals(3, Solution.solution(19, 20));

		// Diagonal from corners (Manhattan distance == 2)
		Assertions.assertEquals(4, Solution.solution(0, 9));
		Assertions.assertEquals(4, Solution.solution(7, 14));
		Assertions.assertEquals(4, Solution.solution(7, 14));
		Assertions.assertEquals(4, Solution.solution(63, 54));
		Assertions.assertEquals(4, Solution.solution(56, 49));
		Assertions.assertEquals(4, Solution.solution(9, 0));
		Assertions.assertEquals(4, Solution.solution(14, 7));
		Assertions.assertEquals(4, Solution.solution(14, 7));
		Assertions.assertEquals(4, Solution.solution(54, 63));
		Assertions.assertEquals(4, Solution.solution(49, 56));
		// Normal diagonal (Manhattan distance == 2)
		Assertions.assertEquals(2, Solution.solution(42, 49));
		// Same row / column (Manhattan distance == 2)
		Assertions.assertEquals(2, Solution.solution(0, 2));

		// Same row / column (Manhattan distance == 3)
		Assertions.assertEquals(3, Solution.solution(0, 3));
		Assertions.assertEquals(3, Solution.solution(3, 0));
		// Single move (Manhattan distance == 3)
		Assertions.assertEquals(1, Solution.solution(19, 36));
		Assertions.assertEquals(1, Solution.solution(36, 19));

		// Same row / column (Manhattan distance == 4)
		Assertions.assertEquals(2, Solution.solution(0, 4));
		Assertions.assertEquals(2, Solution.solution(16, 48));
		// Diagonal (Manhattan distance == 4)
		Assertions.assertEquals(4, Solution.solution(0, 18));
		// Other (Manhattan distance == 4)
		Assertions.assertEquals(2, Solution.solution(0, 11));

		// Same row / column (Manhattan distance == 5)
		Assertions.assertEquals(3, Solution.solution(0, 5));
		Assertions.assertEquals(3, Solution.solution(15, 55));
		// One off (Manhattan distance == 5)
		Assertions.assertEquals(3, Solution.solution(0, 12));
		Assertions.assertEquals(3, Solution.solution(0, 33));
		Assertions.assertEquals(3, Solution.solution(63, 51));
		// Two off (Manhattan distance == 5)
		Assertions.assertEquals(3, Solution.solution(0, 19));
		Assertions.assertEquals(3, Solution.solution(16, 42));
		Assertions.assertEquals(3, Solution.solution(18, 37));

		// Same row / column (Manhattan distance == 6)
		Assertions.assertEquals(4, Solution.solution(0, 6));
		Assertions.assertEquals(4, Solution.solution(6, 0));
		// One off (Manhattan distance == 6)
		Assertions.assertEquals(4, Solution.solution(0, 13));
		Assertions.assertEquals(4, Solution.solution(0, 41));
		Assertions.assertEquals(4, Solution.solution(48, 9));
		Assertions.assertEquals(4, Solution.solution(43, 4));
		Assertions.assertEquals(4, Solution.solution(38, 41));
		// Two off (Manhattan distance == 6)
		Assertions.assertEquals(2, Solution.solution(0, 20));
		Assertions.assertEquals(2, Solution.solution(0, 34));
		Assertions.assertEquals(2, Solution.solution(55, 21));
		// Three off (Manhattan distance == 6)
		Assertions.assertEquals(2, Solution.solution(0, 27));
		Assertions.assertEquals(2, Solution.solution(53, 26));

		// Same row / column (Manhattan distance == 7)
		Assertions.assertEquals(5, Solution.solution(0, 7));
		// One off (Manhattan distance == 7)
		Assertions.assertEquals(3, Solution.solution(0, 14));
		// Two off (Manhattan distance == 7)
		Assertions.assertEquals(3, Solution.solution(0, 21));
		Assertions.assertEquals(3, Solution.solution(0, 42));
		// Three off (Manhattan distance == 7)
		Assertions.assertEquals(3, Solution.solution(0, 28));
		Assertions.assertEquals(3, Solution.solution(0, 35));

		// One off (Manhattan distance == 8)
		Assertions.assertEquals(4, Solution.solution(0, 15));
		Assertions.assertEquals(4, Solution.solution(0, 22));

		// Two off (Manhattan distance == 9)
		Assertions.assertEquals(5, Solution.solution(0, 23));
		Assertions.assertEquals(5, Solution.solution(0, 58));
		// Three off (Manhattan distance == 9)
		Assertions.assertEquals(3, Solution.solution(0, 30));
		Assertions.assertEquals(3, Solution.solution(0, 51));
		// Four off (Manhattan distance == 9)
		Assertions.assertEquals(3, Solution.solution(0, 37));
		Assertions.assertEquals(3, Solution.solution(0, 44));

		// Four off (Manhattan distance == 10)
		Assertions.assertEquals(4, Solution.solution(0, 31));

		// Five off (Manhattan distance == 11)
		Assertions.assertEquals(5, Solution.solution(0, 39));
		Assertions.assertEquals(5, Solution.solution(0, 60));

		// Six off (Manhattan distance == 12)
		Assertions.assertEquals(4, Solution.solution(0, 47));
		Assertions.assertEquals(4, Solution.solution(0, 54));
		Assertions.assertEquals(4, Solution.solution(0, 61));

		// Seven off (Manhattan distance == 13)
		Assertions.assertEquals(5, Solution.solution(0, 55));
		Assertions.assertEquals(5, Solution.solution(0, 62));
	}

	@Test
	public void test2() {
		int kx = 7, ky = 0;

		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				if (x == kx && y == ky) {
					continue;
				}
				System.out.print(Solution.solution(7, y * 8 + x) + " ");
			}
			System.out.println();
		}
	}
}
