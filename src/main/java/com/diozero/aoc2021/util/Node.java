package com.diozero.aoc2021.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node {
	// Optimise equals for day 15
	private final int id;
	private List<Node> shortestPath = new LinkedList<>();
	private int distance = Integer.MAX_VALUE;
	private final Map<Node, Integer> adjacentNodes = new HashMap<>();

	public Node(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
