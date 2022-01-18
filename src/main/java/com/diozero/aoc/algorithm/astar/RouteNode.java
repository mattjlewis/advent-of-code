package com.diozero.aoc.algorithm.astar;

import java.util.StringJoiner;

class RouteNode<T extends GraphNode> implements Comparable<RouteNode<T>> {
	private final T current;
	private T previous;
	private double routeScore;
	private double estimatedScore;

	RouteNode(T current) {
		this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	RouteNode(T current, T previous, double routeScore, double estimatedScore) {
		this.current = current;
		this.previous = previous;
		this.routeScore = routeScore;
		this.estimatedScore = estimatedScore;
	}

	T current() {
		return current;
	}

	T getPrevious() {
		return previous;
	}

	void setPrevious(T previous) {
		this.previous = previous;
	}

	double getRouteScore() {
		return routeScore;
	}

	void setRouteScore(double routeScore) {
		this.routeScore = routeScore;
	}

	double getEstimatedScore() {
		return estimatedScore;
	}

	void setEstimatedScore(double estimatedScore) {
		this.estimatedScore = estimatedScore;
	}

	@Override
	public int compareTo(RouteNode<T> other) {
		if (this.estimatedScore > other.estimatedScore) {
			return 1;
		} else if (this.estimatedScore < other.estimatedScore) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", RouteNode.class.getSimpleName() + "[", "]").add("current=" + current)
				.add("previous=" + previous).add("routeScore=" + routeScore).add("estimatedScore=" + estimatedScore)
				.toString();
	}
}