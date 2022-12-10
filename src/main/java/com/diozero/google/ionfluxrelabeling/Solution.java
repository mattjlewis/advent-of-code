package com.diozero.google.ionfluxrelabeling;

public class Solution {
	public static int[] solution(int h, int[] q) {
		int num_nodes = (int) Math.pow(2, h) - 1;
		int[] result = new int[q.length];

		for (int i = 0; i < q.length; i++) {
			int parent;
			if (q[i] == num_nodes) {
				parent = -1;
			} else {
				parent = 0;
				int mid = 1 << (h - 1);
				int n = num_nodes - q[i];
				while (n > 0) {
					int delta;
					if (n - mid >= 0) {
						delta = mid;
					} else {
						delta = 1;
					}
					if ((n - delta) == 0) {
						break;
					}
					parent += delta;
					n -= delta;
					mid >>= 1;
				}
				parent = num_nodes - parent;
			}
			result[i] = parent;
		}

		return result;
	}

	public static void main(String[] args) {
		solution(3, new int[] { 1, 4, 7, 6 });
	}
}