package com.diozero.aoc2021.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node {
	private final String name;
	private List<Node> shortestPath = new LinkedList<>();
	private int distance = Integer.MAX_VALUE;
	private final Map<Node, Integer> adjacentNodes = new HashMap<>();

	public Node(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Node> getShortestPath() {
		return shortestPath;
	}

	public void setShortestPath(List<Node> path) {
		this.shortestPath = path;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public Map<Node, Integer> getAdjacentNodes() {
		return adjacentNodes;
	}

	public void addDestination(Node destination, int dist) {
		adjacentNodes.put(destination, Integer.valueOf(dist));
	}
}
