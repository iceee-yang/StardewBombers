package com.stardewbombers.shared.entity;

import javafx.geometry.Point2D;
import java.util.EnumMap;
import java.util.Map;
import com.stardewbombers.shared.enums.PowerUpType;
import com.stardewbombers.shared.util.GameConfig;

public class Player {
	public enum Status { NORMAL, INVINCIBLE, DEAD }

	private final String id;
	private int health;
	private Point2D position;
	private double speed;
	private Status status;
	private long invincibleUntilMs;
	private final Map<PowerUpType, Integer> powerUps;
	private int bombCount;
	private int bombPower;

	public Player(String id, Point2D spawnPosition) {
		this.id = id;
		this.health = GameConfig.PLAYER_MAX_HP;
		this.position = spawnPosition;
		this.speed = GameConfig.PLAYER_BASE_SPEED;
		this.status = Status.NORMAL;
		this.invincibleUntilMs = 0L;
		this.powerUps = new EnumMap<PowerUpType, Integer>(PowerUpType.class);
		this.bombCount = GameConfig.INITIAL_BOMB_COUNT;
		this.bombPower = GameConfig.INITIAL_BOMB_POWER;
	}

	public String getId() { return id; }
	public int getHealth() { return health; }
	public Point2D getPosition() { return position; }
	public double getSpeed() { return speed; }
	public Status getStatus() { return status; }
	public int getBombCount() { return bombCount; }
	public int getBombPower() { return bombPower; }

	public void setPosition(Point2D newPos) { this.position = newPos; }
	public void setSpeed(double speed) { this.speed = speed; }

	public boolean isAlive() { return status != Status.DEAD; }

	public void tick(long nowMs) {
		if (status == Status.INVINCIBLE && nowMs >= invincibleUntilMs) {
			status = Status.NORMAL;
			invincibleUntilMs = 0L;
		}
	}

	public boolean takeDamage(int amount, long nowMs) {
		if (!isAlive()) return false;
		if (status == Status.INVINCIBLE) return false;
		this.health -= Math.max(0, amount);
		if (this.health <= 0) {
			this.health = 0;
			this.status = Status.DEAD;
			return true;
		}
		this.status = Status.INVINCIBLE;
		this.invincibleUntilMs = nowMs + GameConfig.INVINCIBLE_MS_AFTER_HIT;
		return true;
	}

	public void addPowerUp(PowerUpType type) {
		powerUps.merge(type, 1, Integer::sum);
		switch (type) {
			case SPEED_BOOST:
				this.speed = this.speed + 0.5;
				break;
			case SHIELD:
				this.status = Status.INVINCIBLE;
				this.invincibleUntilMs = System.currentTimeMillis() + GameConfig.INVINCIBLE_MS_AFTER_HIT;
				break;
			case BOMB_COUNT:
				this.bombCount++;
				break;
			case BOMB_POWER:
				this.bombPower++;
				break;
			default:
				break;
		}
	}
}
