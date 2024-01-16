package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hipparchus.util.ArithmeticUtils;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.CircularLinkedList;
import com.diozero.aoc.util.CircularLinkedList.NodeIterator;

public class Day8 extends Day {
	public static void main(String[] args) {
		new Day8().run();
	}

	@Override
	public String name() {
		return "Haunted Wasteland";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final SandstormMap map = SandstormMap.load(input);

		return Long.toString(Stream.iterate(new NodeWithDirectionIterator(map, map.startNode()),
				NodeWithDirectionIterator::isNotEnd, NodeWithDirectionIterator::iterate).count());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final SandstormMap map = SandstormMap.load(input);

		return Long
				.toString(map.startNodesStream()
						.mapToLong(start_node -> Stream
								.iterate(new NodeWithDirectionIterator(map, start_node),
										NodeWithDirectionIterator::isNotEnd, NodeWithDirectionIterator::iterate)
								.count())
						.reduce(1, ArithmeticUtils::lcm));
	}

	public static record SandstormMap(CircularLinkedList<Direction> directions, Map<String, Node> nodes) {
		public static SandstormMap load(Path input) throws IOException {
			final List<String> lines = Files.readAllLines(input);

			final CircularLinkedList<Direction> directions = new CircularLinkedList<>(
					lines.getFirst().chars().mapToObj(Direction::of).toList());

			final Map<String, Node> nodes = lines.stream().skip(2).map(Node::parse)
					.collect(Collectors.toMap(Node::id, Function.identity()));

			return new SandstormMap(directions, nodes);
		}

		public Node startNode() {
			// Use the default value for when using test data sets without a "AAA" node
			return nodes.getOrDefault(Node.START_NODE_ID, startNodesStream().findFirst().orElseThrow());

		}

		public Stream<Node> startNodesStream() {
			return nodes.entrySet().stream().filter(n -> n.getKey().endsWith("A")).map(Map.Entry::getValue);
		}

		public NodeIterator<Direction> newDirectionIterator() {
			return new CircularLinkedList.NodeIterator<>(directions.head());
		}
	}

	private static enum Direction {
		LEFT, RIGHT;

		public static Direction of(int ch) {
			return switch (ch) {
			case 'L' -> LEFT;
			case 'R' -> RIGHT;
			default -> throw new IllegalArgumentException("Invalid direction '" + (char) ch + "'");
			};
		}
	}

	private static record Node(String id, String left, String right) {

		public static final String START_NODE_ID = "AAA";
		public static final char END_NODE_CHAR = 'Z';

		private static final Pattern PATTERN = Pattern.compile("([0-9A-Z]+) = \\(([0-9A-Z]+), ([0-9A-Z]+)\\)");

		public static Node parse(String line) {
			Matcher m = PATTERN.matcher(line);
			if (!m.matches()) {
				throw new IllegalArgumentException("Invalid Node line '" + line + "'");
			}

			return new Node(m.group(1), m.group(2), m.group(3));
		}

		public boolean isEnd() {
			return id.charAt(id.length() - 1) == END_NODE_CHAR;
		}

		public boolean isNotEnd() {
			return !isEnd();
		}

		public String get(Direction direction) {
			return direction == Direction.LEFT ? left : right;
		}
	}

	public static class NodeWithDirectionIterator {
		private final Map<String, Node> nodes;
		private Node node;
		private final CircularLinkedList.NodeIterator<Direction> nodeIterator;

		public NodeWithDirectionIterator(SandstormMap map, Node node) {
			this.nodes = map.nodes;
			this.node = node;
			this.nodeIterator = map.newDirectionIterator();
		}

		public NodeWithDirectionIterator iterate() {
			node = nodes.get(node.get(nodeIterator.getValueAndIncrement()));

			return this;
		}

		public boolean isNotEnd() {
			return node.isNotEnd();
		}
	}
}
