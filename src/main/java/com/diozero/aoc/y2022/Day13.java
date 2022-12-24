package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.Tuple3;

public class Day13 extends Day {
	private static final String OPEN = "[";
	private static final String CLOSE = "]";
	private static final String DIVIDER_PACKET_1 = "[[2]]";
	private static final String DIVIDER_PACKET_2 = "[[6]]";

	public static void main(String[] args) {
		new Day13().run();
	}

	@Override
	public String name() {
		return "Distress Signal";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Tuple3<Integer, Packet, Packet>> packet_pairs = Packet.loadAsPairs(input);

		return Integer.toString(packet_pairs.stream().filter(pair -> pair.second().compareTo(pair.third()) == -1)
				.mapToInt(Tuple3::first).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Packet> packets = Files.lines(input).filter(l -> !l.isBlank()).map(Packet::parse)
				.collect(Collectors.toList());
		packets.add(Packet.parse(DIVIDER_PACKET_1));
		packets.add(Packet.parse(DIVIDER_PACKET_2));

		int index = 1;
		int decoder_key = 1;
		for (Packet p : packets.stream().sorted().toList()) {
			String s = p.toString();
			if (s.equals(DIVIDER_PACKET_1) || s.equals(DIVIDER_PACKET_2)) {
				decoder_key *= index;
			}
			index++;
		}

		return Integer.toString(decoder_key);
	}

	private static interface Packet extends Comparable<Packet> {
		static final Pattern PACKET_PATTERN = Pattern.compile("\\" + OPEN + "|" + CLOSE + "|\\d+");

		static List<Tuple3<Integer, Packet, Packet>> loadAsPairs(Path input) throws IOException {
			final List<Tuple3<Integer, Packet, Packet>> packet_pairs = new ArrayList<>();

			String line1 = null;
			int index = 1;
			for (String line : Files.readAllLines(input)) {
				if (line.isBlank()) {
					continue;
				}

				if (line1 == null) {
					line1 = line;
				} else {
					// Create a new pair of packets
					packet_pairs.add(new Tuple3<>(Integer.valueOf(index++), Packet.parse(line1), Packet.parse(line)));
					line1 = null;
				}
			}

			return packet_pairs;
		}

		static Packet parse(String line) {
			final Matcher matcher = PACKET_PATTERN.matcher(line);
			final Deque<Packet> stack = new ArrayDeque<>();
			while (matcher.find()) {
				final String group = matcher.group();
				switch (group) {
				case OPEN:
					final ArrayPacket array_packet = new ArrayPacket();
					if (!stack.isEmpty() && stack.getLast() instanceof ArrayPacket parent_array) {
						parent_array.packets.add(array_packet);
					}
					stack.offerLast(array_packet);
					break;
				case CLOSE:
					if (stack.size() > 1) {
						stack.removeLast();
					}
					break;
				default:
					final ValuePacket value_packet = new ValuePacket(Integer.parseInt(group));
					if (stack.getLast() instanceof ArrayPacket parent_array) {
						parent_array.packets.add(value_packet);
					} else {
						throw new IllegalStateException("Expected the parent to be an array");
					}
				}
			}

			return stack.removeFirst();
		}
	}

	private static record ValuePacket(int value) implements Packet {
		@Override
		public int compareTo(Packet other) {
			if (other instanceof ValuePacket other_value) {
				return Integer.compare(value, other_value.value);
			}

			return new ArrayPacket(List.of(this)).compareTo(other);
		}

		@Override
		public String toString() {
			return Integer.toString(value);
		}
	}

	private static record ArrayPacket(List<Packet> packets) implements Packet {
		public ArrayPacket() {
			this(new ArrayList<>());
		}

		@Override
		public int compareTo(Packet other) {
			ArrayPacket other_array;
			if (other instanceof ArrayPacket arr) {
				other_array = arr;
			} else {
				other_array = new ArrayPacket(List.of(other));
			}

			for (int i = 0; i < Math.min(packets.size(), other_array.packets.size()); i++) {
				int result = packets.get(i).compareTo(other_array.packets.get(i));
				if (result != 0) {
					return result;
				}
			}

			return Integer.compare(packets.size(), other_array.packets.size());
		}

		@Override
		public String toString() {
			return packets.stream().map(Packet::toString).collect(Collectors.joining(",", OPEN, CLOSE));
		}
	}
}
