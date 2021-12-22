package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.UUID;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;

public class Day18 extends AocBase {
	public static void main(String[] args) {
		Pair pair = loadPairs("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]");
		assert (pair.canExplode());
		assert (!pair.canSplit());
		pair.explode();
		assert (pair.toString().equals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"));

		pair = loadPairs("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]");
		assert (pair.canExplode());
		assert (!pair.canSplit());
		pair.explode();
		assert (pair.toString().equals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"));

		Pair left = loadPairs("[[[[4,3],4],4],[7,[[8,4],9]]]");
		Pair right = loadPairs("[1,1]");
		pair = Pair.add(left, right);
		assert (pair.toString().equals("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]"));
		assert (pair.canExplode());
		assert (!pair.canSplit());
		pair.explode();
		assert (pair.toString().equals("[[[[0,7],4],[7,[[8,4],9]]],[1,1]]"));

		pair = loadPairs("[[[[0,7],4],[7,[[8,4],9]]],[1,1]]");
		assert (pair.canExplode());
		assert (!pair.canSplit());
		pair.explode();
		assert (pair.toString().equals("[[[[0,7],4],[15,[0,13]]],[1,1]]"));

		pair = loadPairs("[[[[0,7],4],[15,[0,13]]],[1,1]]");
		assert (!pair.canExplode());
		assert (pair.canSplit());
		pair.split();
		assert (pair.toString().equals("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]"));

		pair = loadPairs("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]");
		assert (!pair.canExplode());
		assert (pair.canSplit());
		pair.split();
		assert (pair.toString().equals("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]"));

		pair = loadPairs("[[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]");
		assert (pair.canExplode());
		assert (!pair.canSplit());
		pair.explode();
		assert (pair.toString().equals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"));

		pair = loadPairs("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]");
		assert (!pair.canExplode());
		assert (!pair.canSplit());

		Logger.debug("Doing addAndReduce");
		left = loadPairs("[[[[4,3],4],4],[7,[[8,4],9]]]");
		right = loadPairs("[1,1]");
		pair = Pair.addAndReduce(left, right);
		assert (pair.toString().equals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"));
		/*-                             [[[[0,7],4],[[7,8],[6,0]]],[1,1]] */

		left = loadPairs("[[[[4,3],4],4],[7,[[8,4],9]]]");
		right = loadPairs("[1,1]");
		pair = Pair.addAndReduce(left, right);
		assert (pair.toString().equals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"));
		/*-                             [[[[0,7],4],[[7,8],[6,0]]],[1,1]] */

		new Day18().run();
	}

	/**
	 * Examples:
	 *
	 * <pre>
	 * [1,2]
	 * [[1,2],3]
	 * [9,[8,7]]
	 * [[1,9],[8,5]]
	 * [[[[1,2],[3,4]],[[5,6],[7,8]]],9]
	 * [[[9,[3,8]],[[0,9],6]],[[[3,7],[4,9]],3]]
	 * [[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]
	 * </pre>
	 *
	 * @param input input data
	 */
	private static List<Pair> loadData(Path input) throws IOException {
		return Files.lines(input).map(Day18::loadPairs).toList();
	}

	private static Pair loadPairs(String line) {
		PrimitiveIterator.OfInt it = line.chars().iterator();

		char ch = (char) it.nextInt();
		if (ch != '[') {
			throw new IllegalStateException("Expected '[', got '" + ch + "'");
		}

		return loadPairs(null, it);
	}

	private static Pair loadPairs(Pair parent, PrimitiveIterator.OfInt it) {
		Pair pair = new Pair(parent);

		Value value;
		char ch = (char) it.nextInt();
		if (ch == '[') {
			value = new Value(pair, loadPairs(pair, it));
			ch = (char) it.nextInt();
		} else {
			int x = ch - 48;
			ch = (char) it.nextInt();
			if (Character.isDigit(ch)) {
				x = x * 10 + (ch - 48);
				ch = (char) it.nextInt();
			}
			value = new Value(pair, x);
		}
		pair.setLeft(value);

		if (ch != ',') {
			throw new IllegalStateException("Expected ',' but got '" + ch + "'");
		}

		ch = (char) it.nextInt();
		if (ch == '[') {
			value = new Value(pair, loadPairs(pair, it));
			ch = (char) it.nextInt();
		} else {
			int x = ch - 48;
			ch = (char) it.nextInt();
			if (Character.isDigit(ch)) {
				x = x * 10 + (ch - 48);
				ch = (char) it.nextInt();
			}
			value = new Value(pair, x);
		}
		pair.setRight(value);

		if (ch != ']') {
			throw new IllegalStateException("Expected ']' but got '" + ch + "'");
		}

		return pair;
	}

	@Override
	public long part1(Path input) throws IOException {
		List<Pair> pairs = loadData(input);

		Logger.debug("Loaded Pairs:");
		pairs.forEach(pair -> Logger.debug("canExplode: {}, canSplit: {}, pair: {}", pair.canExplode(), pair.canSplit(),
				pair));

		Pair pair = null;
		for (Pair p : pairs) {
			if (pair == null) {
				pair = p;
			} else {
				pair = Pair.addAndReduce(pair, p);
			}
		}

		System.out.println(pair);

		return pair.getMagnitude();
	}

	@Override
	public long part2(Path input) throws IOException {
		List<Pair> pairs = loadData(input);

		int max_mag = Integer.MIN_VALUE;

		for (int i = 0; i < pairs.size() - 1; i++) {
			for (int j = i + 1; j < pairs.size(); j++) {
				Pair p1 = pairs.get(i);
				Pair p2 = pairs.get(j);
				max_mag = Math.max(max_mag, Pair.addAndReduce(p1, p2).getMagnitude());
				max_mag = Math.max(max_mag, Pair.addAndReduce(p2, p1).getMagnitude());
			}
		}

		return max_mag;
	}

	private static class Pair {
		public static Pair addAndReduce(Pair a, Pair b) {
			return reduce(add(a, b));
		}

		public int getMagnitude() {
			/*
			 * The magnitude of a pair is 3 times the magnitude of its left element plus 2
			 * times the magnitude of its right element. The magnitude of a regular number
			 * is just that number.
			 */
			int magnitude = 0;
			if (left.isPair()) {
				magnitude = 3 * left.getPairValue().getMagnitude();
			} else {
				magnitude = 3 * left.getIntValue();
			}

			if (right.isPair()) {
				magnitude += 2 * right.getPairValue().getMagnitude();
			} else {
				magnitude += 2 * right.getIntValue();
			}

			return magnitude;
		}

		public static Pair add(Pair a, Pair b) {
			// Must clone a and b as Pair is mutable
			return new Pair(null, a.clone(), b.clone());
		}

		public static Pair reduce(Pair pair) {
			String orig = pair.toString();
			/*
			 * During reduction, at most one action applies, after which the process returns
			 * to the top of the list of actions. For example, if split produces a pair that
			 * meets the explode criteria, that pair explodes before other splits occur.
			 */
			Pair p = pair;
			while (true) {
				if (p.canExplode()) {
					Logger.trace("can explode");
					// p = explodeString(p);
					p.explode();
					continue;
				}

				if (p.canSplit()) {
					Logger.trace("can split");
					p.split();
					continue;
				}

				break;
			}
			Logger.debug("Result of reduce {}: {}", orig, p);

			return p;
		}

		private String uuid;
		private Pair parent;
		private Value left;
		private Value right;

		public Pair(Pair parent) {
			this.uuid = UUID.randomUUID().toString();
			this.parent = parent;
		}

		public Pair(Pair parent, int left, int right) {
			this.uuid = UUID.randomUUID().toString();
			this.parent = parent;
			this.left = new Value(this, left);
			this.right = new Value(this, right);
		}

		public Pair(Pair parent, Pair left, Pair right) {
			this.uuid = UUID.randomUUID().toString();
			this.parent = parent;
			left.setParent(this);
			right.setParent(this);
			this.left = new Value(this, left);
			this.right = new Value(this, right);
		}

		@Override
		public Pair clone() {
			return Day18.loadPairs(toString());
		}

		public void setParent(Pair parent) {
			this.parent = parent;
		}

		public Value getLeft() {
			return left;
		}

		public void setLeft(Value value) {
			this.left = value;
		}

		public Value getRight() {
			return right;
		}

		public void setRight(Value value) {
			this.right = value;
		}

		/**
		 * If any pair is nested inside four pairs, the leftmost such pair explodes.
		 *
		 * @return true if there is a nested pair of depth > 4
		 */
		public boolean canExplode() {
			return canExplode(0);
		}

		public boolean canExplode(int nestLevel) {
			if (nestLevel >= 4) {
				return true;
			}

			return left.canExplode(nestLevel + 1) || right.canExplode(nestLevel + 1);
		}

		public boolean canSplit() {
			return left.canSplit() || right.canSplit();
		}

		public boolean explode() {
			if (parent != null) {
				throw new IllegalStateException("Can only invoke this on the root pair");
			}

			return explode(0);
		}

		private boolean explode(int depth) {
			Logger.trace("explode depth: {}, left: {}, right: {}", depth, left, right);
			// Note can only do one explosion per pass

			if (depth >= 3) {
				// If any pair is nested inside four pairs, the leftmost such pair explodes.
				if (left.isPair() || right.isPair()) {
					/*
					 * To explode a pair, the pair's left value is added to the first regular number
					 * to the left of the exploding pair (if any), and the pair's right value is
					 * added to the first regular number to the right of the exploding pair (if
					 * any). Exploding pairs will always consist of two regular numbers. Then, the
					 * entire exploding pair is replaced with the regular number 0.
					 */
					Logger.trace("Exploding node [{},{}]...", left, right);

					// Which pair has exploded?
					if (left.isPair()) {
						int left_left_val = left.getPairValue().getLeft().getIntValue();
						int left_right_val = left.getPairValue().getRight().getIntValue();
						// [[4,3],4] -> [0,7]
						// [[9,8],1] -> [0,9]
						// [7,[[8,4],9]] -> [15,[0,13]]
						left = new Value(this, 0);

						parent.addToFirstNumberToTheLeft(this, left_left_val);
						addToFirstNumberToTheRight(this, left_right_val);
					} else {
						int right_left_val = right.getPairValue().getLeft().getIntValue();
						int right_right_val = right.getPairValue().getRight().getIntValue();

						/*-
						 *  [0,[6,7]] -> [6,0]
						 * [7,[6,[5,[4,[3,2]]]]] -> [7,[6,[5,[7,0]]]]
						 * [[6,[5,[4,[3,2]]]],1] -> [[6,[5,[7,0]]],3]
						 * [[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]] -> [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]
						 * [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]] -> [[3,[2,[8,0]]],[9,[5,[7,0]]]]
						 * [[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]] -> [[[[0,7],4],[[7,8],[6,0]]],[8,1]]
						 *                    ---------    -        [[[[0,7],4],[[7,8],[6,0]]],[1,1]]
						 */
						right = new Value(this, 0);

						addToFirstNumberToTheLeft(this, right_left_val);
						parent.addToFirstNumberToTheRight(this, right_right_val);
					}
					Logger.trace("Exploded node, now [{},{}]...", left, right);

					return true;
				}

				return false;
			}

			boolean exploded = false;
			if (left.isPair()) {
				exploded = left.getPairValue().explode(depth + 1);
			}

			if (!exploded && right.isPair()) {
				exploded = right.getPairValue().explode(depth + 1);
			}

			return exploded;
		}

		public boolean split() {
			/*
			 * If any regular number is 10 or greater, the leftmost such regular number
			 * splits.
			 *
			 * To split a regular number, replace it with a pair; the left element of the
			 * pair should be the regular number divided by two and rounded down, while the
			 * right element of the pair should be the regular number divided by two and
			 * rounded up. For example, 10 becomes [5,5], 11 becomes [5,6], 12 becomes
			 * [6,6], and so on.
			 */
			if (left.split()) {
				return true;
			}

			return right.split();
		}

		/*
		 * The pair's left value is added to the first regular number to the left of the
		 * exploding pair (if any)
		 */
		public void addToFirstNumberToTheLeft(Pair child, int val) {
			if (!left.isPair()) {
				left.add(val);
			} else if (!left.getPairValue().equals(child)) {
				left.getPairValue().addToFirstRightNumber(val);
			} else if (parent != null) {
				parent.addToFirstNumberToTheLeft(this, val);
			}
		}

		/*
		 * The pair's right value is added to the first regular number to the right of
		 * the exploding pair (if any)
		 */
		public void addToFirstNumberToTheRight(Pair child, int val) {
			if (!right.isPair()) {
				right.add(val);
			} else if (!right.getPairValue().equals(child)) {
				right.getPairValue().addToFirstLeftNumber(val);
			} else if (parent != null) {
				parent.addToFirstNumberToTheRight(this, val);
			}
		}

		/*
		 * Navigate down towards the first number leaf node on the left
		 */
		public void addToFirstLeftNumber(int val) {
			if (left.isPair()) {
				left.getPairValue().addToFirstLeftNumber(val);
			} else {
				left.add(val);
			}
		}

		/*
		 * Navigate down towards the first number leaf node on the right
		 */
		public void addToFirstRightNumber(int val) {
			if (right.isPair()) {
				right.getPairValue().addToFirstRightNumber(val);
			} else {
				right.add(val);
			}
		}

		@Override
		public String toString() {
			return "[" + left.toString() + "," + right.toString() + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (uuid == null) {
				if (other.uuid != null)
					return false;
			} else if (!uuid.equals(other.uuid))
				return false;
			return true;
		}
	}

	private static class Value {
		// A Value always belongs to a pair
		private Pair pair;
		// A Value can either be a literal integer value or another Pair
		private int intValue;
		private Pair pairValue;

		public Value(Pair pair, int intValue) {
			this.pair = pair;
			this.intValue = intValue;
		}

		public Value(Pair pair, Pair pairValue) {
			this.pair = pair;
			this.pairValue = pairValue;
		}

		public boolean isPair() {
			return pairValue != null;
		}

		public int getIntValue() {
			return intValue;
		}

		public void add(int val) {
			intValue += val;
		}

		public Pair getPairValue() {
			return pairValue;
		}

		public boolean canExplode(int nestLevel) {
			return pairValue != null && pairValue.canExplode(nestLevel);
		}

		public boolean canSplit() {
			if (pairValue == null) {
				return intValue >= 10;
			}

			return pairValue.canSplit();
		}

		public boolean split() {
			if (pairValue != null) {
				return pairValue.split();
			}

			if (intValue >= 10) {
				pairValue = new Pair(pair, intValue / 2, (int) Math.ceil(intValue / 2.0));

				return true;
			}

			return false;
		}

		@Override
		public String toString() {
			return pairValue == null ? Integer.toString(intValue) : pairValue.toString();
		}
	}
}
