package com.diozero.aoc.algorithm;

public class HaversineScorer {
	private static final double EARTH_RADIUS = 6_372_800; // In meters

	public static int computeCost(Station from, Station to) {
		double delta_lat_rads = Math.toRadians(to.latitude() - from.latitude());
		double delta_lon_rads = Math.toRadians(to.longitude() - from.longitude());
		double from_lat_rads = Math.toRadians(from.latitude());
		double to_lat_rads = Math.toRadians(to.latitude());

		double a = Math.pow(Math.sin(delta_lat_rads / 2), 2)
				+ Math.pow(Math.sin(delta_lon_rads / 2), 2) * Math.cos(from_lat_rads) * Math.cos(to_lat_rads);
		double c = 2 * Math.asin(Math.sqrt(a));

		return (int) Math.round(EARTH_RADIUS * c);
	}
}