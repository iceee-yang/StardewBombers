package com.stardewbombers.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Point2D;
import com.stardewbombers.shared.entity.Bomb;

public class BombComponent {
	// Component for managing bomb placement and explosion timing
	private final String ownerId;
	private int bombCount;
	private boolean unlimited = false;
	private int bombPower;
	private final List<Bomb> activeBombs = new ArrayList<>();
	private long fuseMs = 2000;

	public BombComponent(String ownerId, int bombCount, int bombPower) {
		this.ownerId = ownerId;
		this.bombCount = bombCount;
		this.unlimited = bombCount <= 0;
		this.bombPower = bombPower;
	}

	public int getBombCount() { return bombCount; }
	public int getBombPower() { return bombPower; }
	public boolean isUnlimited() { return unlimited; }
	public void setUnlimited(boolean unlimited) { this.unlimited = unlimited; }
	public void setBombCount(int count) { this.bombCount = count; this.unlimited = count <= 0; }
	public void setBombPower(int power) { this.bombPower = power; }
	public void setFuseMs(long fuseMs) { this.fuseMs = fuseMs; }
	public List<Bomb> getActiveBombs() { return activeBombs; }

	public boolean placeBomb(Point2D position, long nowMs) {
		if (!unlimited && activeBombs.size() >= bombCount) return false;
		activeBombs.add(new Bomb(ownerId, position, bombPower, fuseMs, nowMs));
		return true;
	}

	public List<Bomb> tick(long nowMs) {
		List<Bomb> exploded = new ArrayList<>();
		Iterator<Bomb> it = activeBombs.iterator();
		while (it.hasNext()) {
			Bomb b = it.next();
			if (b.shouldExplode(nowMs)) {
				exploded.add(b);
				it.remove();
			}
		}
		return exploded;
	}
}
