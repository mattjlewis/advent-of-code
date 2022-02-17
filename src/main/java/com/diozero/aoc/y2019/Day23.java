package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.TextParser;
import com.diozero.aoc.y2019.util.IntcodeVirtualMachine;

public class Day23 extends Day {
	public static void main(String[] args) {
		new Day23().run();
	}

	@Override
	public String name() {
		return "Category Six";
	}

	@Override
	public String part1(Path input) throws IOException {
		final Network network = new Network(true, 50, TextParser.loadFirstLineAsCsvLongArray(input));
		network.start();

		try {
			network.awaitCompletion();
			network.shutdown();
		} catch (InterruptedException e) {
			Logger.error(e, "Error: {}", e);
		}

		return Long.toString(network.nat.lastPacket.y());
	}

	@Override
	public String part2(Path input) throws IOException {
		final Network network = new Network(false, 50, TextParser.loadFirstLineAsCsvLongArray(input));
		network.start();

		try {
			network.awaitCompletion();
			network.shutdown();
		} catch (InterruptedException e) {
			Logger.error(e, "Error: {}", e);
		}

		return Long.toString(network.nat.previousY.getAsLong());
	}

	private static class Network {
		private final boolean haltOnFirstNatPacket;
		private final NAT nat;
		private final Map<Integer, Computer> computers;
		private final CountDownLatch completionMonitor;
		private final ExecutorService executor;
		private final ScheduledExecutorService scheduler;
		private ScheduledFuture<?> natFuture;

		public Network(boolean haltOnFirstNatPacket, int numComputers, long[] programData) {
			this.haltOnFirstNatPacket = haltOnFirstNatPacket;
			nat = new NAT(this);
			computers = IntStream.range(0, numComputers).mapToObj(i -> new Computer(this, i, programData))
					.collect(Collectors.toMap(c -> Integer.valueOf(c.nic.address), Function.identity()));
			completionMonitor = new CountDownLatch(1);
			executor = Executors.newFixedThreadPool(numComputers);
			scheduler = Executors.newSingleThreadScheduledExecutor();

			if (!haltOnFirstNatPacket) {
				int nat_poll_duration = 10;
				// The NAT will check for idle periods every 10 ms
				natFuture = scheduler.scheduleAtFixedRate(nat, nat_poll_duration, nat_poll_duration,
						TimeUnit.MILLISECONDS);
			}
		}

		public void start() {
			computers.values().forEach(c -> c.start(executor));
		}

		public void send(int destAddress, Packet packet) {
			if (destAddress == 255) {
				nat.accept(packet);
				if (haltOnFirstNatPacket) {
					Logger.debug("Got termination packet: {}", packet);
					completed();
				}
			} else {
				computers.get(Integer.valueOf(destAddress)).nic.transmit(packet);
			}
		}

		public void completed() {
			if (natFuture != null) {
				natFuture.cancel(false);
				natFuture = null;
			}

			scheduler.shutdown();
			try {
				scheduler.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Logger.error(e, "Error: {}", e);
			}

			completionMonitor.countDown();
		}

		public void awaitCompletion() throws InterruptedException {
			completionMonitor.await();
		}

		public void shutdown() throws InterruptedException {
			computers.values().forEach(Computer::shutdown);
			executor.shutdown();
			executor.awaitTermination(1, TimeUnit.SECONDS);
		}
	}

	private static class NAT implements Runnable {
		private final Network network;
		private Packet lastPacket;
		private OptionalLong previousY;

		public NAT(Network network) {
			this.network = network;
			previousY = OptionalLong.empty();
		}

		public synchronized void accept(Packet packet) {
			Logger.debug("NAT packet: {}", packet);
			lastPacket = packet;
		}

		@Override
		public synchronized void run() {
			if (lastPacket == null) {
				return;
			}

			// Is the network idle?
			boolean all_nics_idle = !network.computers.values().parallelStream().anyMatch(c -> !c.nic.idle());
			if (all_nics_idle) {
				Logger.debug("Network is idle");
				network.send(0, lastPacket);
				if (previousY.isPresent() && lastPacket.y() == previousY.getAsLong()) {
					Logger.debug("Received a NAT packet with the same y twice in a row: {}", lastPacket);
					network.completed();
				}

				previousY = OptionalLong.of(lastPacket.y());

				lastPacket = null;
			}
		}
	}

	private static class Computer {
		private final NIC nic;
		private final IntcodeVirtualMachine vm;
		private Future<?> future;

		public Computer(Network network, int address, long[] programData) {
			nic = new NIC(network, address);
			vm = IntcodeVirtualMachine.load(programData, nic, nic);
		}

		public void start(ExecutorService es) {
			future = es.submit(vm);
		}

		public void shutdown() {
			future.cancel(true);
			try {
				vm.awaitTermination();
			} catch (InterruptedException e) {
				Logger.error(e, "Error: {}", e);
			}
		}
	}

	private static class NIC implements LongSupplier, LongConsumer {
		private final Network network;
		private final int address;
		private final Queue<Long> inputBuffer;
		private final Queue<Packet> outputBuffer;
		private boolean sentAddress = false;
		private Packet currentOutputPacket;

		public NIC(Network network, int address) {
			this.network = network;
			this.address = address;
			inputBuffer = new ConcurrentLinkedQueue<>();
			outputBuffer = new ConcurrentLinkedQueue<>();
		}

		public void transmit(Packet packet) {
			outputBuffer.add(packet);
		}

		public boolean idle() {
			return outputBuffer.isEmpty() && currentOutputPacket == null;
		}

		@Override
		public long getAsLong() {
			if (!sentAddress) {
				sentAddress = true;
				return address;
			}

			if (currentOutputPacket != null) {
				long output = currentOutputPacket.y();
				currentOutputPacket = null;
				return output;
			}

			if (outputBuffer.isEmpty()) {
				return -1;
			}

			currentOutputPacket = outputBuffer.poll();
			return currentOutputPacket.x();
		}

		@Override
		public void accept(long value) {
			if (inputBuffer.size() < 2) {
				inputBuffer.add(Long.valueOf(value));
			} else {
				int dest_addr = (int) inputBuffer.poll().longValue();
				long x = inputBuffer.poll().longValue();
				network.send(dest_addr, new Packet(x, value));
			}
		}
	}

	private static record Packet(long x, long y) {
		//
	}
}
