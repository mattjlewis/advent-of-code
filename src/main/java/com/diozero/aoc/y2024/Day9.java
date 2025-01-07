package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;

public class Day9 extends Day {
	public static void main(String[] args) {
		new Day9().run();
	}

	@Override
	public String name() {
		return "Disk Fragmenter";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final AtomicInteger block_id = new AtomicInteger(0);
		final AtomicBoolean block = new AtomicBoolean(true);
		final int[] disk = Files.readAllLines(input).get(0).chars().map(Character::getNumericValue).flatMap(i -> {
			final int value = block.getAndSet(!block.get()) ? block_id.getAndIncrement() : -1;
			return IntStream.generate(() -> value).limit(i);
		}).toArray();

		for (int i = disk.length - 1; i >= 0; i--) {
			if (disk[i] != -1) {
				for (int j = 0; j < i; j++) {
					if (disk[j] == -1) {
						disk[j] = disk[i];
						disk[i] = -1;
						break;
					}
				}
			}
		}

		final AtomicInteger position = new AtomicInteger(0);
		return Long.toString(
				Arrays.stream(disk).takeWhile(i -> i != -1).mapToLong(i -> i * position.getAndIncrement()).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final AtomicInteger block_id = new AtomicInteger(0);
		final AtomicInteger position = new AtomicInteger(0);
		final AtomicBoolean block = new AtomicBoolean(true);
		final List<Block> disk = Files.readAllLines(input).get(0).chars().map(Character::getNumericValue)
				.mapToObj(i -> generateBlock(block.getAndSet(!block.get()), block_id, position.getAndAdd(i), i))
				.collect(Collectors.toList());

		for (int i = disk.size() - 1; i >= 0; i--) {
			if (disk.get(i) instanceof File f) {
				boolean moved = false;
				for (int j = 0; j < i && !moved; j++) {
					if (disk.get(j) instanceof Space s && s.length >= f.length) {
						// Move file f at i to space s at j
						disk.set(j, new File(f.id, s.position, f.length));
						// Create space at i where the file was
						disk.set(i, new Space(f.position, f.length));
						// Add remaining space at j+1 if required
						if (s.length > f.length) {
							disk.add(j + 1, new Space(s.position + f.length, s.length - f.length));
						}
						moved = true;
					}
				}
			}
		}

		return Long.toString(
				disk.stream().filter(b -> b instanceof File).map(b -> (File) b).mapToLong(File::checksum).sum());
	}

	private static Block generateBlock(boolean block, AtomicInteger blockId, int position, int i) {
		return block ? new File(blockId.getAndIncrement(), position, i) : new Space(position, i);
	}

	private static interface Block {
		int position();

		int length();
	}

	private static record File(int id, int position, int length) implements Block {
		public long checksum() {
			long sum = 0;
			for (int i = 0; i < length; i++) {
				sum += id * (position + i);
			}
			return sum;
		}
	}

	private static record Space(int position, int length) implements Block {

	}
}
