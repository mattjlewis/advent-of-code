package com.diozero.aoc.util;

import java.util.concurrent.BlockingQueue;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

public class FunctionUtil {
	private FunctionUtil() {
	}

	public static LongSupplier blockingLongSupplier(BlockingQueue<Long> blockingQueue) {
		return () -> {
			try {
				return blockingQueue.take().longValue();
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted while waiting for new queue element", e);
			}
		};
	}

	public static LongConsumer blockingLongConsumer(BlockingQueue<Long> blockingQueue) {
		return (l) -> {
			try {
				blockingQueue.put(Long.valueOf(l));
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted while inserting queue element", e);
			}
		};
	}
}
