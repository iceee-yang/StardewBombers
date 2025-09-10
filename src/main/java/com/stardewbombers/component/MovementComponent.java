package com.stardewbombers.component;

import javafx.geometry.Point2D;

public class MovementComponent {
	private Point2D position;
	private double speed;

	public MovementComponent(Point2D startPosition, double speed) {
		this.position = startPosition;
		this.speed = speed;
	}

	public Point2D getPosition() { return position; }
	public double getSpeed() { return speed; }
	public void setSpeed(double speed) { this.speed = speed; }

	public void moveUp() { position = position.add(0, -speed); }
	public void moveDown() { position = position.add(0, speed); }
	public void moveLeft() { position = position.add(-speed, 0); }
	public void moveRight() { position = position.add(speed, 0); }
}
