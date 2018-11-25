package me.rhin.kingdomraiders.server.gameobjects.entity;

import me.rhin.kingdomraiders.server.Main;
import me.rhin.kingdomraiders.server.gameobjects.entity.monster.Monster;
import me.rhin.kingdomraiders.server.gameobjects.entity.projectile.Projectile;
import me.rhin.kingdomraiders.server.gameobjects.tile.TileType;

public class Entity {

	public double x, y;
	public int w, h;

	private EntityShoot entityShoot;
	public EntityStats stats;

	public Entity() {
		this.entityShoot = new EntityShoot();
		this.stats = new EntityStats();
	}

	public void setBounds(double x, double y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	// Checks for collision for tiles
	public boolean isTileCollided() {
		// 4 corners of our entity
		for (int i = 0; i < 4; i++) {
			double x = (i == 0 || i == 2) ? (this.x) : (this.x + this.w);
			double y = (i == 0 || i == 1) ? (this.y) : (this.y + this.h);

			// Get surrounding tiles to check for any big toptiles that are larget than
			// 32x32...
			for (int sTile = -10; sTile < 12; sTile++) {
				TileType tile = Main.getServer().getManager().getMapManager().getTileTypeFromLocation(x + (sTile * 32),
						y + (sTile * 32));
				if (tile == null) // Out of bounds..
					continue;

				if (tile.tileCollider != null) {
					double tileX = (((int) Math.round(x + (sTile * 32))) / 32) * 32;
					double tileY = (((int) Math.round(y + (sTile * 32))) / 32) * 32;

					//TODO: make proper collision detection. (dont use this.x).
					if (this.x > tileX + tile.tileCollider.x && this.x < tileX + tile.tileCollider.w)
						if (this.y > tileY + tile.tileCollider.y && this.y < tileY + tile.tileCollider.h)
							return true;
				}
			}

			// Just check for normal bottom tile collision
			TileType tile = Main.getServer().getManager().getMapManager().getTileTypeFromLocation(x, y);

			if (tile == null || tile.tileCollider != null) // Out of bounds, or if this is a toptile.
				continue;

			if (this instanceof Projectile) {
				if (tile.projectileCollision)
					return true;

			} else if (tile.entityCollision)
				return true;
		}
		return false;
	}

	// Specificly checks for entity collision
	// TODO: Find a way to make this more efficient.
	public Monster getCollidedEntity() {
		// 4 corners of our entity
		for (int i = 0; i < 4; i++) {
			double x = (i == 0 || i == 2) ? (this.x) : (this.x + 32);
			double y = (i == 0 || i == 1) ? (this.y) : (this.y + 32);

			for (Monster m : Main.getServer().getManager().getMonsterManager().monsters) {
				if (x > m.x && x < m.x + m.w)
					if (y > m.y && y < m.y + m.h)
						return m;
			}

		}

		return null;
	}

	public EntityShoot entityShoot() {
		return this.entityShoot;
	}

	public void update() {
		this.entityShoot.update();
	}
}
