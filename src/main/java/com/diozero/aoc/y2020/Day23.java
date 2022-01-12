package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;
import com.diozero.aoc.util.CircularLinkedList;
import com.diozero.aoc.util.CircularLinkedList.Node;

public class Day23 extends AocBase {
	public static void main(String[] args) {
		new Day23().run();
	}

	@Override
	public String part1(Path input) throws IOException {
		CircularLinkedList<Integer> cups = loadData(input);
		final Map<Integer, Node<Integer>> cups_map = new HashMap<>();
		cups.traverse(node -> cups_map.put(node.value(), node));

		moveCups(cups, cups_map, 100);

		StringBuffer buffer = new StringBuffer();
		Node<Integer> start = cups.get(Integer.valueOf(1));
		for (Node<Integer> cup = start.next(); !cup.value().equals(start.value()); cup = cup.next()) {
			buffer.append(cup.value());
		}

		return buffer.toString();
	}

	@Override
	public String part2(Path input) throws IOException {
		CircularLinkedList<Integer> cups = loadData(input);

		final Map<Integer, Node<Integer>> cups_map = new HashMap<>();
		cups.traverse(node -> cups_map.put(node.value(), node));
		for (int i = cups_map.size() + 1; i <= 1_000_000; i++) {
			Integer value = Integer.valueOf(i);
			cups_map.put(value, cups.add(value));
		}

		long start_ms = System.currentTimeMillis();

		moveCups(cups, cups_map, 10_000_000);

		long duration_ms = System.currentTimeMillis() - start_ms;
		System.out.format("Took %,dms%n", duration_ms);

		Node<Integer> cup = cups.get(Integer.valueOf(1)).next();
		return Long.toString(cup.value().intValue() * cup.next().value().longValue());
	}

	private static void moveCups(CircularLinkedList<Integer> cups, final Map<Integer, Node<Integer>> cupsMap,
			int moves) {
		int max_val = cupsMap.size();

		Node<Integer> current_cup = cups.head();
		for (int move = 1; move <= moves; move++) {
			// Logger.debug("-- move {} --", move);

			/*
			 * The crab picks up the three cups that are immediately clockwise of the
			 * current cup. They are removed from the circle; cup spacing is adjusted as
			 * necessary to maintain the circle.
			 */
			// Logger.debug("cups: {}", getCupsString(cups, current_cup));
			List<Node<Integer>> picked_up_cups = List.of(current_cup.next(), current_cup.next().next(),
					current_cup.next().next().next());
			Node<Integer> last_picked_up_cup = picked_up_cups.get(picked_up_cups.size() - 1);
			current_cup.setNext(last_picked_up_cup.next());
			// Logger.debug("pick up: {}", picked_up_cups);

			/*
			 * The crab selects a destination cup: the cup with a label equal to the current
			 * cup's label minus one. If this would select one of the cups that was just
			 * picked up, the crab will keep subtracting one until it finds a cup that
			 * wasn't just picked up. If at any point in this process the value goes below
			 * the lowest value on any cup's label, it wraps around to the highest value on
			 * any cup's label instead.
			 */
			Integer destination_cup_value = Integer
					.valueOf((current_cup.value().intValue() + max_val - 2) % max_val + 1);
			while (picked_up_cups.contains(cupsMap.get(destination_cup_value))) {
				destination_cup_value = Integer.valueOf((destination_cup_value.intValue() + max_val - 2) % max_val + 1);
			}
			// Logger.debug("destination: {}", destination_cup_value);
			Node<Integer> destination_cup = cupsMap.get(destination_cup_value);

			/*
			 * The crab places the cups it just picked up so that they are immediately
			 * clockwise of the destination cup. They keep the same order as when they were
			 * picked up.
			 */
			last_picked_up_cup.setNext(destination_cup.next());
			destination_cup.setNext(picked_up_cups.get(0));

			/*
			 * The crab selects a new current cup: the cup which is immediately clockwise of
			 * the current cup.
			 */
			current_cup = current_cup.next();
		}

		Logger.debug("-- final --");
		Logger.debug("cups: {}", getCupsString(cups, current_cup.value()));
	}

	private static CircularLinkedList<Integer> loadData(Path input) throws IOException {
		return new CircularLinkedList<>(Files.lines(input).findFirst()
				.map(l -> l.chars().mapToObj(ch -> Integer.valueOf(ch - 48)).toList()).orElseThrow());
	}

	private static String getCupsString(CircularLinkedList<Integer> cups, Integer currentCup) {
		return String.join(" ", cups.toList().stream().map(i -> i.equals(currentCup) ? "(" + i + ")" : i.toString())
				.toArray(String[]::new));
	}
}
