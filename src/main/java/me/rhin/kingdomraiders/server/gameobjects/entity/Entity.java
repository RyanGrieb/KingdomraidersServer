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

	public Collider collider;

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

	public void setCollision(double x, double y, int w, int h) {
		this.collider = new Collider(x, y, w, h);
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;

		if (this.collider != null)
			this.collider.setPosition(x, y);
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public boolean isTileCollided() {
		for (int cX = -this.w * 2; cX < this.w * 2; cX += 32)
			for (int cY = -this.h * 2; cY < this.h * 2; cY += 32) {
				TileType tile = Main.getServer().getManager().getMapManager().getTileTypeFromLocation((this.x + cX),
						(this.y + cY));
				if (tile == null) // Out of bounds..
					continue;

				double tileX = ((((int) Math.round(x + cX)) / 32) * 32) + tile.tileCollider.x;
				double tileY = ((((int) Math.round(y + cY)) / 32) * 32) + tile.tileCollider.y;

				if (this.collider.x < (tileX + tile.tileCollider.w) && (this.collider.x + this.collider.w) > tileX)
					if (this.collider.y < (tileY + tile.tileCollider.h)
							&& (this.collider.y + this.collider.h) > tileY) {
						if (this instanceof Projectile) {
							if (tile.projectileCollision)
								return true;

						} else if (tile.entityCollision)
							return true;
					}
			}
		return false;
	}

	// Specificly checks for entity collision
	// TODO: Find a way to make this more efficient.
	public Monster getCollidedEntity() {

		for (Monster m : Main.getServer().getManager().getMonsterManager().monsters) {
			if (this.collider.x < (m.x + m.w) && (this.collider.x + this.collider.w) > m.x)
				if (this.collider.y < (m.y + m.h) && (this.collider.y + this.collider.h) > m.y)
					return m;
		}
		return null;
	}

	public EntityShoot entityShoot() {
		return this.entityShoot;
	}

	public void update() {
		this.entityShoot.update();
	}

	// Collider class
	public class Collider {
		private double xOffset, yOffset;
		public double x, y, w, h;

		public Collider(double x, double y, double w, double h) {
			this.xOffset = x;
			this.yOffset = y;

			this.x = Entity.this.x + this.xOffset;
			this.y = Entity.this.y + this.yOffset;
			this.w = w;
			this.h = h;
		}

		public void setPosition(double x, double y) {
			this.x = x + this.xOffset;
			this.y = y + this.yOffset;
		}
	}

}
