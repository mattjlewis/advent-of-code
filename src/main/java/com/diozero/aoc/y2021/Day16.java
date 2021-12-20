package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;

/*-
 * 8A,       00,       4A,       80,       1A,       80,       02,       F4,       78
 * 1000 1010 0000 0000 0100 1010 1000 0000 0001 1010 1000 0000 0000 0010 1111 0100 0111 1000
 *
 * Unpacking:
 * 100 010 1 00000000001
 * Version: 4, type: 2 (op - min), len type id: 1, num sub-packets: 1
 * 001 010 1 00000000001
 * Version: 1, type: 2 (op - min), len type id: 1, num sub-packets: 1
 * 101 010 0 000000000001011
 * Version: 5, type: 2 (op - min), len type id: 0, combined length of 2 sub-packets: 11
 * 110 100 01111 000
 * Version: 6, type: 4 (literal), value: 15
 */
public class Day16 extends AocBase {
	public static void main(String[] args) {
		/*-
		for (int i = 0; i < 15; i++) {
			System.setProperty("sample", i == 0 ? "" : Integer.toString(i));
			new Day16().run();
		}
		System.getProperties().remove("sample");
		*/
		new Day16().run();
	}

	private Packet loadData(Path input) throws IOException {
		final Queue<Integer> bytes = new LinkedList<>();
		Stream.of(Files.lines(input).findFirst().orElseThrow().split("(?<=\\G.{2})"))
				.forEach(b -> bytes.add(Integer.valueOf(b, 16)));

		return readPacket(new BitStream(bytes));
	}

	@Override
	public long part1(Path input) throws IOException {
		Packet packet = loadData(input);
		Logger.debug("root packet: {}", packet);

		return packet.sumVersions();
	}

	@Override
	public long part2(Path input) throws IOException {
		Packet packet = loadData(input);
		Logger.debug("root packet: {}", packet);

		return packet.calculateValue();
	}

	private Packet readPacket(BitStream bs) {
		// Get the packet version
		int version = bs.readBits(3);
		Logger.debug("version: {}", version);

		Packet.Type type = Packet.Type.valueOf(bs.readBits(3));
		Logger.debug("type: {}", type);

		if (type == Packet.Type.LITERAL) {
			Logger.debug("Processing a literal");

			long value = 0;
			while (true) {
				int i = bs.readBits(5);
				value |= i & 0b01111;
				boolean is_end = (i & 0b10000) == 0;
				Logger.debug("i: {}, new_val: {}, is_end: {}, value: {}", i, i & 0b01111, is_end, value);
				if (is_end) {
					Logger.debug("End of literal values");
					break;
				}
				value <<= 4;
			}

			return new Packet(version, value);
		}

		Logger.debug("Processing an operator");

		int length_type_id = bs.readBits(1);
		List<Packet> packets = new ArrayList<>();
		if (length_type_id == 0) {
			/*
			 * If the length type ID is 0, then the next 15 bits are a number that
			 * represents the total length in bits of the sub-packets contained by this
			 * packet
			 */
			int num_sub_packet_bits = bs.readBits(15);
			Logger.debug("length type id 0, combined length of sub-packets: {} bits", num_sub_packet_bits);

			// Can be up to 32768 bits to store sub-packets
			Queue<Integer> sub_queue = new LinkedList<>();
			while (num_sub_packet_bits > 0) {
				int bits_to_read = Math.min(8, num_sub_packet_bits);
				int val = bs.readBits(bits_to_read);
				if (bits_to_read < 8) {
					val <<= (8 - bits_to_read);
				}
				sub_queue.add(Integer.valueOf(val));
				num_sub_packet_bits -= bits_to_read;
			}

			BitStream sub_bs = new BitStream(sub_queue);
			while (sub_bs.remainingBits() > 7) {
				packets.add(readPacket(sub_bs));
			}
		} else {
			/*
			 * If the length type ID is 1, then the next 11 bits are a number that
			 * represents the number of sub-packets immediately contained by this packet.
			 */
			int num_sub_packets = bs.readBits(11);
			Logger.debug("length type id 1, number of sub-packets: {}", num_sub_packets);

			for (int i = 0; i < num_sub_packets; i++) {
				packets.add(readPacket(bs));
			}
		}

		return new Packet(type, version, packets);
	}

	public class Packet {
		public enum Type {
			SUM, PRODUCT, MINIMUM, MAXIMUM, LITERAL, GREATER_THAN, LESS_THAN, EQUAL_TO;

			public static Type valueOf(int index) {
				if (index < 0 || index >= Type.values().length) {
					throw new IllegalArgumentException("Invalid packet type " + index);
				}
				return Type.values()[index];
			}
		}

		private final Type type;
		private final int version;
		// Only if LITERAL
		private long value;
		// Only if not LITERAL
		private List<Packet> packets;

		public Packet(int version, long value) {
			this.type = Type.LITERAL;
			this.version = version;
			this.value = value;
		}

		public Packet(Type type, int version, List<Packet> packets) {
			this.type = type;
			this.version = version;
			this.packets = packets;
		}

		public Type getType() {
			return type;
		}

		public int getVersion() {
			return version;
		}

		public long getValue() {
			return value;
		}

		public List<Packet> getPackets() {
			return packets;
		}

		public long sumVersions() {
			if (packets == null) {
				return version;
			}

			return version + packets.stream().mapToLong(p -> p.sumVersions()).sum();
		}

		public long calculateValue() {
			switch (type) {
			case SUM:
				return packets.stream().mapToLong(p -> p.calculateValue()).sum();
			case PRODUCT:
				return packets.stream().mapToLong(p -> p.calculateValue()).reduce(1L, (a, b) -> a * b);
			case MINIMUM:
				return packets.stream().mapToLong(p -> p.calculateValue()).min().orElseThrow();
			case MAXIMUM:
				return packets.stream().mapToLong(p -> p.calculateValue()).max().orElseThrow();
			case LITERAL:
				return value;
			case GREATER_THAN:
				return packets.get(0).calculateValue() > packets.get(1).calculateValue() ? 1 : 0;
			case LESS_THAN:
				return packets.get(0).calculateValue() < packets.get(1).calculateValue() ? 1 : 0;
			case EQUAL_TO:
				return packets.get(0).calculateValue() == packets.get(1).calculateValue() ? 1 : 0;
			default:
				throw new IllegalArgumentException("Unhandled packet type: " + type);
			}
		}

		@Override
		public String toString() {
			return "Packet [type=" + type + ", version=" + version
					+ (type == Type.LITERAL ? (", value=" + value) : (", packets=" + packets)) + "]";
		}
	}

	public static class BitStream {
		private Queue<Integer> bytes;
		private int bitPos;
		private int currentByte;

		public BitStream(Queue<Integer> bytes) {
			this.bytes = bytes;
			currentByte = bytes.remove().intValue();
			Logger.debug("First byte: 0x{}", Integer.toHexString(currentByte));
			bitPos = 7;
		}

		public int remainingBits() {
			return bitPos + 8 * bytes.size();
		}

		public int readBits(int numBits) {
			int val = 0;

			// TODO Could optimise this by processing up to 8 bits at a time
			for (int i = 0; i < numBits; i++) {
				val = (val << 1) | readBit();
			}

			Logger.trace("read {} bits, value: {}", numBits, val);

			return val;
		}

		private int readBit() {
			if (bitPos == -1) {
				// Lazy load the next byte which must be present
				currentByte = bytes.remove().intValue();
				Logger.trace("Loaded byte: 0x{}", Integer.toHexString(currentByte));
				bitPos = 7;
			}

			return ((currentByte & 1 << bitPos--) != 0) ? 1 : 0;
		}
	}
}
