public class Line {
	int x1;
	int y1;
	int x2;
	int y2;

	public Line(String s) {
		String[] parts = s.split(" -> ");

		String[] x1_y1 = parts[0].split(",");
		x1 = Integer.parseInt(x1_y1[0]);
		y1 = Integer.parseInt(x1_y1[1]);

		String[] x2_y2 = parts[1].split(",");
		x2 = Integer.parseInt(x2_y2[0]);
		y2 = Integer.parseInt(x2_y2[1]);
	}

	public boolean isDiagonal() {
		return x1 != x2 && y1 != y2;
	}

	public int getMinX() {
		return Math.min(x1, x2);
	}

	public int getMaxX() {
		return Math.max(x1, x2);
	}

	public int getMinY() {
		return Math.min(y1, y2);
	}

	public int getMaxY() {
		return Math.max(y1, y2);
	}

	@Override
	public String toString() {
		return x1 + "," + y1 + " -> " + x2 + "," + y2;
	}
}
