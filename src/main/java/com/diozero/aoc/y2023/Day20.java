package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.hipparchus.util.ArithmeticUtils;

import com.diozero.aoc.Day;

public class Day20 extends Day {
	private static final Pattern MODULE_PATTERN = Pattern.compile("^([%|&])?(\\w+) -> (.*)$");

	public static void main(String[] args) {
		new Day20().run();
	}

	@Override
	public String name() {
		return "Pulse Propagation";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Module> modules = Files.lines(input).map(Module::parse).toList();
		final Map<String, Module> modules_map = modules.stream()
				.collect(Collectors.toMap(Module::id, Function.identity()));

		for (final Module module : modules) {
			module.destinationIds
					.stream().map(
							id -> modules_map.computeIfAbsent(id,
									key -> new Module(key, ModuleType.OUTPUT, Collections.emptyList(),
											Collections.emptyList(), new AtomicBoolean(), Collections.emptyMap())))
					.forEach(module.destinations::add);

			module.destinations.stream().filter(Module::isConjunction)
					.forEach(m -> m.pulses.put(module.id, Boolean.FALSE));
		}

		var high_pulses = 0;
		var low_pulses = 0;
		for (var i = 0; i < 1000; i++) {
			int[] pulse_counts = pressButton(modules_map.get("broadcaster"), new HashMap<>(), i);
			high_pulses += pulse_counts[0];
			low_pulses += pulse_counts[1];
		}

		return Integer.toString(high_pulses * low_pulses);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Module> modules = Files.lines(input).map(Module::parse).toList();
		final Map<String, Module> modules_map = modules.stream()
				.collect(Collectors.toMap(Module::id, Function.identity()));
		final Map<String, Integer> rx_modules = new HashMap<>();

		for (final Module module : modules) {
			module.destinationIds
					.stream().map(
							id -> modules_map.computeIfAbsent(id,
									key -> new Module(key, ModuleType.OUTPUT, Collections.emptyList(),
											Collections.emptyList(), new AtomicBoolean(), Collections.emptyMap())))
					.forEach(module.destinations::add);

			for (Module destination : module.destinations) {
				if (destination.isConjunction()) {
					destination.pulses.put(module.id, Boolean.FALSE);
				} else if (destination.id.equals("rx")) {
					modules.stream().filter(m -> m.destinationIds.contains(module.id))
							.forEach(m -> rx_modules.put(m.id, Integer.valueOf(0)));
				}
			}
		}

		final Module broadcaster = modules_map.get("broadcaster");
		var i = 1;
		while (true) {
			if (pressButton(broadcaster, rx_modules, i)[2] > 0) {
				return Long.toString(
						rx_modules.values().stream().mapToLong(Integer::intValue).reduce(1, ArithmeticUtils::lcm));
			} else if (rx_modules.isEmpty()) {
				return Long.toString(0);
			}

			i++;
		}
	}

	private static int[] pressButton(Module broadcaster, Map<String, Integer> rxModules, int i) {
		final Queue<Pulse> queue = new LinkedList<>();
		queue.add(new Pulse("button", broadcaster, false));

		int high_pulses = 0;
		int low_pulses = 0;
		while (!queue.isEmpty()) {
			final Pulse pulse = queue.remove();

			if (pulse.status) {
				high_pulses++;
				if (rxModules.containsKey(pulse.from)) {
					rxModules.put(pulse.from, Integer.valueOf(i));
					if (!rxModules.values().stream().anyMatch(value -> value.intValue() <= 0)) {
						return new int[] { 0, 0, 1 };
					}
				}
			} else {
				low_pulses++;
			}

			final Module module = pulse.to;
			switch (module.type) {
			case BROADCASTER:
				module.destinations.forEach(t -> queue.add(new Pulse(pulse.to.id, t, pulse.status)));
				break;
			case CONJUNCTION:
				module.pulses.put(pulse.from, Boolean.valueOf(pulse.status));
				module.destinations.forEach(t -> queue.add(
						new Pulse(pulse.to.id, t, module.pulses.values().stream().anyMatch(b -> !b.booleanValue()))));
				break;
			case FLIPFLOP:
				if (!pulse.status) {
					module.toggle();
					module.destinations.forEach(t -> queue.add(new Pulse(pulse.to.id, t, module.status.get())));
				}
				break;
			default:
			}
		}

		return new int[] { high_pulses, low_pulses, 0 };
	}

	private static enum ModuleType {
		BROADCASTER, FLIPFLOP, CONJUNCTION, OUTPUT;

		public static ModuleType of(String type) {
			if (type == null) {
				return BROADCASTER;
			}
			return switch (type) {
			case "%" -> FLIPFLOP;
			case "&" -> CONJUNCTION;
			default -> throw new IllegalArgumentException("Invalid ModuleType '" + type + "'");
			};
		}
	}

	private static record Module(String id, ModuleType type, List<String> destinationIds, List<Module> destinations,
			AtomicBoolean status, Map<String, Boolean> pulses) {
		public static Module parse(String line) {
			return MODULE_PATTERN
					.matcher(line).results().findFirst().map(mr -> new Module(mr.group(2), ModuleType.of(mr.group(1)),
							List.of(mr.group(3).split(", ")), new ArrayList<>(), new AtomicBoolean(), new HashMap<>()))
					.get();
		}

		public boolean isConjunction() {
			return type == ModuleType.CONJUNCTION;
		}

		public void toggle() {
			status.set(!status.get());
		}
	}

	private static record Pulse(String from, Module to, boolean status) {
		//
	}
}
