package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import com.diozero.aoc.Day;

public class Day7 extends Day {
	public static void main(String[] args) {
		new Day7().run();
	}

	@Override
	public String name() {
		return "No Space Left On Device";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Directory root = Directory.build(input);
		if (Logger.isDebugEnabled()) {
			root.print(0);
		}

		// Find all directories with a total size of at most 100,000
		final List<Directory> dirs = new ArrayList<>();
		root.addIfLessThan(dirs, 100_000);

		return Long.toString(dirs.stream().mapToLong(Directory::totalSize).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Directory root = Directory.build(input);

		final long disk_size = 70_000_000;
		final long target_free_space = 30_000_000;
		final long cur_free_space = disk_size - root.totalSize();
		final long to_delete = target_free_space - cur_free_space;

		// Find all outer directories with a total size of at least to_delete
		final List<Directory> dirs = new ArrayList<>();
		root.addIfGreaterThan(dirs, to_delete);

		return Long.toString(dirs.stream().mapToLong(Directory::totalSize).min().orElseThrow());
	}

	private static final record Directory(String name, Directory parent, Map<String, Directory> directories,
			Map<String, File> files) {
		static Directory build(Path input) throws IOException {
			Directory root = null;
			Directory cwd = null;

			for (String line : Files.readAllLines(input)) {
				// Change directory command
				if (line.startsWith("$ cd ")) {
					String path = line.substring(5);
					switch (path) {
					case "/":
						if (root == null) {
							root = Directory.create("/");
						}
						cwd = root;
						break;
					case "..":
						if (cwd == null) {
							throw new IllegalStateException("cwd not set");
						}
						cwd = cwd.parent();
						break;
					default:
						if (cwd == null) {
							throw new IllegalStateException("cwd not set");
						}
						final Directory temp_cwd = cwd;
						cwd = cwd.directories.computeIfAbsent(path, p -> Directory.create(p, temp_cwd));
					}
				} else if (line.equals("$ ls")) {
					// Ignore
				} else {
					if (cwd == null) {
						throw new IllegalStateException("cwd not set");
					}
					// Ignore directory listings as they are added via cd commands
					if (!line.startsWith("dir ")) {
						cwd.addFile(File.parse(line));
					}
				}
			}

			return root;
		}

		public void addIfLessThan(final List<Directory> dirs, final long maxSize) {
			if (totalSize() <= maxSize) {
				dirs.add(this);
			}
			directories.values().forEach(child_dir -> child_dir.addIfLessThan(dirs, maxSize));
		}

		public void addIfGreaterThan(final List<Directory> dirs, final long minSize) {
			if (totalSize() >= minSize) {
				dirs.add(this);
				directories.values().forEach(child_dir -> child_dir.addIfGreaterThan(dirs, minSize));
			}
		}

		static Directory create(String name) {
			return new Directory(name, null, new HashMap<>(), new HashMap<>());
		}

		static Directory create(String name, Directory parent) {
			return new Directory(name, parent, new HashMap<>(), new HashMap<>());
		}

		public void addFile(File f) {
			files.put(f.name, f);
		}

		public long totalSize() {
			return files.values().stream().mapToLong(File::size).sum()
					+ directories.values().stream().mapToLong(Directory::totalSize).sum();
		}

		public void print(int indent) {
			System.out.format("%s- %s (dir, size=%,d)%n", " ".repeat(2 * indent), name, Long.valueOf(totalSize()));
			files.values().stream().sorted(Comparator.comparing(File::name)).forEach(file -> file.print(indent + 1));
			directories.values().stream().sorted(Comparator.comparing(Directory::name))
					.forEach(dir -> dir.print(indent + 1));
		}

		@Override
		public String toString() {
			return "Directory [" + name + (parent == null ? "" : ", parent:" + parent.name) + "]";
		}
	}

	private static final record File(String name, long size) {
		static final File parse(String line) {
			String[] parts = line.split(" ");
			return new File(parts[1], Long.parseLong(parts[0]));
		}

		public void print(int indent) {
			System.out.format("%s- %s (file, size=%,d)%n", " ".repeat(2 * indent), name, Long.valueOf(size()));
		}
	}
}
