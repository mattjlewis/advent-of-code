package com.diozero.aoc.util;

public class Projectile {
	private int posX;
	private int posY;
	private int velX;
	private int velY;
	private int dragX;
	private int accelY;

	public Projectile(int posX, int posY, int velX, int velY, int dragX, int accelY) {
		this.posX = posX;
		this.posY = posY;
		this.velX = velX;
		this.velY = velY;
		this.dragX = Math.abs(dragX);
		this.accelY = accelY;
	}

	public void reset(int posX, int posY, int velX, int velY) {
		this.posX = posX;
		this.posY = posY;
		this.velX = velX;
		this.velY = velY;
	}

	/**
	 * Return the point at which this projectile will hit the specified target
	 *
	 * @param target the target area
	 * @return the point at which this project hits the target; null if it misses
	 */
	public Point2D hits(Rectangle target) {
		// Iterate until posY < target.bottomRight.posY or posX >
		// target.bottomRight().x()
		int prev_x;
		int prev_y;
		while (true) {
			prev_x = posX;
			prev_y = posY;
			step();
			if (posY < target.bottomRight().y() || posX > target.bottomRight().x()) {
				break;
			}
		}

		// Did we actually hit the target?
		if (prev_x < target.topLeft().x() || prev_x > target.bottomRight().x() || prev_y > target.topLeft().y()
				|| prev_y < target.bottomRight().y()) {
			return null;
		}

		return new Point2D(prev_x, prev_y);
	}

	public void step() {
		posX += velX;
		posY += velY;

		velX += (velX < 0 ? dragX : velX == 0 ? 0 : -dragX);
		velY += accelY;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	@Override
	public String toString() {
		return "Projectile [posX=" + posX + ", posY=" + posY + ", velX=" + velX + ", velY=" + velY + ", dragX=" + dragX
				+ ", accelY=" + accelY + "]";
	}
}
