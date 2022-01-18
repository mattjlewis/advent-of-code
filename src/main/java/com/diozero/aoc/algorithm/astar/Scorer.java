package com.diozero.aoc.algorithm.astar;

public interface Scorer<T extends GraphNode> {
	double computeCost(T from, T to);
}