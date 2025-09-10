package com.stardewbombers.shared.entity;

import javafx.geometry.Point2D;

public class Bomb {
	// Bomb entity for bomb placement and explosion timing
	private final String ownerId;
	private final int power;
	private final long explodeAtMs;
	private Point2D position;

	public Bomb(String ownerId, Point2D position, int power, long fuseMs, long nowMs) {
		this.ownerId = ownerId;
		this.position = position;
		this.power = power;
		this.explodeAtMs = nowMs + fuseMs;
	}

	public String getOwnerId() { return ownerId; }
	public int getPower() { return power; }
	public Point2D getPosition() { return position; }
	public long getExplodeAtMs() { return explodeAtMs; }

	public boolean shouldExplode(long nowMs) {
		return nowMs >= explodeAtMs;
	}
}
